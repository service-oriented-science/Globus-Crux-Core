package org.globus.crux;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Class handling the namespace for the custom schema extension used
 * to configure crux service beans.
 *
 * @author Doreen Seider
 */
public class CruxNamespaceHandler extends NamespaceHandlerSupport {

	/** Constant holding a tag to parse. */
	private final static String serviceTag = "service";

	/** Constant holding a tag to parse. */
	private final static String annotatedRPSetTag ="annotatedRPSet";

	/**
	 * Initialize this namespace handler.
	 */
	public void init() {
		registerBeanDefinitionParser(serviceTag, new ServiceBeanConstructor());
		registerBeanDefinitionParser(annotatedRPSetTag, new RPBeanConstructor());
	}

}
