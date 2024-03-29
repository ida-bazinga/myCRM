
package com.haulmont.thesis.crm.core.app.bp.efwebservice1c;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "EfWebService1C", targetNamespace = "http://EfWebService1C", wsdlLocation = "http://vm-srv-zeta/test_efi.bp.83/ws/EfWebService1C?wsdl")
public class EfWebService1C
    extends Service
{

    private final static URL EFWEBSERVICE1C_WSDL_LOCATION;
    private final static WebServiceException EFWEBSERVICE1C_EXCEPTION;
    private final static QName EFWEBSERVICE1C_QNAME = new QName("http://EfWebService1C", "EfWebService1C");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://vm-srv-zeta/test_efi.bp.83/ws/EfWebService1C?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        EFWEBSERVICE1C_WSDL_LOCATION = url;
        EFWEBSERVICE1C_EXCEPTION = e;
    }

    public EfWebService1C() {
        super(__getWsdlLocation(), EFWEBSERVICE1C_QNAME);
    }

    public EfWebService1C(WebServiceFeature... features) {
        super(__getWsdlLocation(), EFWEBSERVICE1C_QNAME, features);
    }

    public EfWebService1C(URL wsdlLocation) {
        super(wsdlLocation, EFWEBSERVICE1C_QNAME);
    }

    public EfWebService1C(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, EFWEBSERVICE1C_QNAME, features);
    }

    public EfWebService1C(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public EfWebService1C(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns EfWebService1CPortType
     */
    @WebEndpoint(name = "EfWebService1CSoap")
    public EfWebService1CPortType getEfWebService1CSoap() {
        return super.getPort(new QName("http://EfWebService1C", "EfWebService1CSoap"), EfWebService1CPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns EfWebService1CPortType
     */
    @WebEndpoint(name = "EfWebService1CSoap")
    public EfWebService1CPortType getEfWebService1CSoap(WebServiceFeature... features) {
        return super.getPort(new QName("http://EfWebService1C", "EfWebService1CSoap"), EfWebService1CPortType.class, features);
    }

    private static URL __getWsdlLocation() {
        if (EFWEBSERVICE1C_EXCEPTION!= null) {
            throw EFWEBSERVICE1C_EXCEPTION;
        }
        return EFWEBSERVICE1C_WSDL_LOCATION;
    }

}
