package org.globus.crux.messaging.wsn;

import org.globus.crux.OperationProvider;
import org.globus.crux.ProviderException;
import org.oasis_open.docs.wsn.bw_2.WSNNotificationProducer;

/**
 * @author Doreen Seider
 */
public class WSNNotificationProducerProvider implements OperationProvider<WSNNotificationProducer> {
	private WSNNotificationProducer impl;

	public WSNNotificationProducer getImplementation() throws ProviderException {
		if (impl == null) {
			impl = new WSNNotificationProducerImpl();
			// FIXME inject dependencies
		}
		return impl;
	}

	public Class<WSNNotificationProducer> getInterface() {
		return WSNNotificationProducer.class;
	}

}
