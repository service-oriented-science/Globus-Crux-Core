package org.globus.crux.cxf.jaxb;

import org.globus.crux.DispatchHandler;
import org.globus.crux.stateful.CruxContext;
import org.w3c.dom.Document;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author turtlebender
 */
public abstract class AbstractJAXBStatefulReflectiveHandler<T, V> implements DispatchHandler {
    private JAXBContext jaxb;
    private QName keyName;
    private DocumentBuilderFactory dbf;
    private Object target;

    protected AbstractJAXBStatefulReflectiveHandler(QName keyName, Object target, JAXBContext jaxb) {
        this.jaxb = jaxb;
        this.keyName = keyName;
        dbf = DocumentBuilderFactory.newInstance();
        this.target = target;
    }

    public Source handle(CruxContext context, Source request) throws Exception {

        Object key = context.getKey();
        Object unmarshalled = jaxb.createUnmarshaller().unmarshal(request);
        V result = doHandle(key, target, (T) unmarshalled);
        Document doc = dbf.newDocumentBuilder().newDocument();
        jaxb.createMarshaller().marshal(result, doc);
        return new DOMSource(doc);
    }

    protected abstract V doHandle(Object key, Object target, T payload) throws Exception;
}
