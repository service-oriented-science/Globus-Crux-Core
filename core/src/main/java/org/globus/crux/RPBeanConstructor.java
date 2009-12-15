package org.globus.crux;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * Class parsing the custom schema extension used to configure crux service
 * beans.
 *
 * @author Doreen Seider
 */
public class RPBeanConstructor extends AbstractSingleBeanDefinitionParser {

	private static final String targetTag = "target";

	@Override
	protected String getBeanClassName(Element element) {
		return "org.globus.crux.wsrf.properties.AnnotationResourcePropertySet";
	}

	@Override
	protected void doParse(Element element, BeanDefinitionBuilder bean) {
		bean.addPropertyReference("target", element.getAttribute(targetTag));
	}
}
