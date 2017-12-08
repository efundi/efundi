/**
 * CourseOfferingTracsService_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package za.ac.nwu.wsdl.CourseOfferingTracs;

public interface CourseOfferingTracsService_PortType extends java.rmi.Remote {
    public za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo insertModuleOfferingTracs(za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo moduleOfferingSearchCriteriaInfo, za.ac.nwu.wsdl.CourseOfferingTracs.ContextInfo contextInfo) throws java.rmi.RemoteException, za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter, za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist, za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed, za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter, za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied;
    public za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo[] getModuleOfferingBySearchCriteria(za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsSearchCriteriaInfo moduleOfferingSearchCriteriaInfo, za.ac.nwu.wsdl.CourseOfferingTracs.ContextInfo contextInfo) throws java.rmi.RemoteException, za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter, za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist, za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed, za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter, za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied;
    public za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo updateModuleOfferingTracs(za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo moduleOfferingTracsInfo, za.ac.nwu.wsdl.CourseOfferingTracs.ContextInfo contextInfo) throws java.rmi.RemoteException, za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter, za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist, za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed, za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter, za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied;
    public za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo[] findModuleOfferingTracs(za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo moduleOfferingTracsInfo, za.ac.nwu.wsdl.CourseOfferingTracs.ContextInfo contextInfo) throws java.rmi.RemoteException, za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter, za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist, za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed, za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter, za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied;
}
