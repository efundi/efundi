/**
 * ContextInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package za.ac.nwu.wsdl.CourseOfferingTracs;

public class ContextInfo  implements java.io.Serializable {
    private java.lang.String authenticatedPrincipalId;

    private java.lang.String principalId;

    private java.util.Calendar currentDate;

    private za.ac.nwu.wsdl.CourseOfferingTracs.AttributeInfo[] attributes;

    public ContextInfo() {
    }

    public ContextInfo(
           java.lang.String authenticatedPrincipalId,
           java.lang.String principalId,
           java.util.Calendar currentDate,
           za.ac.nwu.wsdl.CourseOfferingTracs.AttributeInfo[] attributes) {
           this.authenticatedPrincipalId = authenticatedPrincipalId;
           this.principalId = principalId;
           this.currentDate = currentDate;
           this.attributes = attributes;
    }


    /**
     * Gets the authenticatedPrincipalId value for this ContextInfo.
     * 
     * @return authenticatedPrincipalId
     */
    public java.lang.String getAuthenticatedPrincipalId() {
        return authenticatedPrincipalId;
    }


    /**
     * Sets the authenticatedPrincipalId value for this ContextInfo.
     * 
     * @param authenticatedPrincipalId
     */
    public void setAuthenticatedPrincipalId(java.lang.String authenticatedPrincipalId) {
        this.authenticatedPrincipalId = authenticatedPrincipalId;
    }


    /**
     * Gets the principalId value for this ContextInfo.
     * 
     * @return principalId
     */
    public java.lang.String getPrincipalId() {
        return principalId;
    }


    /**
     * Sets the principalId value for this ContextInfo.
     * 
     * @param principalId
     */
    public void setPrincipalId(java.lang.String principalId) {
        this.principalId = principalId;
    }


    /**
     * Gets the currentDate value for this ContextInfo.
     * 
     * @return currentDate
     */
    public java.util.Calendar getCurrentDate() {
        return currentDate;
    }


    /**
     * Sets the currentDate value for this ContextInfo.
     * 
     * @param currentDate
     */
    public void setCurrentDate(java.util.Calendar currentDate) {
        this.currentDate = currentDate;
    }


    /**
     * Gets the attributes value for this ContextInfo.
     * 
     * @return attributes
     */
    public za.ac.nwu.wsdl.CourseOfferingTracs.AttributeInfo[] getAttributes() {
        return attributes;
    }


    /**
     * Sets the attributes value for this ContextInfo.
     * 
     * @param attributes
     */
    public void setAttributes(za.ac.nwu.wsdl.CourseOfferingTracs.AttributeInfo[] attributes) {
        this.attributes = attributes;
    }

    public za.ac.nwu.wsdl.CourseOfferingTracs.AttributeInfo getAttributes(int i) {
        return this.attributes[i];
    }

    public void setAttributes(int i, za.ac.nwu.wsdl.CourseOfferingTracs.AttributeInfo _value) {
        this.attributes[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ContextInfo)) return false;
        ContextInfo other = (ContextInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.authenticatedPrincipalId==null && other.getAuthenticatedPrincipalId()==null) || 
             (this.authenticatedPrincipalId!=null &&
              this.authenticatedPrincipalId.equals(other.getAuthenticatedPrincipalId()))) &&
            ((this.principalId==null && other.getPrincipalId()==null) || 
             (this.principalId!=null &&
              this.principalId.equals(other.getPrincipalId()))) &&
            ((this.currentDate==null && other.getCurrentDate()==null) || 
             (this.currentDate!=null &&
              this.currentDate.equals(other.getCurrentDate()))) &&
            ((this.attributes==null && other.getAttributes()==null) || 
             (this.attributes!=null &&
              java.util.Arrays.equals(this.attributes, other.getAttributes())));
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
        if (getAuthenticatedPrincipalId() != null) {
            _hashCode += getAuthenticatedPrincipalId().hashCode();
        }
        if (getPrincipalId() != null) {
            _hashCode += getPrincipalId().hashCode();
        }
        if (getCurrentDate() != null) {
            _hashCode += getCurrentDate().hashCode();
        }
        if (getAttributes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAttributes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAttributes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ContextInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "ContextInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authenticatedPrincipalId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "authenticatedPrincipalId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("principalId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "principalId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currentDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("attributes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "attributes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "AttributeInfo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
