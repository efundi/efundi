package za.ac.nwu.jobs;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;

import za.ac.nwu.ldap.LDAPRetrieval;
import za.ac.nwu.model.Lecturer;
import za.ac.nwu.model.Module;
import za.ac.nwu.model.Student;

public class Utility {

    private static final Log log = LogFactory.getLog(Utility.class);

    private static LDAPRetrieval ldapRetrieval;

    private static final String ADMIN = "admin";

    /**
     * A simple method that generates the hashCode by taking the sum of the parameter hashCodes. If
     * a parameter value is null a default number(old nr. 7) is used.
     */
    public static int hashCode(Object... objects) {
        int hash = 0;
        for (Object obj : objects) {
            hash += obj == null ? 7 : obj.hashCode();
        }
        return hash;
    }

    /**
     * A method that tests for String equality.
     */
    public static boolean equals(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }

    /**
     * Returns a unique set of CourseSet Ids. (Examples: WISK, AFNL)
     * @param cmStructures
     * @return
     */
    public static Set<String> getCourseSetIds(final Set<Module> modules) {
        Set<String> courseSetIds = new HashSet<String>();
        for (Module module : modules) {
            courseSetIds.add(module.getCourseCode());
        }
        return courseSetIds;
    }

    /**
      * Returns a unique set of CanonicalCourses ids. (Examples: WISK 111, AFNL 111)
      */
    public static Set<String> getCanonicalCourseIds(final Set<Module> modules) {
        Set<String> canonicalCourseIds = new HashSet<String>();
        for (Module module : modules) {
            canonicalCourseIds.add(module.getCanonicalCourseReference());
        }
        return canonicalCourseIds;
    }

    /**
     * Returns a unique set of Lecturer user names. (Examples: david, john123)
     */
    public static Set<String> getLecturerUserNames(final Module module) {
        Set<String> lecturerUserNames = new HashSet<String>();
        for (Lecturer lecturer : module.getLinkedLecturers()) {
            lecturerUserNames.add(lecturer.getUserName());
        }
        return lecturerUserNames;
    }

    /**
     * Returns a unique set of Student user names. (Examples: stud123, stud456)
     */
    public static Set<String> getStudentUserNames(final Module module) {
        Set<String> studentUserNames = new HashSet<String>();
        for (Student student : module.getLinkedStudents()) {
            studentUserNames.add(student.getUserName());
        }
        return studentUserNames;
    }

    /**
     * Returns a unique set of Lecturers linked to a CourseSet.
     */
    public static Set<String> getLecturersLinkedToCourseSet(final String courseSetId,
            final Set<Module> modules) {
        Set<String> lecturerUserNames = new HashSet<String>();
        for (Module module : modules) {
            if (Utility.equals(courseSetId, module.getCourseCode())) {
                for (Lecturer lecturer : module.getLinkedLecturers()) {
                    lecturerUserNames.add(lecturer.getUserName());
                }
            }
        }
        return lecturerUserNames;
    }

    /**
     * Returns a set of all lecturers linked to the modules.
     */
    public static Set<Lecturer> getAllLecturers(final Set<Module> modules) {
        Set<Lecturer> lecturers = new HashSet<Lecturer>();
        for (Module module : modules) {
            for (Lecturer lecturer : module.getLinkedLecturers()) {
                lecturers.add(lecturer);
            }
        }
        return lecturers;
    }

    /**
     * Returns a set of all students linked to the modules.
     */
    public static Set<Student> getAllStudents(final Set<Module> modules) {
        Set<Student> students = new HashSet<Student>();
        for (Module module : modules) {
            for (Student student : module.getLinkedStudents()) {
                students.add(student);
            }
        }
        return students;
    }

    public static LDAPRetrieval getLDAPRetrieval(
            ServerConfigurationService serverConfigurationService) {
        if (ldapRetrieval == null) {
            ldapRetrieval = new LDAPRetrieval(serverConfigurationService);
        }
        return ldapRetrieval;
    }

    public static boolean isSuperUser(SessionManager sessionManager,
            SecurityService securityService, String quartJobName) throws JobExecutionException {
        boolean superUser = false;
        Session session = sessionManager.getCurrentSession();
        if (session != null) {
            session.setUserEid(ADMIN);
            session.setUserId(ADMIN);
            if (securityService.isSuperUser()) {
                superUser = true;
            }
            else {
                superUser = false;
                log.error(quartJobName + " error - Not a super user.");
                throw new JobExecutionException(quartJobName
                        + " error - Not a super user ("
                        + session.getUserId()
                        + ").");
            }
        }
        else {
            superUser = false;
            log.error(quartJobName + " error - no active session was found.");
            throw new JobExecutionException(quartJobName + " error - no active session was found.");
        }
        return superUser;
    }
}