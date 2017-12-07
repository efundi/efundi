/**
 * CourseOfferingTracsService_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package za.ac.nwu.wsdl.CourseOfferingTracs;

public class CourseOfferingTracsService_ServiceLocator extends org.apache.axis.client.Service implements za.ac.nwu.wsdl.CourseOfferingTracs.CourseOfferingTracsService_Service {

    public CourseOfferingTracsService_ServiceLocator() {
    }


    public CourseOfferingTracsService_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CourseOfferingTracsService_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for CourseOfferingTracsServicePort
    private java.lang.String CourseOfferingTracsServicePort_address = "http://143.160.36.41/student-tracs-v3-v_test/CourseOfferingTracsService/OfferingTracsService";

    public java.lang.String getCourseOfferingTracsServicePortAddress() {
        return CourseOfferingTracsServicePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CourseOfferingTracsServicePortWSDDServiceName = "CourseOfferingTracsServicePort";

    public java.lang.String getCourseOfferingTracsServicePortWSDDServiceName() {
        return CourseOfferingTracsServicePortWSDDServiceName;
    }

    public void setCourseOfferingTracsServicePortWSDDServiceName(java.lang.String name) {
        CourseOfferingTracsServicePortWSDDServiceName = name;
    }

    public za.ac.nwu.wsdl.CourseOfferingTracs.CourseOfferingTracsService_PortType getCourseOfferingTracsServicePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CourseOfferingTracsServicePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCourseOfferingTracsServicePort(endpoint);
    }

    public za.ac.nwu.wsdl.CourseOfferingTracs.CourseOfferingTracsService_PortType getCourseOfferingTracsServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            za.ac.nwu.wsdl.CourseOfferingTracs.CourseOfferingTracsServiceSoapBindingStub _stub = new za.ac.nwu.wsdl.CourseOfferingTracs.CourseOfferingTracsServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getCourseOfferingTracsServicePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCourseOfferingTracsServicePortEndpointAddress(java.lang.String address) {
        CourseOfferingTracsServicePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (za.ac.nwu.wsdl.CourseOfferingTracs.CourseOfferingTracsService_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                za.ac.nwu.wsdl.CourseOfferingTracs.CourseOfferingTracsServiceSoapBindingStub _stub = new za.ac.nwu.wsdl.CourseOfferingTracs.CourseOfferingTracsServiceSoapBindingStub(new java.net.URL(CourseOfferingTracsServicePort_address), this);
                _stub.setPortName(getCourseOfferingTracsServicePortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("CourseOfferingTracsServicePort".equals(inputPortName)) {
            return getCourseOfferingTracsServicePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "CourseOfferingTracsService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "CourseOfferingTracsServicePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("CourseOfferingTracsServicePort".equals(portName)) {
            setCourseOfferingTracsServicePortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
