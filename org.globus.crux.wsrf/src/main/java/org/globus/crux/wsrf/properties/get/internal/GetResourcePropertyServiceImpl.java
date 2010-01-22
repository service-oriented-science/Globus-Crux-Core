package org.globus.crux.wsrf.properties.get.internal;

import java.util.ResourceBundle;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.globus.crux.wsrf.properties.get.GetResourcePropertyService;
import org.globus.crux.wsrf.properties.get.ResourcePropertySet;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.GetResourcePropertyResponse;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.InvalidResourcePropertyQNameFault;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.ResourceUnknownFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Doreen Seider
 */
public class GetResourcePropertyServiceImpl implements GetResourcePropertyService {

	private static final Logger logger = LoggerFactory.getLogger(GetResourcePropertyServiceImpl.class);
	private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("org.globus.crux.wsrf.wsrf");

	public GetResourcePropertyResponse getResourceProperty(
			ResourcePropertySet rpSet, QName getResourcePropertyRequest)
			throws InvalidResourcePropertyQNameFault, ResourceUnknownFault {

		GetResourcePropertyResponse response = new GetResourcePropertyResponse();
		if (!rpSet.containsResourceProperty(getResourcePropertyRequest)) {
			String message = resourceBundle
					.getString("no.such.resource.property.exists");
			message = String.format("%s %s", message,
					getResourcePropertyRequest);
			logger.info(message);
			throw new InvalidResourcePropertyQNameFault(message);
		}
		Object rpResult = rpSet.getResourceProperty(getResourcePropertyRequest);
		JAXBElement rp = new JAXBElement(getResourcePropertyRequest, rpResult.getClass(), rpResult);
		response.getAny().add(rp);
		return response;
	}
}