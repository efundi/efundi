/**
 * AcademicPeriodInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package za.ac.nwu.wsdl.CourseOfferingTracs;

public class AcademicPeriodInfo  implements java.io.Serializable {
    private java.lang.String acadPeriodtTypeKey;

    private java.lang.String acadPeriodValue;

    public AcademicPeriodInfo() {
    }

    public AcademicPeriodInfo(
           java.lang.String acadPeriodtTypeKey,
           java.lang.String acadPeriodValue) {
           this.acadPeriodtTypeKey = acadPeriodtTypeKey;
           this.acadPeriodValue = acadPeriodValue;
    }


    /**
     * Gets the acadPeriodtTypeKey value for this AcademicPeriodInfo.
     * 
     * @return acadPeriodtTypeKey
     */
    public java.lang.String getAcadPeriodtTypeKey() {
        return acadPeriodtTypeKey;
    }


    /**
     * Sets the acadPeriodtTypeKey value for this AcademicPeriodInfo.
     * 
     * @param acadPeriodtTypeKey
     */
    public void setAcadPeriodtTypeKey(java.lang.String acadPeriodtTypeKey) {
        this.acadPeriodtTypeKey = acadPeriodtTypeKey;
    }


    /**
     * Gets the acadPeriodValue value for this AcademicPeriodInfo.
     * 
     * @return acadPeriodValue
     */
    public java.lang.String getAcadPeriodValue() {
        return acadPeriodValue;
    }


    /**
     * Sets the acadPeriodValue value for this AcademicPeriodInfo.
     * 
     * @param acadPeriodValue
     */
    public void setAcadPeriodValue(java.lang.String acadPeriodValue) {
        this.acadPeriodValue = acadPeriodValue;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AcademicPeriodInfo)) return false;
        AcademicPeriodInfo other = (AcademicPeriodInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.acadPeriodtTypeKey==null && other.getAcadPeriodtTypeKey()==null) || 
             (this.acadPeriodtTypeKey!=null &&
              this.acadPeriodtTypeKey.equals(other.getAcadPeriodtTypeKey()))) &&
            ((this.acadPeriodValue==null && other.getAcadPeriodValue()==null) || 
             (this.acadPeriodValue!=null &&
              this.acadPeriodValue.equals(other.getAcadPeriodValue())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAcadPeriodtTypeKey() != null) {
            _hashCode += getAcadPeriodtTypeKey().hashCode();
        }
        if (getAcadPeriodValue() != null) {
            _hashCode += getAcadPeriodValue().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AcademicPeriodInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "AcademicPeriodInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acadPeriodtTypeKey");
        elemField.setXmlName(new javax.xml.namespace.QName("", "acadPeriodtTypeKey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acadPeriodValue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "acadPeriodValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
