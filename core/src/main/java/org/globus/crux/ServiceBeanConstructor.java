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
public class ServiceBeanConstructor extends AbstractBeanDefinitionParser {

	private BeanDefinitionBuilder serviceBean = BeanDefinitionBuilder.rootBeanDefinition("org.globus.crux.SOAPServiceFactory");
	
	private ManagedList providers = new ManagedList();
	private ManagedList wrappers = new ManagedList();

	private static final String interfTag = "interf";
	private static final String targetTag = "target";
	private static final String dialectTag = "dialect";
	private static final String providersTag = "crux:providers";
	private static final String notifiersTag = "crux:notifiers";
	private static final String queryEngineTag = "crux:queryEngine";

	private final static String getRPProviderTag = "crux:getRPProvider";
	private final static String queryRPProviderTag = "crux:queryRPProvider";
	private final static String immediateResourceLifetimeProviderTag = "crux:immediateResourceLifetimeProvider";
	private final static String scheduledResourceLifetimeProviderTag = "crux:scheduledResourceLifetimeProvider";

	private final static String rpChangedNotifierTag = "crux:RPChangeNotifier";

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		
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
		parseProviders(providerElements);
		
		Element notifierssElement = DomUtils.getChildElementByTagName(element, notifiersTag);
		List<Element> notifiersElements = DomUtils.getChildElementsByTagName(notifierssElement,
				new String[] {rpChangedNotifierTag});
		parseNotifiers(notifiersElements);
		
		
		if (providers.size() > 0) {
			serviceBean.addPropertyValue("providers", providers);
		}
		if (wrappers.size() > 0) {
			serviceBean.addPropertyValue("wrappers", wrappers);
		}
		
		return serviceBean.getBeanDefinition();
	}

	/**
	 * Parses the 'providers' element, creates appropriate beans and adds them to the service bean
	 * construction..
	 *
	 * @param providersElements a {@link List} of {@link OperationProvider} elements.
	 */
	private void parseProviders(List<Element> providersElements) {
		for (int i = 0; i < providersElements.size(); ++i) {
			Element providerElement = (Element) providersElements.get(i);
			String providerElementName = providerElement.getNodeName();

			if (providerElementName.equals(getRPProviderTag)) {
				parseGetRPProvider(providerElement);
			} else if (providerElementName.equals(queryRPProviderTag)) {
				parseQueryRPProvider(providerElement);
			} else if (providerElementName.equals(immediateResourceLifetimeProviderTag)) {
				parseImmediateResourceLifetimeProvider(providerElement);
			} else if (providerElementName.equals(scheduledResourceLifetimeProviderTag)) {
				parseScheduledResourceLifetimeProvider(providerElement);
			}
		}		
	}

	/**
	 * Parses the 'getRPProvider' element and creates a appropriate bean.
	 *
	 * @param providerElement the appropriate provider {@link Element}.
	 */
	private void parseGetRPProvider(Element providerElement) {
		BeanDefinitionBuilder provider = BeanDefinitionBuilder.rootBeanDefinition("org.globus.crux.wsrf.properties.GetRPProvider");
		provider.addPropertyReference("RPSet", providerElement.getAttribute(targetTag));
		providers.add(provider.getBeanDefinition());
	}

	/**
	 * Parses the 'queryRPProvider' element and creates a appropriate bean.
	 *
	 * @param providerElement the appropriate provider {@link Element}.
	 */
	private void parseQueryRPProvider(Element providerElement) {
		BeanDefinitionBuilder provider = BeanDefinitionBuilder.rootBeanDefinition("org.globus.crux.wsrf.query.QueryRPProvider");

		List<Element> queryEngineElements = DomUtils.getChildElementsByTagName(providerElement, queryEngineTag);
		ManagedList queryEngines = new ManagedList(queryEngineElements.size());

		for (Element queryEngineElement : queryEngineElements) {
			BeanDefinitionBuilder queryEngine = null;

			if (queryEngineElement.getAttribute(dialectTag).equals("xpath")) {
				queryEngine = BeanDefinitionBuilder.rootBeanDefinition("org.globus.crux.wsrf.query.DefaultXPathQueryEngine");
				queryEngine.addPropertyReference("rps", queryEngineElement.getAttribute(targetTag));
			}
			if (queryEngine != null) {
				queryEngines.add(queryEngine.getBeanDefinition());
			}
		}
		provider.addPropertyValue("engines", queryEngines);
		providers.add(provider.getBeanDefinition());
	}

	/**
	 * Parses the 'immediateResourceLifetimeProvider' element and creates a appropriate bean.
	 *
	 * @param providerElement the appropriate provider {@link Element}.
	 */
	private void parseImmediateResourceLifetimeProvider(Element providerElement) {
		BeanDefinitionBuilder provider = BeanDefinitionBuilder.rootBeanDefinition("org.globus.crux.wsrf.lifetime.ImmediateResourceLifetimeProvider");
		provider.addPropertyReference(targetTag, providerElement.getAttribute(targetTag));
		providers.add(provider.getBeanDefinition());
	}

	/**
	 * Parses the 'scheduledResourceLifetimeProvider' element and creates a appropriate bean.
	 *
	 * @param providerElement the appropriate provider {@link Element}.
	 */
	private void parseScheduledResourceLifetimeProvider(Element providerElement) {
		BeanDefinitionBuilder provider = BeanDefinitionBuilder.rootBeanDefinition("org.globus.crux.wsrf.lifetime.ScheduledResourceLifetimeProvider");
		provider.addPropertyReference(targetTag, providerElement.getAttribute(targetTag));
		providers.add(provider.getBeanDefinition());
	}
	
	/**
	 * Parses the 'notifiers' element, creates appropriate beans.
	 *
	 * @param notifiersElements a {@link List} of notifier elements.
	 */
	private void parseNotifiers(List<Element> notifiersElements) {
		for (int i = 0; i < notifiersElements.size(); ++i) {
			Element notifierElement = (Element) notifiersElements.get(i);
			String notifierElementName = notifierElement.getNodeName();

			if (notifierElementName.equals(rpChangedNotifierTag)) {
				parseRPChangedNotifier(notifierElement);
			}
		}
	}
	
	/**
	 * Parses the 'RPChangedNotifier' element and creates a appropriate bean.
	 *
	 * @param providerElement the appropriate provider {@link Element}.
	 */
	private void parseRPChangedNotifier(Element notifierElement) {
		BeanDefinitionBuilder wrapper = BeanDefinitionBuilder.rootBeanDefinition("org.globus.crux.wsrf.properties.ResourcePropertyChangeNotifier");
		wrapper.addPropertyReference("RPSet", notifierElement.getAttribute(targetTag));
		wrappers.add(wrapper.getBeanDefinition());
	}

}
