package edu.nwu.sakai.studentlink.client;

@SuppressWarnings("serial")
public class ModuleOffering implements java.io.Serializable, Comparable<ModuleOffering> {
    private java.lang.String ksapimotracsid;

    private java.lang.String tracsStudyGuideId;

    private java.lang.String tracsStudyGuidename;

    private java.lang.String tracsStudyGuideCreateDate;

    private java.lang.String moduleSubjectCode;

    private java.lang.String moduleNumber;

    private java.lang.String moduleSite;

    private java.lang.String moduleOrgEnt;

    private java.lang.String methodOfDeliveryTypeKey;

    private java.lang.String modeOfDeliveryTypeKey;

    private java.lang.String languageTypeKey;

    private java.lang.String termTypeKey;

    private java.lang.String startDate;

    private java.lang.String endDate;

    private boolean linkedToLecturer;

    private java.lang.String linkedByLecturer;

    public ModuleOffering() {
    }

    public ModuleOffering(
           java.lang.String ksapimotracsid,
           java.lang.String tracsStudyGuideId,
           java.lang.String tracsStudyGuidename,
           java.lang.String tracsStudyGuideCreateDate,
           java.lang.String moduleSubjectCode,
           java.lang.String moduleNumber,
           java.lang.String moduleSite,
           java.lang.String moduleOrgEnt,
           java.lang.String methodOfDeliveryTypeKey,
           java.lang.String modeOfDeliveryTypeKey,
           java.lang.String languageTypeKey,
           java.lang.String termTypeKey,
           java.lang.String startDate,
           java.lang.String endDate) {
           this.ksapimotracsid = ksapimotracsid;
           this.tracsStudyGuideId = tracsStudyGuideId;
           this.tracsStudyGuidename = tracsStudyGuidename;
           this.tracsStudyGuideCreateDate = tracsStudyGuideCreateDate;
           this.moduleSubjectCode = moduleSubjectCode;
           this.moduleNumber = moduleNumber;
           this.moduleSite = moduleSite;
           this.moduleOrgEnt = moduleOrgEnt;
           this.methodOfDeliveryTypeKey = methodOfDeliveryTypeKey;
           this.modeOfDeliveryTypeKey = modeOfDeliveryTypeKey;
           this.languageTypeKey = languageTypeKey;
           this.termTypeKey = termTypeKey;
           this.startDate = startDate;
           this.endDate = endDate;
    }


    /**
     * Gets the ksapimotracsid value for this ModuleOfferingTracsInfo.
     * 
     * @return ksapimotracsid
     */
    public java.lang.String getKsapimotracsid() {
        return ksapimotracsid;
    }


    /**
     * Sets the ksapimotracsid value for this ModuleOfferingTracsInfo.
     * 
     * @param ksapimotracsid
     */
    public void setKsapimotracsid(java.lang.String ksapimotracsid) {
        this.ksapimotracsid = ksapimotracsid;
    }


    /**
     * Gets the tracsStudyGuideId value for this ModuleOfferingTracsInfo.
     * 
     * @return tracsStudyGuideId
     */
    public java.lang.String getTracsStudyGuideId() {
        return tracsStudyGuideId;
    }


    /**
     * Sets the tracsStudyGuideId value for this ModuleOfferingTracsInfo.
     * 
     * @param tracsStudyGuideId
     */
    public void setTracsStudyGuideId(java.lang.String tracsStudyGuideId) {
        this.tracsStudyGuideId = tracsStudyGuideId;
    }


    /**
     * Gets the tracsStudyGuidename value for this ModuleOfferingTracsInfo.
     * 
     * @return tracsStudyGuidename
     */
    public java.lang.String getTracsStudyGuidename() {
        return tracsStudyGuidename;
    }


    /**
     * Sets the tracsStudyGuidename value for this ModuleOfferingTracsInfo.
     * 
     * @param tracsStudyGuidename
     */
    public void setTracsStudyGuidename(java.lang.String tracsStudyGuidename) {
        this.tracsStudyGuidename = tracsStudyGuidename;
    }


    /**
     * Gets the tracsStudyGuideCreateDate value for this ModuleOfferingTracsInfo.
     * 
     * @return tracsStudyGuideCreateDate
     */
    public java.lang.String getTracsStudyGuideCreateDate() {
        return tracsStudyGuideCreateDate;
    }


    /**
     * Sets the tracsStudyGuideCreateDate value for this ModuleOfferingTracsInfo.
     * 
     * @param tracsStudyGuideCreateDate
     */
    public void setTracsStudyGuideCreateDate(java.lang.String tracsStudyGuideCreateDate) {
        this.tracsStudyGuideCreateDate = tracsStudyGuideCreateDate;
    }


    /**
     * Gets the moduleSubjectCode value for this ModuleOfferingTracsInfo.
     * 
     * @return moduleSubjectCode
     */
    public java.lang.String getModuleSubjectCode() {
        return moduleSubjectCode;
    }


    /**
     * Sets the moduleSubjectCode value for this ModuleOfferingTracsInfo.
     * 
     * @param moduleSubjectCode
     */
    public void setModuleSubjectCode(java.lang.String moduleSubjectCode) {
        this.moduleSubjectCode = moduleSubjectCode;
    }


    /**
     * Gets the moduleNumber value for this ModuleOfferingTracsInfo.
     * 
     * @return moduleNumber
     */
    public java.lang.String getModuleNumber() {
        return moduleNumber;
    }


    /**
     * Sets the moduleNumber value for this ModuleOfferingTracsInfo.
     * 
     * @param moduleNumber
     */
    public void setModuleNumber(java.lang.String moduleNumber) {
        this.moduleNumber = moduleNumber;
    }


    /**
     * Gets the moduleSite value for this ModuleOfferingTracsInfo.
     * 
     * @return moduleSite
     */
    public java.lang.String getModuleSite() {
        return moduleSite;
    }


    /**
     * Sets the moduleSite value for this ModuleOfferingTracsInfo.
     * 
     * @param moduleSite
     */
    public void setModuleSite(java.lang.String moduleSite) {
        this.moduleSite = moduleSite;
    }


    /**
     * Gets the moduleOrgEnt value for this ModuleOfferingTracsInfo.
     * 
     * @return moduleOrgEnt
     */
    public java.lang.String getModuleOrgEnt() {
        return moduleOrgEnt;
    }


    /**
     * Sets the moduleOrgEnt value for this ModuleOfferingTracsInfo.
     * 
     * @param moduleOrgEnt
     */
    public void setModuleOrgEnt(java.lang.String moduleOrgEnt) {
        this.moduleOrgEnt = moduleOrgEnt;
    }


    /**
     * Gets the methodOfDeliveryTypeKey value for this ModuleOfferingTracsInfo.
     * 
     * @return methodOfDeliveryTypeKey
     */
    public java.lang.String getMethodOfDeliveryTypeKey() {
        return methodOfDeliveryTypeKey;
    }


    /**
     * Sets the methodOfDeliveryTypeKey value for this ModuleOfferingTracsInfo.
     * 
     * @param methodOfDeliveryTypeKey
     */
    public void setMethodOfDeliveryTypeKey(java.lang.String methodOfDeliveryTypeKey) {
        this.methodOfDeliveryTypeKey = methodOfDeliveryTypeKey;
    }


    /**
     * Gets the modeOfDeliveryTypeKey value for this ModuleOfferingTracsInfo.
     * 
     * @return modeOfDeliveryTypeKey
     */
    public java.lang.String getModeOfDeliveryTypeKey() {
        return modeOfDeliveryTypeKey;
    }


    /**
     * Sets the modeOfDeliveryTypeKey value for this ModuleOfferingTracsInfo.
     * 
     * @param modeOfDeliveryTypeKey
     */
    public void setModeOfDeliveryTypeKey(java.lang.String modeOfDeliveryTypeKey) {
        this.modeOfDeliveryTypeKey = modeOfDeliveryTypeKey;
    }


    /**
     * Gets the languageTypeKey value for this ModuleOfferingTracsInfo.
     * 
     * @return languageTypeKey
     */
    public java.lang.String getLanguageTypeKey() {
        return languageTypeKey;
    }


    /**
     * Sets the languageTypeKey value for this ModuleOfferingTracsInfo.
     * 
     * @param languageTypeKey
     */
    public void setLanguageTypeKey(java.lang.String languageTypeKey) {
        this.languageTypeKey = languageTypeKey;
    }


    /**
     * Gets the termTypeKey value for this ModuleOfferingTracsInfo.
     * 
     * @return termTypeKey
     */
    public java.lang.String getTermTypeKey() {
        return termTypeKey;
    }


    /**
     * Sets the termTypeKey value for this ModuleOfferingTracsInfo.
     * 
     * @param termTypeKey
     */
    public void setTermTypeKey(java.lang.String termTypeKey) {
        this.termTypeKey = termTypeKey;
    }


    /**
     * Gets the startDate value for this ModuleOfferingTracsInfo.
     * 
     * @return startDate
     */
    public java.lang.String getStartDate() {
        return startDate;
    }


    /**
     * Sets the startDate value for this ModuleOfferingTracsInfo.
     * 
     * @param startDate
     */
    public void setStartDate(java.lang.String startDate) {
        this.startDate = startDate;
    }


    /**
     * Gets the endDate value for this ModuleOfferingTracsInfo.
     * 
     * @return endDate
     */
    public java.lang.String getEndDate() {
        return endDate;
    }


    /**
     * Sets the endDate value for this ModuleOfferingTracsInfo.
     * 
     * @param endDate
     */
    public void setEndDate(java.lang.String endDate) {
        this.endDate = endDate;
    }

    public boolean isLinkedToLecturer() {
        return linkedToLecturer;
    }

    public void setLinkedToLecturer(boolean linkedToLecturer) {
        this.linkedToLecturer = linkedToLecturer;
    }

    public String toString() {
        return getCanonicalCourseReference() + " " + Campus.getCampus(getModuleSite()).getCode();
    }

    public int compareTo(ModuleOffering o) {
        if (o == null) {
            return 1;
        }
        return this.toString().compareTo(o.toString());
    }

    /**
     * Course Management Helper method - CanonicalCourse eid example: WISK 111 1-C
     */
    public String getCanonicalCourseReference() {
        return moduleSubjectCode + " " + moduleNumber + getMODAndPresentCatStr();
    }

    /**
     * Course Management Helper method - CourseOffering eid example: WISK 111 1-C P 2011
     */
    public String getCourseOfferingReference(int year) {
        return getCanonicalCourseReference()
                + " " 
                + Campus.getCampus(getModuleSite()).getCode()
                + " "
                + year;
    }

    /**
     * Course Management Helper method - EnrollmentSet eid example: WISK 111 1-C P 2011 ES
     */
    public String getEnrollmentSetReference(int year) {
        return getCourseOfferingReference(year) + " ES";
    }

    /**
     * Course Management Helper method - example: 1-C
     */
    private String getMODAndPresentCatStr(){    	
    	StringBuilder strValue = new StringBuilder();    	
    	String methodOfDeliveryCode = getMethodOfDeliveryCode();
		String presentationCategoryChar = getPresentationCategoryCode(); 	
    	if(methodOfDeliveryCode != null  && presentationCategoryChar != null){
    		strValue.append(" " + methodOfDeliveryCode + "-" + presentationCategoryChar);
    	} else if(methodOfDeliveryCode != null){
    		strValue.append(" " + methodOfDeliveryCode);    		
    	}
    	else if(presentationCategoryChar != null){
    		strValue.append(" " + presentationCategoryChar);    		
    	}
    	return strValue.toString();
    }
    
    public String getMethodOfDeliveryCode(){
    	String modCd = getMethodOfDeliveryTypeKey();
        if (modCd != null && modCd.length() != 0){   
        	modCd = modCd.replace("vss.code.ENROLCAT.", "");
        	return modCd.substring(0, modCd.indexOf("."));
    	}
    	return null;
    }    
    
    public String getMethodOfDeliveryCodeParam(){
    	String modCd = getMethodOfDeliveryTypeKey();
        if (modCd != null && modCd.length() != 0){   
        	modCd = modCd.replace("vss.code.ENROLCAT.", "");
        	return "vss.code.ENROLCAT." + modCd.substring(0, modCd.indexOf("."));
    	}
    	return null;
    }    
    
    public String getMethodOfDeliveryName(){
    	String modName = getMethodOfDeliveryTypeKey();
        if (modName != null && modName.length() != 0){    
        	modName = modName.replace("vss.code.ENROLCAT.", "");        	
        	return modName.substring(modName.indexOf(".")+1, modName.length());
    	}
    	return null;
    }    
    
    public String getPresentationCategoryCode(){
    	String modCd = getModeOfDeliveryTypeKey();
        if (modCd != null && modCd.length() != 0){  
        	modCd = modCd.replace("vss.code.PRESENTCAT.", "");
        	return modCd.substring(0, modCd.indexOf("."));
    	}
    	return null;
    }     
    
    public String getModeOfDeliveryCodeParam(){
    	String modCd = getModeOfDeliveryTypeKey();
        if (modCd != null && modCd.length() != 0){  
        	modCd = modCd.replace("vss.code.PRESENTCAT.", "");
        	return "vss.code.PRESENTCAT." + modCd.substring(0, modCd.indexOf("."));
    	}
    	return null;
    }    
    
    public String getPresentationCategoryName(){
    	String modCd = getModeOfDeliveryTypeKey();
        if (modCd != null && modCd.length() != 0){  
        	int index = modCd.lastIndexOf(".");	        
        	return modCd.substring(index+1, modCd.length());
    	}
    	return null;
    }      

    public String getModulePresentingEngCampusName(){
    	String site = getModuleSite();
        if (site != null && site.length() != 0){  
        	return Campus.getCampus(site).getENGName();
    	}
    	return null;
    }  
    
    public String getCourseLevel(){
    	String courseLevel = getModuleNumber();
        if (courseLevel != null && courseLevel.length() != 0){  
        	return courseLevel.substring(0, 1);
    	}
    	return null;
    }
    public String getCourseModule(){
    	String courseModule = getModuleNumber();
        if (courseModule != null && courseModule.length() != 0){  
        	return courseModule.substring(1, 3);
    	}
    	return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (obj instanceof ModuleOffering) {
        	ModuleOffering other = (ModuleOffering) obj;
            return Utility.equals(moduleSubjectCode, other.moduleSubjectCode)
                    && Utility.equals(moduleNumber, other.moduleNumber)
                    && Utility.equals(moduleSite, other.moduleSite)
                    && Utility.equals(getMethodOfDeliveryCodeParam(), other.methodOfDeliveryTypeKey)
                    && Utility.equals(getModeOfDeliveryCodeParam(), other.modeOfDeliveryTypeKey);
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Utility.hashCode(moduleSubjectCode,moduleNumber, moduleSite, methodOfDeliveryTypeKey, modeOfDeliveryTypeKey);
    }

    public java.lang.String getLinkedByLecturer() {
        return linkedByLecturer;
    }

    public void setLinkedByLecturer(java.lang.String linkedByLecturer) {
        this.linkedByLecturer = linkedByLecturer;
    }
}
