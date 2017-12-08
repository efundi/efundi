package za.ac.nwu.model;

import java.util.HashSet;
import java.util.Set;

import za.ac.nwu.jobs.Utility;

public class Module {

    private String courseCode;

    private String courseLevel;

    private String courseModule;
    
    private Campus campus;

    private int year;

    private Set<Student> linkedStudents = new HashSet<Student>();

    private Set<Lecturer> linkedLecturers = new HashSet<Lecturer>();
    
    private String methodOfDeliveryCode;

    private String presentationCategoryCode;

    public Module(String courseCode, String courseLevel, String courseModule, Campus campus,
            int year, String methodOfDeliveryCode, String presentationCategoryCode) {
        this.courseCode = courseCode;
        this.courseLevel = courseLevel;
        this.courseModule = courseModule;
        this.campus = campus;
        this.year = year;
        this.methodOfDeliveryCode = methodOfDeliveryCode;
        this.presentationCategoryCode = presentationCategoryCode;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseLevel() {
        return courseLevel;
    }

    public void setCourseLevel(String courseLevel) {
        this.courseLevel = courseLevel;
    }

    public String getCourseModule() {
        return courseModule;
    }

    public void setCourseModule(String courseModule) {
        this.courseModule = courseModule;
    }
    
    public String getModuleNumber(){
    	return courseLevel + courseModule;
    }

    public Campus getCampus() {
        return campus;
    }

    public void setCampus(Campus campus) {
        this.campus = campus;
    }

    public int getYear() {
        return year;
    }        

	public String getMethodOfDeliveryCode() {
		return methodOfDeliveryCode;
	}

	public void setMethodOfDeliveryCode(String methodOfDeliveryCode) {
		this.methodOfDeliveryCode = methodOfDeliveryCode;
	}

	public String getPresentationCategoryCode() {
		return presentationCategoryCode;
	}

	public void setPresentationCategoryCode(String presentationCategoryCode) {
		this.presentationCategoryCode = presentationCategoryCode;
	}

	public Set<Student> getLinkedStudents() {
        return linkedStudents;
    }

    public void setLinkedStudents(Set<Student> linkedStudents) {
        this.linkedStudents = linkedStudents;
    }

    public Set<Lecturer> getLinkedLecturers() {
        return linkedLecturers;
    }

    public void setLinkedLecturers(Set<Lecturer> linkedLecturers) {
        this.linkedLecturers = linkedLecturers;
    }

    public void addLinkedLecturer(Lecturer lecturer) {
        this.linkedLecturers.add(lecturer);
    }

    /**
     * Course Management Helper method - CanonicalCourse eid example: WISK 111
     */
    public String getCanonicalCourseReference() {
        return courseCode + " " + courseLevel + courseModule + getMODAndPresentCatStr();
    }

    /**
     * Course Management Helper method - CourseOffering eid example: WISK 111 P 2011
     */
    public String getCourseOfferingReference() {
        return getCanonicalCourseReference() + " " + campus.getCode() + " " + year;
    }

    /**
     * Course Management Helper method - EnrollmentSet eid example: WISK 111 P 2011 ES
     */
    public String getEnrollmentSetReference() {
        return getCourseOfferingReference() + " ES";
    }
    
    private String getMODAndPresentCatStr(){    	
    	StringBuilder strValue = new StringBuilder();    	
    	String methodOfDeliveryCode = getMethodOfDeliveryShortCode();
		String presentationCategoryCode = getPresentationCategoryShortCode(); 	
    	if(methodOfDeliveryCode != null  && presentationCategoryCode != null){
    		strValue.append(" " + methodOfDeliveryCode + "-" + presentationCategoryCode);
    	} else if(methodOfDeliveryCode != null){
    		strValue.append(" " + methodOfDeliveryCode);    		
    	}
    	else if(presentationCategoryCode != null){
    		strValue.append(" " + presentationCategoryCode);    		
    	}
    	return strValue.toString();
    }    

    private String getMethodOfDeliveryShortCode(){
    	if (this.methodOfDeliveryCode != null && this.methodOfDeliveryCode.length() != 0){
        	String modCd = this.methodOfDeliveryCode.replace("vss.code.ENROLCAT.", "");
    		if(modCd.contains(".")){
    	    	int lastIndex = modCd.lastIndexOf(".");
    			return modCd.substring(0, lastIndex);
    		}
    		return modCd;
    	}
    	return null;
    }       

    private String getPresentationCategoryShortCode(){
    	if (this.presentationCategoryCode != null && this.presentationCategoryCode.length() != 0){
        	String presCd = this.presentationCategoryCode.replace("vss.code.PRESENTCAT.", "");
    		if(presCd.contains(".")){
    	    	int lastIndex = presCd.lastIndexOf(".");
    			return presCd.substring(0, lastIndex);
    		}
    		return presCd;
    	}
    	return null;
    }   

    @Override
    public int hashCode() {
        return Utility.hashCode(courseCode, courseLevel, courseModule, campus, year, methodOfDeliveryCode, presentationCategoryCode);        
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (obj instanceof Module) {
            Module other = (Module) obj;
            return Utility.equals(courseCode, other.courseCode)
                    && Utility.equals(courseLevel, other.courseLevel)
                    && Utility.equals(courseModule, other.courseModule)
                    && campus == other.campus
                    && year == other.year
                    && Utility.equals(methodOfDeliveryCode, other.methodOfDeliveryCode)
                    && Utility.equals(presentationCategoryCode, other.presentationCategoryCode);
        }
        else {
            return false;
        }
    }
}
