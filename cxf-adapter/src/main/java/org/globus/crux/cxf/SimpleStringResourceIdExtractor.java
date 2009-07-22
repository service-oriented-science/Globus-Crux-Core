package org.globus.crux.cxf;

import org.apache.cxf.ws.addressing.ReferenceParametersType;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import javax.xml.bind.JAXBElement;
import java.util.List;

/**
 * @author turtlebender
 */
public class SimpleStringResourceIdExtractor implements ResourceIdExtractor<String>{
    private QName resourceIdQName;

    public SimpleStringResourceIdExtractor(QName resourceIdQName) {
        this.resourceIdQName = resourceIdQName;
    }

    public String extractId(ReferenceParametersType referenceParameters) throws CXFAddressingException {
        List<Object> parameters = referenceParameters.getAny();
        for(Object parameter: parameters){
            if(parameter instanceof Element){
                Element element = (Element) parameter;
                if(element.getNamespaceURI().equals(resourceIdQName.getNamespaceURI()) &&
                        element.getLocalName().equals(resourceIdQName.getLocalPart())){
                    return element.getFirstChild().getNodeValue();
                }
            }else if(parameter instanceof JAXBElement){
                return ((JAXBElement) parameter).getValue().toString();
            }
        }
        return null;
    }

    public QName getResourceIdQName() {
        return resourceIdQName;
    }

    public void setResourceIdQName(QName resourceIdQName) {
        this.resourceIdQName = resourceIdQName;
    }
}
