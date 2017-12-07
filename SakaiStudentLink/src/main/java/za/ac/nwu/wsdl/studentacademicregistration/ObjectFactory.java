
package za.ac.nwu.wsdl.studentacademicregistration;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the za.ac.nwu.wsdl.studentacademicregistration package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _DoesNotExist_QNAME = new QName("http://nwu.ac.za/wsdl/StudentAcademicRegistration", "DoesNotExist");
    private final static QName _MissingParameter_QNAME = new QName("http://nwu.ac.za/wsdl/StudentAcademicRegistration", "MissingParameter");
    private final static QName _GetStudentAcademicRegistrationResponse_QNAME = new QName("http://nwu.ac.za/wsdl/StudentAcademicRegistration", "getStudentAcademicRegistrationResponse");
    private final static QName _GetStudentAcademicRegistrationByModuleOfferingResponse_QNAME = new QName("http://nwu.ac.za/wsdl/StudentAcademicRegistration", "getStudentAcademicRegistrationByModuleOfferingResponse");
    private final static QName _GetStudentAcademicRegistrationByModuleOffering_QNAME = new QName("http://nwu.ac.za/wsdl/StudentAcademicRegistration", "getStudentAcademicRegistrationByModuleOffering");
    private final static QName _InvalidParameter_QNAME = new QName("http://nwu.ac.za/wsdl/StudentAcademicRegistration", "InvalidParameter");
    private final static QName _PermissionDenied_QNAME = new QName("http://nwu.ac.za/wsdl/StudentAcademicRegistration", "PermissionDenied");
    private final static QName _OperationFailed_QNAME = new QName("http://nwu.ac.za/wsdl/StudentAcademicRegistration", "OperationFailed");
    private final static QName _GetStudentAcademicRegistration_QNAME = new QName("http://nwu.ac.za/wsdl/StudentAcademicRegistration", "getStudentAcademicRegistration");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: za.ac.nwu.wsdl.studentacademicregistration
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetStudentAcademicRegistration }
     * 
     */
    public GetStudentAcademicRegistration createGetStudentAcademicRegistration() {
        return new GetStudentAcademicRegistration();
    }

    /**
     * Create an instance of {@link InvalidParameter }
     * 
     */
    public InvalidParameter createInvalidParameter() {
        return new InvalidParameter();
    }

    /**
     * Create an instance of {@link PermissionDenied }
     * 
     */
    public PermissionDenied createPermissionDenied() {
        return new PermissionDenied();
    }

    /**
     * Create an instance of {@link OperationFailed }
     * 
     */
    public OperationFailed createOperationFailed() {
        return new OperationFailed();
    }

    /**
     * Create an instance of {@link MissingParameter }
     * 
     */
    public MissingParameter createMissingParameter() {
        return new MissingParameter();
    }

    /**
     * Create an instance of {@link DoesNotExist }
     * 
     */
    public DoesNotExist createDoesNotExist() {
        return new DoesNotExist();
    }

    /**
     * Create an instance of {@link GetStudentAcademicRegistrationByModuleOfferingResponse }
     * 
     */
    public GetStudentAcademicRegistrationByModuleOfferingResponse createGetStudentAcademicRegistrationByModuleOfferingResponse() {
        return new GetStudentAcademicRegistrationByModuleOfferingResponse();
    }

    /**
     * Create an instance of {@link GetStudentAcademicRegistrationByModuleOffering }
     * 
     */
    public GetStudentAcademicRegistrationByModuleOffering createGetStudentAcademicRegistrationByModuleOffering() {
        return new GetStudentAcademicRegistrationByModuleOffering();
    }

    /**
     * Create an instance of {@link GetStudentAcademicRegistrationResponse }
     * 
     */
    public GetStudentAcademicRegistrationResponse createGetStudentAcademicRegistrationResponse() {
        return new GetStudentAcademicRegistrationResponse();
    }

    /**
     * Create an instance of {@link StudentAcademicRegistrationInfo }
     * 
     */
    public StudentAcademicRegistrationInfo createStudentAcademicRegistrationInfo() {
        return new StudentAcademicRegistrationInfo();
    }

    /**
     * Create an instance of {@link StudentAcademicQualificationInfo }
     * 
     */
    public StudentAcademicQualificationInfo createStudentAcademicQualificationInfo() {
        return new StudentAcademicQualificationInfo();
    }

    /**
     * Create an instance of {@link AttributeInfo }
     * 
     */
    public AttributeInfo createAttributeInfo() {
        return new AttributeInfo();
    }

    /**
     * Create an instance of {@link StudentModuleAcademicInfo }
     * 
     */
    public StudentModuleAcademicInfo createStudentModuleAcademicInfo() {
        return new StudentModuleAcademicInfo();
    }

    /**
     * Create an instance of {@link AcademicPeriodInfo }
     * 
     */
    public AcademicPeriodInfo createAcademicPeriodInfo() {
        return new AcademicPeriodInfo();
    }

    /**
     * Create an instance of {@link ModuleOfferingSearchCriteriaInfo }
     * 
     */
    public ModuleOfferingSearchCriteriaInfo createModuleOfferingSearchCriteriaInfo() {
        return new ModuleOfferingSearchCriteriaInfo();
    }

    /**
     * Create an instance of {@link StudentAcademicModuleRegistrationInfo }
     * 
     */
    public StudentAcademicModuleRegistrationInfo createStudentAcademicModuleRegistrationInfo() {
        return new StudentAcademicModuleRegistrationInfo();
    }

    /**
     * Create an instance of {@link ContextInfo }
     * 
     */
    public ContextInfo createContextInfo() {
        return new ContextInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DoesNotExist }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://nwu.ac.za/wsdl/StudentAcademicRegistration", name = "DoesNotExist")
    public JAXBElement<DoesNotExist> createDoesNotExist(DoesNotExist value) {
        return new JAXBElement<DoesNotExist>(_DoesNotExist_QNAME, DoesNotExist.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MissingParameter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://nwu.ac.za/wsdl/StudentAcademicRegistration", name = "MissingParameter")
    public JAXBElement<MissingParameter> createMissingParameter(MissingParameter value) {
        return new JAXBElement<MissingParameter>(_MissingParameter_QNAME, MissingParameter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStudentAcademicRegistrationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://nwu.ac.za/wsdl/StudentAcademicRegistration", name = "getStudentAcademicRegistrationResponse")
    public JAXBElement<GetStudentAcademicRegistrationResponse> createGetStudentAcademicRegistrationResponse(GetStudentAcademicRegistrationResponse value) {
        return new JAXBElement<GetStudentAcademicRegistrationResponse>(_GetStudentAcademicRegistrationResponse_QNAME, GetStudentAcademicRegistrationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStudentAcademicRegistrationByModuleOfferingResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://nwu.ac.za/wsdl/StudentAcademicRegistration", name = "getStudentAcademicRegistrationByModuleOfferingResponse")
    public JAXBElement<GetStudentAcademicRegistrationByModuleOfferingResponse> createGetStudentAcademicRegistrationByModuleOfferingResponse(GetStudentAcademicRegistrationByModuleOfferingResponse value) {
        return new JAXBElement<GetStudentAcademicRegistrationByModuleOfferingResponse>(_GetStudentAcademicRegistrationByModuleOfferingResponse_QNAME, GetStudentAcademicRegistrationByModuleOfferingResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStudentAcademicRegistrationByModuleOffering }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://nwu.ac.za/wsdl/StudentAcademicRegistration", name = "getStudentAcademicRegistrationByModuleOffering")
    public JAXBElement<GetStudentAcademicRegistrationByModuleOffering> createGetStudentAcademicRegistrationByModuleOffering(GetStudentAcademicRegistrationByModuleOffering value) {
        return new JAXBElement<GetStudentAcademicRegistrationByModuleOffering>(_GetStudentAcademicRegistrationByModuleOffering_QNAME, GetStudentAcademicRegistrationByModuleOffering.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvalidParameter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://nwu.ac.za/wsdl/StudentAcademicRegistration", name = "InvalidParameter")
    public JAXBElement<InvalidParameter> createInvalidParameter(InvalidParameter value) {
        return new JAXBElement<InvalidParameter>(_InvalidParameter_QNAME, InvalidParameter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PermissionDenied }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://nwu.ac.za/wsdl/StudentAcademicRegistration", name = "PermissionDenied")
    public JAXBElement<PermissionDenied> createPermissionDenied(PermissionDenied value) {
        return new JAXBElement<PermissionDenied>(_PermissionDenied_QNAME, PermissionDenied.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperationFailed }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://nwu.ac.za/wsdl/StudentAcademicRegistration", name = "OperationFailed")
    public JAXBElement<OperationFailed> createOperationFailed(OperationFailed value) {
        return new JAXBElement<OperationFailed>(_OperationFailed_QNAME, OperationFailed.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStudentAcademicRegistration }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://nwu.ac.za/wsdl/StudentAcademicRegistration", name = "getStudentAcademicRegistration")
    public JAXBElement<GetStudentAcademicRegistration> createGetStudentAcademicRegistration(GetStudentAcademicRegistration value) {
        return new JAXBElement<GetStudentAcademicRegistration>(_GetStudentAcademicRegistration_QNAME, GetStudentAcademicRegistration.class, null, value);
    }

}
