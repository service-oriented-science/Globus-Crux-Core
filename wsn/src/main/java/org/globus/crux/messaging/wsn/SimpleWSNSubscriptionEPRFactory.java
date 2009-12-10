package org.globus.crux.messaging.wsn;

import org.oasis_open.docs.wsn.bw_2.SubscribeCreationFailedFault;
import org.globus.crux.messaging.subscription.Subscription;

import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;
import javax.xml.namespace.QName;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public class SimpleWSNSubscriptionEPRFactory implements WSNSubscriptionEPRFactory {
    W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
    private QName serviceName;
    private QName portName;

    public W3CEndpointReference createSubscriptionEPR(Subscription subscription) throws SubscribeCreationFailedFault {
        builder.serviceName(serviceName);
        builder.endpointName(portName);
        return builder.build();
    }

    public QName getServiceName() {
        return serviceName;
    }

    public void setServiceName(QName serviceName) {
        this.serviceName = serviceName;
    }

    public QName getPortName() {
        return portName;
    }

    public void setPortName(QName portName) {
        this.portName = portName;
    }
}
