package za.ac.nwu.wsdl.CourseOfferingTracs;

public class CourseOfferingTracsServiceProxy implements za.ac.nwu.wsdl.CourseOfferingTracs.CourseOfferingTracsService_PortType {
  private String _endpoint = null;
  private za.ac.nwu.wsdl.CourseOfferingTracs.CourseOfferingTracsService_PortType courseOfferingTracsService_PortType = null;
  
  public CourseOfferingTracsServiceProxy() {
    _initCourseOfferingTracsServiceProxy();
  }
  
  public CourseOfferingTracsServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initCourseOfferingTracsServiceProxy();
  }
  
  private void _initCourseOfferingTracsServiceProxy() {
    try {
      courseOfferingTracsService_PortType = (new za.ac.nwu.wsdl.CourseOfferingTracs.CourseOfferingTracsService_ServiceLocator()).getCourseOfferingTracsServicePort();
      if (courseOfferingTracsService_PortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)courseOfferingTracsService_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)courseOfferingTracsService_PortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (courseOfferingTracsService_PortType != null)
      ((javax.xml.rpc.Stub)courseOfferingTracsService_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public za.ac.nwu.wsdl.CourseOfferingTracs.CourseOfferingTracsService_PortType getCourseOfferingTracsService_PortType() {
    if (courseOfferingTracsService_PortType == null)
      _initCourseOfferingTracsServiceProxy();
    return courseOfferingTracsService_PortType;
  }
  
  public za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo insertModuleOfferingTracs(za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo moduleOfferingSearchCriteriaInfo, za.ac.nwu.wsdl.CourseOfferingTracs.ContextInfo contextInfo) throws java.rmi.RemoteException, za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter, za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist, za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed, za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter, za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied{
    if (courseOfferingTracsService_PortType == null)
      _initCourseOfferingTracsServiceProxy();
    return courseOfferingTracsService_PortType.insertModuleOfferingTracs(moduleOfferingSearchCriteriaInfo, contextInfo);
  }
  
  public za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo[] getModuleOfferingBySearchCriteria(za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsSearchCriteriaInfo moduleOfferingSearchCriteriaInfo, za.ac.nwu.wsdl.CourseOfferingTracs.ContextInfo contextInfo) throws java.rmi.RemoteException, za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter, za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist, za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed, za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter, za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied{
    if (courseOfferingTracsService_PortType == null)
      _initCourseOfferingTracsServiceProxy();
    return courseOfferingTracsService_PortType.getModuleOfferingBySearchCriteria(moduleOfferingSearchCriteriaInfo, contextInfo);
  }
  
  public za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo updateModuleOfferingTracs(za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo moduleOfferingTracsInfo, za.ac.nwu.wsdl.CourseOfferingTracs.ContextInfo contextInfo) throws java.rmi.RemoteException, za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter, za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist, za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed, za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter, za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied{
    if (courseOfferingTracsService_PortType == null)
      _initCourseOfferingTracsServiceProxy();
    return courseOfferingTracsService_PortType.updateModuleOfferingTracs(moduleOfferingTracsInfo, contextInfo);
  }
  
  public za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo[] findModuleOfferingTracs(za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo moduleOfferingTracsInfo, za.ac.nwu.wsdl.CourseOfferingTracs.ContextInfo contextInfo) throws java.rmi.RemoteException, za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter, za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist, za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed, za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter, za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied{
    if (courseOfferingTracsService_PortType == null)
      _initCourseOfferingTracsServiceProxy();
    return courseOfferingTracsService_PortType.findModuleOfferingTracs(moduleOfferingTracsInfo, contextInfo);
  }
  
  
}