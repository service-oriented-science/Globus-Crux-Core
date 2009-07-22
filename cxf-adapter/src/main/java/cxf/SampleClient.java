package cxf;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.dynamic.DynamicClientFactory;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.ws.addressing.MAPAggregator;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.AttributedURIType;
import org.apache.cxf.ws.addressing.AddressingBuilder;
import org.apache.cxf.ws.addressing.ObjectFactory;
import org.apache.cxf.ws.addressing.ReferenceParametersType;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.ws.addressing.soap.MAPCodec;

import javax.xml.ws.BindingProvider;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.lang.reflect.Method;
import java.util.Map;

import static org.apache.cxf.ws.addressing.JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES;
import org.apache.cxf.BusFactory;

/**
 * @author turtlebender
 */
public class SampleClient {

    private static final ObjectFactory WSA_OBJECT_FACTORY =
            new ObjectFactory();

    private static AddressingProperties createProperties() {
        // get Message Addressing Properties instance
        AddressingBuilder builder = AddressingBuilder.getAddressingBuilder();
        AddressingProperties maps = builder.newAddressingProperties();

        // set MessageID property
        AttributedURIType messageID =
                WSA_OBJECT_FACTORY.createAttributedURIType();
        messageID.setValue("urn:uuid:" + System.currentTimeMillis());
        maps.setMessageID(messageID);
        ReferenceParametersType params = new ReferenceParametersType();

        params.getAny().add(new JAXBElement<String>(new QName("http://counter.com", "CounterKey"), String.class, "counter1"));
        EndpointReferenceType epr = new EndpointReferenceType();
        epr.setReferenceParameters(params);
        maps.setTo(epr);
        return maps;
    }

    public static void main(String[] args) throws Exception {
        BusFactory.getDefaultBus().getOutInterceptors().add(new MAPCodec());
        MAPAggregator agg = new MAPAggregator();
        agg.setAddressingRequired(true);
        BusFactory.getDefaultBus().getOutInterceptors().add(agg);
        DynamicClientFactory dcf = DynamicClientFactory.newInstance();

        Client client = dcf.createClient("http://localhost:9000/helloWorld?wsdl", SampleClient.class.getClassLoader());
        // associate MAPs with request context

        Object customerParam =
                Thread.currentThread().getContextClassLoader().loadClass("com.example.stockquote.TradePriceRequest").newInstance();

        Method setCustIdMethod = customerParam.getClass().getMethod("setTickerSymbol", String.class);
        setCustIdMethod.invoke(customerParam, "CUST-42");
        client.getRequestContext().put(CLIENT_ADDRESSING_PROPERTIES, createProperties());
        Object[] result = client.invoke("GetLastTradePrice", customerParam);
        System.out.println("[0] = " + result[0]);
    }
}
