package cxf;

import org.apache.cxf.ws.addressing.JAXWSAConstants;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;

import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceContext;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.namespace.QName;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author turtlebender
 */
@WebServiceProvider
@ServiceMode(value = Service.Mode.PAYLOAD)
public class StatefulServiceWebProvider implements Provider<Source> {
    @Resource
    private WebServiceContext context;
    private Handler handler;


    public void setHandler(Handler handler) {
        this.handler = handler;
    }


    public Source invoke(Source streamSource) {
        AddressingProperties map =
                (AddressingProperties) context.getMessageContext().get(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_INBOUND);
        if (map.getAction().getValue().equals("http://counter.com/CounterPortType/createCounterRequest")) {
            try {
                Object key = handler.handle(new Object());
                context.getEndpointReference();

                
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
//        List<Object> params = map.getToEndpointReference().getReferenceParameters().getAny();
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static void main(String[] args) throws Exception {
        JaxWsServerFactoryBean svrFactory = new JaxWsServerFactoryBean();
        svrFactory.setServiceClass(StatefulServiceWebProvider.class);
        svrFactory.setWsdlURL("cxf-adapter/stock.wsdl");
        svrFactory.setAddress("http://localhost:9000/helloWorld");

        svrFactory.setServiceName(new QName("http://example.com/stockquote", "StockQuoteService"));
        svrFactory.setServiceBean(new StatefulServiceWebProvider());
        svrFactory.getFeatures().add(new WSAddressingFeature());
        svrFactory.getInInterceptors().add(new LoggingInInterceptor());
        svrFactory.getOutInterceptors().add(new LoggingOutInterceptor());
        svrFactory.create();
    }
}
