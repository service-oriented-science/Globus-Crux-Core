package org.globus.crux;

import java.util.List;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Class parsing the custom schema extension used to configure crux service beans.
 *
 * @author Doreen Seider
 */
public class ServiceBeanDefinitionParser extends
		AbstractBeanDefinitionParser {

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {

		BeanDefinitionBuilder serviceBean = BeanDefinitionBuilder.rootBeanDefinition("org.globus.crux.SOAPServiceFactory");

		final String interf = "interf";
		serviceBean.addPropertyValue(interf, element.getAttribute(interf));
		final String target = "target";
		serviceBean.addPropertyReference(target, element.getAttribute(target));

		Element providersElement = DomUtils.getChildElementByTagName(element, "crux:providers");
		List<Element> providerElements = DomUtils.getChildElementsByTagName(providersElement, "crux:provider");
		System.out.println(providerElements.size());
		if (providerElements != null && providerElements.size() > 0) {
			serviceBean.addPropertyValue("providers", parseProviders(providerElements));
		}

		return serviceBean.getBeanDefinition();
	}

	/**
	 * Parses the 'providers' element.
	 *
	 * @param childElements a {@link List} of {@link OperationProvider} elements.
	 * @return a {@link List} of {@link OperationProvider} objects.
	 */
	private ManagedList parseProviders(List<Element> childElements) {
		ManagedList children = new ManagedList(childElements.size());

		for (int i = 0; i < childElements.size(); ++i) {
			Element childElement = (Element) childElements.get(i);
			BeanDefinitionBuilder child = BeanDefinitionBuilder.rootBeanDefinition("org.globus.crux.wsrf.properties.GetRPProvider");
			child.addPropertyReference("RPSet", childElement.getAttribute("resourceSet"));
			children.add(child.getBeanDefinition());
		}

		return children;
	}

}
