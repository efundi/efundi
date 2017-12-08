
package za.ac.nwu.wsdl.studentacademicregistration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getStudentAcademicRegistrationByModuleOffering complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getStudentAcademicRegistrationByModuleOffering">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="moduleOfferingSearchCriteriaInfo" type="{http://nwu.ac.za/wsdl/StudentAcademicRegistration}ModuleOfferingSearchCriteriaInfo"/>
 *         &lt;element name="contextInfo" type="{http://nwu.ac.za/wsdl/StudentAcademicRegistration}ContextInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getStudentAcademicRegistrationByModuleOffering", propOrder = {
    "moduleOfferingSearchCriteriaInfo",
    "contextInfo"
})
public class GetStudentAcademicRegistrationByModuleOffering {

    @XmlElement(required = true)
    protected ModuleOfferingSearchCriteriaInfo moduleOfferingSearchCriteriaInfo;
    protected ContextInfo contextInfo;

    /**
     * Gets the value of the moduleOfferingSearchCriteriaInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ModuleOfferingSearchCriteriaInfo }
     *     
     */
    public ModuleOfferingSearchCriteriaInfo getModuleOfferingSearchCriteriaInfo() {
        return moduleOfferingSearchCriteriaInfo;
    }

    /**
     * Sets the value of the moduleOfferingSearchCriteriaInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ModuleOfferingSearchCriteriaInfo }
     *     
     */
    public void setModuleOfferingSearchCriteriaInfo(ModuleOfferingSearchCriteriaInfo value) {
        this.moduleOfferingSearchCriteriaInfo = value;
    }

    /**
     * Gets the value of the contextInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ContextInfo }
     *     
     */
    public ContextInfo getContextInfo() {
        return contextInfo;
    }

    /**
     * Sets the value of the contextInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContextInfo }
     *     
     */
    public void setContextInfo(ContextInfo value) {
        this.contextInfo = value;
    }

}
