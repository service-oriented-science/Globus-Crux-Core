package com.counter.service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import com.counter.CreateCounter;
import com.counter.CreateCounterResponse;
import org.w3c.dom.Document;

/**
 * @author turtlebender
 */
public class CounterResponseHandler  {
    JAXBContext context;
    DocumentBuilderFactory dbf;

    public CounterResponseHandler() throws Exception {
        context = JAXBContext.newInstance("com.counter");
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
    }

    public CreateCounterResponse handle(WebServiceContext wsc, CreateCounter request) throws Exception {
        CreateCounterResponse response = new CreateCounterResponse();
        JAXBElement<String> key = new JAXBElement<String>(new QName("http://counter.com", "CounterKey"),
                String.class, "counter1");
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        context.createMarshaller().marshal(key, doc);
        W3CEndpointReference epr = (W3CEndpointReference) wsc.getEndpointReference(doc.getDocumentElement());
        response.setEndpointReference(epr);
        return response;
    }
}
