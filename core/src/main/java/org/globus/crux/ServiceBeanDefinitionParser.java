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
public class ServiceBeanDefinitionParser extends AbstractBeanDefinitionParser {

	private static final String interfTag = "interf";
	private static final String targetTag = "target";
	private static final String dialectTag = "dialect";
	private static final String providersTag = "crux:providers";
	private static final String queryEngineTag = "crux:queryEngine";

	/** Tag identifying a GetRPProvider. */
	private final static String getRPProviderTag = "crux:getRPProvider";
	/** Tag identifying a QueryRPProvider. */
	private final static String queryRPProviderTag = "crux:queryRPProvider";
	/** Tag identifying a ImmediateResourceLifetimeProvider. */
	private final static String immediateResourceLifetimeProviderTag = "crux:immediateResourceLifetimeProvider";
	/** Tag identifying a ScheduledResourceLifetimeProvider. */
	private final static String scheduledResourceLifetimeProviderTag = "crux:scheduledResourceLifetimeProvider";

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {

		BeanDefinitionBuilder serviceBean = BeanDefinitionBuilder.rootBeanDefinition("org.globus.crux.SOAPServiceFactory");

		String interf = element.getAttribute(interfTag);
		try {
			serviceBean.addPropertyValue(interfTag, getClass().getClassLoader().loadClass(interf));
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("The given service interface was not found: " + interf);
		}
		serviceBean.addPropertyReference(targetTag, element.getAttribute(targetTag));

		Element providersElement = DomUtils.getChildElementByTagName(element, providersTag);
		List<Element> providerElements = DomUtils.getChildElementsByTagName(providersElement,
				new String[] {getRPProviderTag, queryRPProviderTag, immediateResourceLifetimeProviderTag,
				scheduledResourceLifetimeProviderTag});

		if (providerElements != null && providerElements.size() > 0) {
			serviceBean.addPropertyValue("providers", parseProviders(providerElements));
		}

		return serviceBean.getBeanDefinition();
	}

	/**
	 * Parses the 'providers' element.
	 *
	 * @param providersElements a {@link List} of {@link OperationProvider} elements.
	 * @return a {@link List} of {@link OperationProvider} objects.
	 */
	private ManagedList parseProviders(List<Element> providersElements) {
		ManagedList providers = new ManagedList(providersElements.size());

		for (int i = 0; i < providersElements.size(); ++i) {
			Element providerElement = (Element) providersElements.get(i);
			String providerElementName = providerElement.getNodeName();

			AbstractBeanDefinition provider = null;

			if (providerElementName.equals(getRPProviderTag)) {
				provider = parseGetRPProvider(providerElement);
			} else if (providerElementName.equals(queryRPProviderTag)) {
				// FIXME this does not work at the moment and I don't have a clue why
//				provider = parseQueryRPProvider(providerElement);
			} else if (providerElementName.equals(immediateResourceLifetimeProviderTag)) {
				provider = parseImmediateResourceLifetimeProvider(providerElement);
			} else if (providerElementName.equals(scheduledResourceLifetimeProviderTag)) {
				provider = parseScheduledResourceLifetimeProvider(providerElement);
			}

			if (provider != null) {
				providers.add(provider);
			}
		}

		return providers;
	}

	/**
	 * Parses the 'getRPProvider' element.
	 *
	 * @param providerElement the appropriate provider {@link Element}.
	 * @return an {@link AbstractBeanDefinition} representing the GetRPProvider object.
	 */
	private AbstractBeanDefinition parseGetRPProvider(Element providerElement) {
		BeanDefinitionBuilder provider = BeanDefinitionBuilder.rootBeanDefinition("org.globus.crux.wsrf.properties.GetRPProvider");
		provider.addPropertyReference("RPSet", providerElement.getAttribute(targetTag));
		return provider.getBeanDefinition();
	}

	/**
	 * Parses the 'queryRPProvider' element.
	 *
	 * @param providerElement the appropriate provider {@link Element}.
	 * @return an {@link AbstractBeanDefinition} representing the QueryRPProvider object.
	 */
	private AbstractBeanDefinition parseQueryRPProvider(Element providerElement) {
		BeanDefinitionBuilder provider = BeanDefinitionBuilder.rootBeanDefinition("org.globus.crux.wsrf.query.QueryRPProvider");

		List<Element> queryEngineElements = DomUtils.getChildElementsByTagName(providerElement, queryEngineTag);
		ManagedList queryEngines = new ManagedList(queryEngineElements.size());

		for (Element queryEngineElement : queryEngineElements) {
			BeanDefinitionBuilder queryEngine = null;

			if (queryEngineElement.getAttribute(dialectTag).equals("xpath")) {
				queryEngine = BeanDefinitionBuilder.rootBeanDefinition("org.globus.crux.wsrf.query.DefaultXPathQueryEngine");
				queryEngine.addPropertyReference("Rps", queryEngineElement.getAttribute(targetTag));
			}
			if (queryEngine != null) {
				queryEngines.add(queryEngine);
			}
		}
		provider.addPropertyValue("Engine", queryEngines);
		return provider.getBeanDefinition();
	}

	/**
	 * Parses the 'immediateResourceLifetimeProvider' element.
	 *
	 * @param providerElement the appropriate provider {@link Element}.
	 * @return an {@link AbstractBeanDefinition} representing the ImmediateResourceLifetimeProvider object.
	 */
	private AbstractBeanDefinition parseImmediateResourceLifetimeProvider(Element providerElement) {
		BeanDefinitionBuilder provider = BeanDefinitionBuilder.rootBeanDefinition("org.globus.crux.wsrf.lifetime.ImmediateResourceLifetimeProvider");
		provider.addPropertyReference(targetTag, providerElement.getAttribute(targetTag));
		return provider.getBeanDefinition();
	}

	/**
	 * Parses the 'scheduledResourceLifetimeProvider' element.
	 *
	 * @param providerElement the appropriate provider {@link Element}.
	 * @return an {@link AbstractBeanDefinition} representing the ResourceLifetimeProvider object.
	 */
	private AbstractBeanDefinition parseScheduledResourceLifetimeProvider(Element providerElement) {
		BeanDefinitionBuilder provider = BeanDefinitionBuilder.rootBeanDefinition("org.globus.crux.wsrf.lifetime.ScheduledResourceLifetimeProvider");
		provider.addPropertyReference(targetTag, providerElement.getAttribute(targetTag));
		return provider.getBeanDefinition();
	}
}
