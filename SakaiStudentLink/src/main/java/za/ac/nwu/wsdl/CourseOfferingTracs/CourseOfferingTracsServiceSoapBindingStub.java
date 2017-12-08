/**
 * CourseOfferingTracsServiceSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package za.ac.nwu.wsdl.CourseOfferingTracs;

public class CourseOfferingTracsServiceSoapBindingStub extends org.apache.axis.client.Stub implements za.ac.nwu.wsdl.CourseOfferingTracs.CourseOfferingTracsService_PortType {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[4];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getModuleOfferingBySearchCriteria");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "moduleOfferingSearchCriteriaInfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "ModuleOfferingTracsSearchCriteriaInfo"), za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsSearchCriteriaInfo.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "contextInfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "ContextInfo"), za.ac.nwu.wsdl.CourseOfferingTracs.ContextInfo.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "moduleOfferingTracsInfo"));
        oper.setReturnClass(za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "InvalidParameter"),
                      "za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter",
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "InvalidParameter"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "DoesNotExist"),
                      "za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist",
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "DoesNotExist"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "OperationFailed"),
                      "za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed",
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "OperationFailed"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "MissingParameter"),
                      "za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter",
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "MissingParameter"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "PermissionDenied"),
                      "za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied",
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "PermissionDenied"), 
                      true
                     ));
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("insertModuleOfferingTracs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "moduleOfferingSearchCriteriaInfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "moduleOfferingTracsInfo"), za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "contextInfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "ContextInfo"), za.ac.nwu.wsdl.CourseOfferingTracs.ContextInfo.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "moduleOfferingTracsInfo"));
        oper.setReturnClass(za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "InvalidParameter"),
                      "za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter",
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "InvalidParameter"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "DoesNotExist"),
                      "za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist",
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "DoesNotExist"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "OperationFailed"),
                      "za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed",
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "OperationFailed"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "MissingParameter"),
                      "za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter",
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "MissingParameter"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "PermissionDenied"),
                      "za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied",
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "PermissionDenied"), 
                      true
                     ));
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("findModuleOfferingTracs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "moduleOfferingTracsInfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "moduleOfferingTracsInfo"), za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "contextInfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "ContextInfo"), za.ac.nwu.wsdl.CourseOfferingTracs.ContextInfo.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "moduleOfferingTracsInfo"));
        oper.setReturnClass(za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "InvalidParameter"),
                      "za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter",
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "InvalidParameter"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "DoesNotExist"),
                      "za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist",
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "DoesNotExist"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "OperationFailed"),
                      "za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed",
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "OperationFailed"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "MissingParameter"),
                      "za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter",
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "MissingParameter"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "PermissionDenied"),
                      "za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied",
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "PermissionDenied"), 
                      true
                     ));
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("updateModuleOfferingTracs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "moduleOfferingTracsInfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "moduleOfferingTracsInfo"), za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "contextInfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "ContextInfo"), za.ac.nwu.wsdl.CourseOfferingTracs.ContextInfo.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "moduleOfferingTracsInfo"));
        oper.setReturnClass(za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "InvalidParameter"),
                      "za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter",
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "InvalidParameter"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "DoesNotExist"),
                      "za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist",
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "DoesNotExist"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "OperationFailed"),
                      "za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed",
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "OperationFailed"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "MissingParameter"),
                      "za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter",
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "MissingParameter"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "PermissionDenied"),
                      "za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied",
                      new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "PermissionDenied"), 
                      true
                     ));
        _operations[3] = oper;

    }

    public CourseOfferingTracsServiceSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public CourseOfferingTracsServiceSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public CourseOfferingTracsServiceSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "AcademicPeriodInfo");
            cachedSerQNames.add(qName);
            cls = za.ac.nwu.wsdl.CourseOfferingTracs.AcademicPeriodInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "AttributeInfo");
            cachedSerQNames.add(qName);
            cls = za.ac.nwu.wsdl.CourseOfferingTracs.AttributeInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "ContextInfo");
            cachedSerQNames.add(qName);
            cls = za.ac.nwu.wsdl.CourseOfferingTracs.ContextInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "DoesNotExist");
            cachedSerQNames.add(qName);
            cls = za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "InvalidParameter");
            cachedSerQNames.add(qName);
            cls = za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "MetaInfo");
            cachedSerQNames.add(qName);
            cls = za.ac.nwu.wsdl.CourseOfferingTracs.MetaInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "MissingParameter");
            cachedSerQNames.add(qName);
            cls = za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "moduleOfferingTracsInfo");
            cachedSerQNames.add(qName);
            cls = za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "ModuleOfferingTracsSearchCriteriaInfo");
            cachedSerQNames.add(qName);
            cls = za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsSearchCriteriaInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "OperationFailed");
            cachedSerQNames.add(qName);
            cls = za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "PermissionDenied");
            cachedSerQNames.add(qName);
            cls = za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo[] getModuleOfferingBySearchCriteria(za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsSearchCriteriaInfo moduleOfferingSearchCriteriaInfo, za.ac.nwu.wsdl.CourseOfferingTracs.ContextInfo contextInfo) throws java.rmi.RemoteException, za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter, za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist, za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed, za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter, za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "getModuleOfferingBySearchCriteria"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {moduleOfferingSearchCriteriaInfo, contextInfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo[]) org.apache.axis.utils.JavaUtils.convert(_resp, za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter) {
              throw (za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist) {
              throw (za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed) {
              throw (za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter) {
              throw (za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied) {
              throw (za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo insertModuleOfferingTracs(za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo moduleOfferingSearchCriteriaInfo, za.ac.nwu.wsdl.CourseOfferingTracs.ContextInfo contextInfo) throws java.rmi.RemoteException, za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter, za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist, za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed, za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter, za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "insertModuleOfferingTracs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {moduleOfferingSearchCriteriaInfo, contextInfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo) _resp;
            } catch (java.lang.Exception _exception) {
                return (za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo) org.apache.axis.utils.JavaUtils.convert(_resp, za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter) {
              throw (za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist) {
              throw (za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed) {
              throw (za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter) {
              throw (za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied) {
              throw (za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo[] findModuleOfferingTracs(za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo moduleOfferingTracsInfo, za.ac.nwu.wsdl.CourseOfferingTracs.ContextInfo contextInfo) throws java.rmi.RemoteException, za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter, za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist, za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed, za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter, za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "findModuleOfferingTracs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {moduleOfferingTracsInfo, contextInfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo[]) org.apache.axis.utils.JavaUtils.convert(_resp, za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter) {
              throw (za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist) {
              throw (za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed) {
              throw (za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter) {
              throw (za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied) {
              throw (za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo updateModuleOfferingTracs(za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo moduleOfferingTracsInfo, za.ac.nwu.wsdl.CourseOfferingTracs.ContextInfo contextInfo) throws java.rmi.RemoteException, za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter, za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist, za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed, za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter, za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://nwu.ac.za/wsdl/CourseOfferingTracs", "updateModuleOfferingTracs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {moduleOfferingTracsInfo, contextInfo});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo) _resp;
            } catch (java.lang.Exception _exception) {
                return (za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo) org.apache.axis.utils.JavaUtils.convert(_resp, za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter) {
              throw (za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist) {
              throw (za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed) {
              throw (za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter) {
              throw (za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied) {
              throw (za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

}
