package org.globus.crux;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Class handling the namespace for the custom schema extension used
 * to configure crux service beans.
 *
 * @author Doreen Seider
 */
public class ServiceNamespaceHandler extends NamespaceHandlerSupport {

	/** Constant holding the tag of the element to parse. */
	private final static String elementToParse ="service";

	/**
	 * Initialize this namespace handler.
	 */
	public void init() {
		registerBeanDefinitionParser(elementToParse, new ServiceBeanDefinitionParser());
	}

}
