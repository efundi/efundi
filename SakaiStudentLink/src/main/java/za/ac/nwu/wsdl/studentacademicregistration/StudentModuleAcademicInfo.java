
package za.ac.nwu.wsdl.studentacademicregistration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for studentModuleAcademicInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="studentModuleAcademicInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="academicPeriod" type="{http://nwu.ac.za/wsdl/StudentAcademicRegistration}AcademicPeriodInfo" minOccurs="0"/>
 *         &lt;element name="exemption" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="exemptionTypeKey" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="module" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="moduleDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="moduleDiscontinueDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="moduleDiscontinueReasonTypeKey" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="moduleRegistrationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="moduleTypeTypeKey" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="program" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="qualification" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="repeatingModule" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="univNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "studentModuleAcademicInfo", propOrder = {
    "academicPeriod",
    "exemption",
    "exemptionTypeKey",
    "module",
    "moduleDescription",
    "moduleDiscontinueDate",
    "moduleDiscontinueReasonTypeKey",
    "moduleRegistrationDate",
    "moduleTypeTypeKey",
    "program",
    "qualification",
    "repeatingModule",
    "univNumber"
})
@XmlSeeAlso({
    StudentAcademicModuleRegistrationInfo.class
})
public class StudentModuleAcademicInfo {

    protected AcademicPeriodInfo academicPeriod;
    protected boolean exemption;
    protected boolean exemptionTypeKey;
    protected String module;
    protected String moduleDescription;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar moduleDiscontinueDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar moduleDiscontinueReasonTypeKey;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar moduleRegistrationDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar moduleTypeTypeKey;
    protected String program;
    protected String qualification;
    protected boolean repeatingModule;
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
     * Gets the value of the exemption property.
     * 
     */
    public boolean isExemption() {
        return exemption;
    }

    /**
     * Sets the value of the exemption property.
     * 
     */
    public void setExemption(boolean value) {
        this.exemption = value;
    }

    /**
     * Gets the value of the exemptionTypeKey property.
     * 
     */
    public boolean isExemptionTypeKey() {
        return exemptionTypeKey;
    }

    /**
     * Sets the value of the exemptionTypeKey property.
     * 
     */
    public void setExemptionTypeKey(boolean value) {
        this.exemptionTypeKey = value;
    }

    /**
     * Gets the value of the module property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModule() {
        return module;
    }

    /**
     * Sets the value of the module property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModule(String value) {
        this.module = value;
    }

    /**
     * Gets the value of the moduleDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModuleDescription() {
        return moduleDescription;
    }

    /**
     * Sets the value of the moduleDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModuleDescription(String value) {
        this.moduleDescription = value;
    }

    /**
     * Gets the value of the moduleDiscontinueDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getModuleDiscontinueDate() {
        return moduleDiscontinueDate;
    }

    /**
     * Sets the value of the moduleDiscontinueDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setModuleDiscontinueDate(XMLGregorianCalendar value) {
        this.moduleDiscontinueDate = value;
    }

    /**
     * Gets the value of the moduleDiscontinueReasonTypeKey property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getModuleDiscontinueReasonTypeKey() {
        return moduleDiscontinueReasonTypeKey;
    }

    /**
     * Sets the value of the moduleDiscontinueReasonTypeKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setModuleDiscontinueReasonTypeKey(XMLGregorianCalendar value) {
        this.moduleDiscontinueReasonTypeKey = value;
    }

    /**
     * Gets the value of the moduleRegistrationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getModuleRegistrationDate() {
        return moduleRegistrationDate;
    }

    /**
     * Sets the value of the moduleRegistrationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setModuleRegistrationDate(XMLGregorianCalendar value) {
        this.moduleRegistrationDate = value;
    }

    /**
     * Gets the value of the moduleTypeTypeKey property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getModuleTypeTypeKey() {
        return moduleTypeTypeKey;
    }

    /**
     * Sets the value of the moduleTypeTypeKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setModuleTypeTypeKey(XMLGregorianCalendar value) {
        this.moduleTypeTypeKey = value;
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
     * Gets the value of the repeatingModule property.
     * 
     */
    public boolean isRepeatingModule() {
        return repeatingModule;
    }

    /**
     * Sets the value of the repeatingModule property.
     * 
     */
    public void setRepeatingModule(boolean value) {
        this.repeatingModule = value;
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
