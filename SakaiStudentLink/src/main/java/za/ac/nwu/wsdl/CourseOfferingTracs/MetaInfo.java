/**
 * MetaInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package za.ac.nwu.wsdl.CourseOfferingTracs;

public class MetaInfo  implements java.io.Serializable {
    private int versionInd;

    private java.util.Calendar createTime;

    private java.lang.String createId;

    private java.util.Calendar updateTime;

    private java.lang.String updateId;

    private java.lang.String auditFunction;

    public MetaInfo() {
    }

    public MetaInfo(
           int versionInd,
           java.util.Calendar createTime,
           java.lang.String createId,
           java.util.Calendar updateTime,
           java.lang.String updateId,
           java.lang.String auditFunction) {
           this.versionInd = versionInd;
           this.createTime = createTime;
           this.createId = createId;
           this.updateTime = updateTime;
           this.updateId = updateId;
           this.auditFunction = auditFunction;
    }


    /**
     * Gets the versionInd value for this MetaInfo.
     * 
     * @return versionInd
     */
    public int getVersionInd() {
        return versionInd;
    }


    /**
     * Sets the versionInd value for this MetaInfo.
     * 
     * @param versionInd
     */
    public void setVersionInd(int versionInd) {
        this.versionInd = versionInd;
    }


    /**
     * Gets the createTime value for this MetaInfo.
     * 
     * @return createTime
     */
    public java.util.Calendar getCreateTime() {
        return createTime;
    }


    /**
     * Sets the createTime value for this MetaInfo.
     * 
     * @param createTime
     */
    public void setCreateTime(java.util.Calendar createTime) {
        this.createTime = createTime;
    }


    /**
     * Gets the createId value for this MetaInfo.
     * 
     * @return createId
     */
    public java.lang.String getCreateId() {
        return createId;
    }


    /**
     * Sets the createId value for this MetaInfo.
     * 
     * @param createId
     */
    public void setCreateId(java.lang.String createId) {
        this.createId = createId;
    }


    /**
     * Gets the updateTime value for this MetaInfo.
     * 
     * @return updateTime
     */
    public java.util.Calendar getUpdateTime() {
        return updateTime;
    }


    /**
     * Sets the updateTime value for this MetaInfo.
     * 
     * @param updateTime
     */
    public void setUpdateTime(java.util.Calendar updateTime) {
        this.updateTime = updateTime;
    }


    /**
     * Gets the updateId value for this MetaInfo.
     * 
     * @return updateId
     */
    public java.lang.String getUpdateId() {
        return updateId;
    }


    /**
     * Sets the updateId value for this MetaInfo.
     * 
     * @param updateId
     */
    public void setUpdateId(java.lang.String updateId) {
        this.updateId = updateId;
    }


    /**
     * Gets the auditFunction value for this MetaInfo.
     * 
     * @return auditFunction
     */
    public java.lang.String getAuditFunction() {
        return auditFunction;
    }


    /**
     * Sets the auditFunction value for this MetaInfo.
     * 
     * @param auditFunction
     */
    public void setAuditFunction(java.lang.String auditFunction) {
        this.auditFunction = auditFunction;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MetaInfo)) return false;
        MetaInfo other = (MetaInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.versionInd == other.getVersionInd() &&
            ((this.createTime==null && other.getCreateTime()==null) || 
             (this.createTime!=null &&
              this.createTime.equals(other.getCreateTime()))) &&
            ((this.createId==null && other.getCreateId()==null) || 
             (this.createId!=null &&
              this.createId.equals(other.getCreateId()))) &&
            ((this.updateTime==null && other.getUpdateTime()==null) || 
             (this.updateTime!=null &&
              this.updateTime.equals(other.getUpdateTime()))) &&
            ((this.updateId==null && other.getUpdateId()==null) || 
             (this.updateId!=null &&
              this.updateId.equals(other.getUpdateId()))) &&
            ((this.auditFunction==null && other.getAuditFunction()==null) || 
             (this.auditFunction!=null &&
              this.auditFunction.equals(other.getAuditFunction())));
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
        _hashCode += getVersionInd();
        if (getCreateTime() != null) {
            _hashCode += getCreateTime().hashCode();
        }
        if (getCreateId() != null) {
            _hashCode += getCreateId().hashCode();
        }
        if (getUpdateTime() != null) {
            _hashCode += getUpdateTime().hashCode();
        }
        if (getUpdateId() != null) {
            _hashCode += getUpdateId().hashCode();
        }
        if (getAuditFunction() != null) {
            _hashCode += getAuditFunction().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MetaInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "MetaInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("versionInd");
        elemField.setXmlName(new javax.xml.namespace.QName("", "versionInd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("createTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "createTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("createId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "createId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("updateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "updateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("updateId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "updateId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("auditFunction");
        elemField.setXmlName(new javax.xml.namespace.QName("", "auditFunction"));
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
