package org.globus.crux.messaging.wsn;

import org.oasis_open.docs.wsn.bw_2.SubscribeCreationFailedFault;
import org.globus.crux.messaging.subscription.Subscription;

import javax.xml.ws.wsaddressing.W3CEndpointReference;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
public interface WSNSubscriptionEPRFactory{

    public W3CEndpointReference createSubscriptionEPR(Subscription subscription) throws SubscribeCreationFailedFault;

}
