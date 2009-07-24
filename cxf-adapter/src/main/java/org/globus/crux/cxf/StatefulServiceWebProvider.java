package org.globus.crux.cxf;

import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;
import org.globus.crux.stateful.CruxContext;

import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceContext;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXResult;
import javax.xml.namespace.QName;
import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author turtlebender
 */
@WebServiceProvider
@ServiceMode(value = Service.Mode.PAYLOAD)
public class StatefulServiceWebProvider implements Provider<Source> {
    @Resource
    private WebServiceContext context;
    private Map<QName, DispatchHandler<CXFCruxContext>> handlerMap =
            new ConcurrentHashMap<QName, DispatchHandler<CXFCruxContext>>();
    private CXFCruxContextFactory factory;

    public void setFactory(CXFCruxContextFactory factory) {
        this.factory = factory;
    }

    public void registerHandler(QName name, DispatchHandler<CXFCruxContext> handler) {
        handlerMap.put(name, handler);
    }

    public Source invoke(Source streamSource) {
        try {
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            QNameExtractor qe = new QNameExtractor();
            trans.transform(streamSource, new SAXResult(qe));
            QName requestName = qe.qname;
            DispatchHandler<CXFCruxContext> handler = handlerMap.get(requestName);
            return handler.handle(factory.createContext(context), streamSource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    class QNameExtractor extends DefaultHandler2 {
        boolean visited = false;
        private QName qname;

        @Override
        public void startElement(String s, String s1, String s2, Attributes attributes) throws SAXException {
            if (visited) {
                return;
            }
            qname = new QName(s, s1);
        }
    }   
}
