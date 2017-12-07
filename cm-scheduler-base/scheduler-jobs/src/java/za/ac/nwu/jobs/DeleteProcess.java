package za.ac.nwu.jobs;

import java.text.MessageFormat;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;

import za.ac.nwu.model.Lecturer;
import za.ac.nwu.model.Module;
import za.ac.nwu.model.Status;
import za.ac.nwu.model.Student;
import za.ac.nwu.sql.DataManager;

public class DeleteProcess {

    private static final Log LOG = LogFactory.getLog(DeleteProcess.class);

    private DataManager dataManager;

    private CourseManagementAdministration cmAdmin;

    private CourseManagementService cmService;

    private ServerConfigurationService serverConfigurationService;

    public DeleteProcess(final DataManager dataManager,
            final CourseManagementAdministration cmAdmin, final CourseManagementService cmService,
            final ServerConfigurationService serverConfigurationService) {
        this.dataManager = dataManager;
        this.cmAdmin = cmAdmin;
        this.cmService = cmService;
        this.serverConfigurationService = serverConfigurationService;
    }

    public void delete(final int year) {
        final Set<Module> deletedModules = dataManager.getModules(year, true, Status.DELETED);
        final Set<Module> insertedAndDoneModules = dataManager.getModules(year, false,
            Status.INSERTED, Status.DONE);
        //Remove in reverse order to avoid foreign key data problems
        deleteCourseOfferingRelatedData(deletedModules, insertedAndDoneModules);
        deleteCanonicalCourses(deletedModules);
        deleteCourseSets(deletedModules);
        //If only deleted data exists for this year, delete the AcademicSession.
        if (insertedAndDoneModules.size() == 0) {
            deleteAcademicSession(year);
        }
        //Update CM Link records
        dataManager.deleteDeletedDataStatus(year);
    }

    private void deleteCourseOfferingRelatedData(final Set<Module> deletedModules,
            final Set<Module> insertedAndDoneModules) {
        for (Module deletedModule : deletedModules) {
            //Lecturers
            for (Lecturer lecturer : deletedModule.getLinkedLecturers()) {
                //Lecturers shouldn't have enrollments. They are linked to the EnrollmentSet.
                //Remove lecturer from EnrollmentSet
                if (cmService.isEnrollmentSetDefined(deletedModule.getEnrollmentSetReference())) {
                    EnrollmentSet enrollmentSet = cmService.getEnrollmentSet(deletedModule
                            .getEnrollmentSetReference());
                    Set<String> instructors = enrollmentSet.getOfficialInstructors();
                    for (String instructor : instructors) {
                        if (Utility.equals(instructor, lecturer.getUserName())) {
                            instructors.remove(instructor);
                            break;
                        }
                    }
                    enrollmentSet.setOfficialInstructors(instructors);
                    cmAdmin.updateEnrollmentSet(enrollmentSet);
                    LOG.info("Removed Lecturer from EnrollmentSet: "
                            + lecturer.getUserName()
                            + " - "
                            + deletedModule.getEnrollmentSetReference());
                }
            }
            //Students' membership/enrollment should only be removed when no same CourseOffering exists for another lecturer.
            if (!insertedAndDoneModules.contains(deletedModule)) {
                //Students
                for (Student student : deletedModule.getLinkedStudents()) {
                    //Section Memberships
                    cmAdmin.removeSectionMembership(student.getUserName(),
                        deletedModule.getCourseOfferingReference());
                    LOG.info("Removed Student Membership from Section: "
                            + student.getUserName()
                            + " - "
                            + deletedModule.getCourseOfferingReference());
                    //Enrollment
                    cmAdmin.removeEnrollment(student.getUserName(),
                        deletedModule.getEnrollmentSetReference());
                    LOG.info("Removed Student from Enrollment: "
                            + student.getUserName()
                            + " - "
                            + deletedModule.getEnrollmentSetReference());
                }
                cmAdmin.removeCourseOfferingFromCourseSet(deletedModule.getCourseCode(),
                    deletedModule.getCourseOfferingReference());
                LOG.info("Removed CourseOffering from CourseSet: "
                        + deletedModule.getCourseOfferingReference()
                        + " - "
                        + deletedModule.getCourseCode());
                //This also removes Sections and EnrollmentSets linked to the CourseOffering.
                cmAdmin.removeCourseOffering(deletedModule.getCourseOfferingReference());
                LOG.info("Removed CourseOffering: " + deletedModule.getCourseOfferingReference());
            }
        }
    }

    private void deleteCanonicalCourses(final Set<Module> deletedModules) {
        for (Module deletedModule : deletedModules) {
            //Canonical Courses are not year driven - must check db for test
            if (!dataManager.isNonDeletedCanonicalCourseExists(deletedModule)) {
                cmAdmin.removeCanonicalCourse(deletedModule.getCanonicalCourseReference());
                LOG.info("Removed CanonicalCourse: " + deletedModule.getCanonicalCourseReference());
            }
        }
    }

    private void deleteCourseSets(final Set<Module> deletedModules) {
        for (Module deletedModule : deletedModules) {
            //Course Sets are not year driven - must check db for test
            if (!dataManager.isNonDeletedCourseSetExists(deletedModule)) {
                cmAdmin.removeCourseSet(deletedModule.getCourseCode());
                LOG.info("Removed CourseSet: " + deletedModule.getCourseCode());
            }
        }
    }

    private void deleteAcademicSession(final int year) {
        String title = MessageFormat.format(serverConfigurationService.getString(
            "nwu.cm.AcademicSession.title", "Year {0,number,####}"), year);
        if (!cmService.isAcademicSessionDefined(title)) {
            cmAdmin.removeAcademicSession(title);
            LOG.info("Removed AcademicSession: " + title);
        }
    }
}