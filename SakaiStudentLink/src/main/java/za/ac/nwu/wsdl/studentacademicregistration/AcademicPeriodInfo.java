
package za.ac.nwu.wsdl.studentacademicregistration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AcademicPeriodInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AcademicPeriodInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="acadPeriodtTypeKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="acadPeriodValue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AcademicPeriodInfo", propOrder = {
    "acadPeriodtTypeKey",
    "acadPeriodValue"
})
public class AcademicPeriodInfo {

    protected String acadPeriodtTypeKey;
    @XmlElement(required = true)
    protected String acadPeriodValue;

    /**
     * Gets the value of the acadPeriodtTypeKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAcadPeriodtTypeKey() {
        return acadPeriodtTypeKey;
    }

    /**
     * Sets the value of the acadPeriodtTypeKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAcadPeriodtTypeKey(String value) {
        this.acadPeriodtTypeKey = value;
    }

    /**
     * Gets the value of the acadPeriodValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAcadPeriodValue() {
        return acadPeriodValue;
    }

    /**
     * Sets the value of the acadPeriodValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAcadPeriodValue(String value) {
        this.acadPeriodValue = value;
    }

}
