package org.camel.systest;


import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.feature.LoggingFeature;
import org.globus.crux.stateful.StatefulServiceFactory;
import org.globus.crux.stateful.StateAdapter;
import org.globus.crux.stateful.StatefulServiceException;
import org.globus.crux.stateful.internal.DefaultStateAdapter;
import org.globus.crux.stateful.resource.InMemoryResourceManager;
import org.globus.wsrf.properties.GetResourceProperty;

import javax.xml.namespace.QName;

import cxf.AddressingIdInterceptor;
import cxf.SimpleStringResourceIdExtractor;

/**
 * @author turtlebender
 */
public class Server {
    public static void main(String[] args) throws Exception {

//        GetResourcePropertyImpl implementer1 = new GetResourcePropertyImpl();
//        GetRP rp = new GetRP();
//        StateAdapter<String> adapter = new DefaultStateAdapter<String>();
//        Interceptor idInterceptor = createInterceptor(adapter);
//        rp = enhanceTarget(rp, adapter);
//        implementer1.setDelegate(rp);
//        startGetRP(implementer1, idInterceptor);
//        startCounter(new DefaultCounterService(), idInterceptor);




//        WSRFMediator mediator = new WSRFMediator();
//        mediator.getAddressMap().put(
//                "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceProperties/GetResourceProperty",
//                "local://getresourceproperty");
//        mediator.getAddressMap().put("http://www.counter.com/createCounter", "local://counterservice");
//        DummyServiceGenerator dummyServiceGenerator = new DummyServiceGenerator();
//        String address2 = "http://localhost:9027/myapp/myservice";
//        JaxWsServerFactoryBean servFac = new JaxWsServerFactoryBean();
//        servFac.setServiceBean(dummyServiceGenerator.createDummyService(DefaultCounterService.class, GetResourceProperty.class));
//        servFac.setAddress(address2);
//        servFac.getFeatures().add(new WSAddressingFeature());
//        servFac.getFeatures().add(new LoggingFeature());
//        servFac.getInInterceptors().add(mediator);
//        org.apache.cxf.endpoint.Server server = servFac.create();
//        server.start();
    }

    private static void startGetRP(GetResourcePropertyImpl implementor1, Interceptor... interceptors) {
        JaxWsServerFactoryBean servFac = new JaxWsServerFactoryBean();
        servFac.setAddress("local://getresourceproperty");
        servFac.setServiceBean(implementor1);
        servFac.getFeatures().add(new WSAddressingFeature());
        servFac.getFeatures().add(new LoggingFeature());
        for (Interceptor interceptor : interceptors) {
            servFac.getInInterceptors().add(interceptor);
        }
        org.apache.cxf.endpoint.Server server = servFac.create();
        server.start();
    }

//    private static void startCounter(CounterService implementor1, Interceptor... interceptors) {
//        JaxWsServerFactoryBean servFac = new JaxWsServerFactoryBean();
//        servFac.setAddress("local://counterservice");
//        servFac.setServiceBean(implementor1);
//        servFac.getFeatures().add(new WSAddressingFeature());
//        servFac.getFeatures().add(new LoggingFeature());
//        for (Interceptor interceptor : interceptors) {
//            servFac.getInInterceptors().add(interceptor);
//        }
//        org.apache.cxf.endpoint.Server server = servFac.create();
//        server.start();
//    }

    private static GetRP enhanceTarget(GetRP rp, StateAdapter<String> adapter) throws StatefulServiceException {
        StatefulServiceFactory<GetRP, String, Counter> fac =
                new StatefulServiceFactory<GetRP, String, Counter>();
        fac.setTarget(rp);
        fac.setResourceManager(new InMemoryResourceManager<String, Counter>());
        fac.getResourceManager().storeResource("counter1", new Counter());
        fac.setStateAdapter(adapter);
        rp = fac.getStatefulService();
        return rp;
    }

    private static Interceptor createInterceptor(StateAdapter<String> adapter) {
        AddressingIdInterceptor<String> idInterceptor = new AddressingIdInterceptor<String>();
        idInterceptor.setIdExtractor(new SimpleStringResourceIdExtractor(new QName("http://www.counter.com", "id")));
        idInterceptor.setStateAdapter(adapter);
        return idInterceptor;
    }
}
