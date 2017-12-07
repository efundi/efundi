
package za.ac.nwu.wsdl.studentacademicregistration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for studentAcademicModuleRegistrationInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="studentAcademicModuleRegistrationInfo">
 *   &lt;complexContent>
 *     &lt;extension base="{http://nwu.ac.za/wsdl/StudentAcademicRegistration}studentModuleAcademicInfo">
 *       &lt;sequence>
 *         &lt;element name="moduleMethodOfDeliveryTypeKey" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="modulePresentationCategoryTypeKey" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "studentAcademicModuleRegistrationInfo", propOrder = {
    "moduleMethodOfDeliveryTypeKey",
    "modulePresentationCategoryTypeKey"
})
public class StudentAcademicModuleRegistrationInfo
    extends StudentModuleAcademicInfo
{

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar moduleMethodOfDeliveryTypeKey;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar modulePresentationCategoryTypeKey;

    /**
     * Gets the value of the moduleMethodOfDeliveryTypeKey property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getModuleMethodOfDeliveryTypeKey() {
        return moduleMethodOfDeliveryTypeKey;
    }

    /**
     * Sets the value of the moduleMethodOfDeliveryTypeKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setModuleMethodOfDeliveryTypeKey(XMLGregorianCalendar value) {
        this.moduleMethodOfDeliveryTypeKey = value;
    }

    /**
     * Gets the value of the modulePresentationCategoryTypeKey property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getModulePresentationCategoryTypeKey() {
        return modulePresentationCategoryTypeKey;
    }

    /**
     * Sets the value of the modulePresentationCategoryTypeKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setModulePresentationCategoryTypeKey(XMLGregorianCalendar value) {
        this.modulePresentationCategoryTypeKey = value;
    }

}
