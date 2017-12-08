
package za.ac.nwu.wsdl.studentacademicregistration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for studentAcademicQualificationInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="studentAcademicQualificationInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="academicPeriod" type="{http://nwu.ac.za/wsdl/StudentAcademicRegistration}AcademicPeriodInfo"/>
 *         &lt;element name="academicYears" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="historicYears" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="mainQualification" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="program" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="programDesc" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="programMethodOfDeliveryTypeKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="programPresentationCategoryTypeKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="qualification" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="qualificationDesc" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="qualificationDiscontinueDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="qualificationDiscontinueReasonTypeKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="qualificationFaculty" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="qualificationMethodOfDeliveryTypeKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="qualificationPresentationCategoryTypeKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="qualificationRegistrationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="qualificationResultTypeKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="qualificationSite" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="qualificationStartDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="univNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "studentAcademicQualificationInfo", propOrder = {
    "academicPeriod",
    "academicYears",
    "historicYears",
    "mainQualification",
    "program",
    "programDesc",
    "programMethodOfDeliveryTypeKey",
    "programPresentationCategoryTypeKey",
    "qualification",
    "qualificationDesc",
    "qualificationDiscontinueDate",
    "qualificationDiscontinueReasonTypeKey",
    "qualificationFaculty",
    "qualificationMethodOfDeliveryTypeKey",
    "qualificationPresentationCategoryTypeKey",
    "qualificationRegistrationDate",
    "qualificationResultTypeKey",
    "qualificationSite",
    "qualificationStartDate",
    "univNumber"
})
@XmlSeeAlso({
    StudentAcademicRegistrationInfo.class
})
public class StudentAcademicQualificationInfo {

    @XmlElement(required = true)
    protected AcademicPeriodInfo academicPeriod;
    protected int academicYears;
    protected int historicYears;
    protected boolean mainQualification;
    @XmlElement(required = true)
    protected String program;
    @XmlElement(required = true)
    protected String programDesc;
    protected String programMethodOfDeliveryTypeKey;
    protected String programPresentationCategoryTypeKey;
    @XmlElement(required = true)
    protected String qualification;
    @XmlElement(required = true)
    protected String qualificationDesc;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar qualificationDiscontinueDate;
    protected String qualificationDiscontinueReasonTypeKey;
    protected String qualificationFaculty;
    protected String qualificationMethodOfDeliveryTypeKey;
    protected String qualificationPresentationCategoryTypeKey;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar qualificationRegistrationDate;
    protected String qualificationResultTypeKey;
    protected String qualificationSite;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar qualificationStartDate;
    @XmlElement(required = true)
    protected String univNumber;

    /**
     * Gets the value of the academicPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link AcademicPeriodInfo }
     *     
     */
    public AcademicPeriodInfo getAcademicPeriod() {
        return academicPeriod;
    }

    /**
     * Sets the value of the academicPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link AcademicPeriodInfo }
     *     
     */
    public void setAcademicPeriod(AcademicPeriodInfo value) {
        this.academicPeriod = value;
    }

    /**
     * Gets the value of the academicYears property.
     * 
     */
    public int getAcademicYears() {
        return academicYears;
    }

    /**
     * Sets the value of the academicYears property.
     * 
     */
    public void setAcademicYears(int value) {
        this.academicYears = value;
    }

    /**
     * Gets the value of the historicYears property.
     * 
     */
    public int getHistoricYears() {
        return historicYears;
    }

    /**
     * Sets the value of the historicYears property.
     * 
     */
    public void setHistoricYears(int value) {
        this.historicYears = value;
    }

    /**
     * Gets the value of the mainQualification property.
     * 
     */
    public boolean isMainQualification() {
        return mainQualification;
    }

    /**
     * Sets the value of the mainQualification property.
     * 
     */
    public void setMainQualification(boolean value) {
        this.mainQualification = value;
    }

    /**
     * Gets the value of the program property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProgram() {
        return program;
    }

    /**
     * Sets the value of the program property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProgram(String value) {
        this.program = value;
    }

    /**
     * Gets the value of the programDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProgramDesc() {
        return programDesc;
    }

    /**
     * Sets the value of the programDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProgramDesc(String value) {
        this.programDesc = value;
    }

    /**
     * Gets the value of the programMethodOfDeliveryTypeKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProgramMethodOfDeliveryTypeKey() {
        return programMethodOfDeliveryTypeKey;
    }

    /**
     * Sets the value of the programMethodOfDeliveryTypeKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProgramMethodOfDeliveryTypeKey(String value) {
        this.programMethodOfDeliveryTypeKey = value;
    }

    /**
     * Gets the value of the programPresentationCategoryTypeKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProgramPresentationCategoryTypeKey() {
        return programPresentationCategoryTypeKey;
    }

    /**
     * Sets the value of the programPresentationCategoryTypeKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProgramPresentationCategoryTypeKey(String value) {
        this.programPresentationCategoryTypeKey = value;
    }

    /**
     * Gets the value of the qualification property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQualification() {
        return qualification;
    }

    /**
     * Sets the value of the qualification property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQualification(String value) {
        this.qualification = value;
    }

    /**
     * Gets the value of the qualificationDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQualificationDesc() {
        return qualificationDesc;
    }

    /**
     * Sets the value of the qualificationDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQualificationDesc(String value) {
        this.qualificationDesc = value;
    }

    /**
     * Gets the value of the qualificationDiscontinueDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getQualificationDiscontinueDate() {
        return qualificationDiscontinueDate;
    }

    /**
     * Sets the value of the qualificationDiscontinueDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setQualificationDiscontinueDate(XMLGregorianCalendar value) {
        this.qualificationDiscontinueDate = value;
    }

    /**
     * Gets the value of the qualificationDiscontinueReasonTypeKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQualificationDiscontinueReasonTypeKey() {
        return qualificationDiscontinueReasonTypeKey;
    }

    /**
     * Sets the value of the qualificationDiscontinueReasonTypeKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQualificationDiscontinueReasonTypeKey(String value) {
        this.qualificationDiscontinueReasonTypeKey = value;
    }

    /**
     * Gets the value of the qualificationFaculty property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQualificationFaculty() {
        return qualificationFaculty;
    }

    /**
     * Sets the value of the qualificationFaculty property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQualificationFaculty(String value) {
        this.qualificationFaculty = value;
    }

    /**
     * Gets the value of the qualificationMethodOfDeliveryTypeKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQualificationMethodOfDeliveryTypeKey() {
        return qualificationMethodOfDeliveryTypeKey;
    }

    /**
     * Sets the value of the qualificationMethodOfDeliveryTypeKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQualificationMethodOfDeliveryTypeKey(String value) {
        this.qualificationMethodOfDeliveryTypeKey = value;
    }

    /**
     * Gets the value of the qualificationPresentationCategoryTypeKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQualificationPresentationCategoryTypeKey() {
        return qualificationPresentationCategoryTypeKey;
    }

    /**
     * Sets the value of the qualificationPresentationCategoryTypeKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQualificationPresentationCategoryTypeKey(String value) {
        this.qualificationPresentationCategoryTypeKey = value;
    }

    /**
     * Gets the value of the qualificationRegistrationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getQualificationRegistrationDate() {
        return qualificationRegistrationDate;
    }

    /**
     * Sets the value of the qualificationRegistrationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setQualificationRegistrationDate(XMLGregorianCalendar value) {
        this.qualificationRegistrationDate = value;
    }

    /**
     * Gets the value of the qualificationResultTypeKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQualificationResultTypeKey() {
        return qualificationResultTypeKey;
    }

    /**
     * Sets the value of the qualificationResultTypeKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQualificationResultTypeKey(String value) {
        this.qualificationResultTypeKey = value;
    }

    /**
     * Gets the value of the qualificationSite property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQualificationSite() {
        return qualificationSite;
    }

    /**
     * Sets the value of the qualificationSite property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQualificationSite(String value) {
        this.qualificationSite = value;
    }

    /**
     * Gets the value of the qualificationStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getQualificationStartDate() {
        return qualificationStartDate;
    }

    /**
     * Sets the value of the qualificationStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setQualificationStartDate(XMLGregorianCalendar value) {
        this.qualificationStartDate = value;
    }

    /**
     * Gets the value of the univNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnivNumber() {
        return univNumber;
    }

    /**
     * Sets the value of the univNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnivNumber(String value) {
        this.univNumber = value;
    }

}
