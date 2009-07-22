package org.globus.wsrf.utils;

import org.xml.sax.InputSource;

import javax.wsdl.xml.WSDLLocator;
import java.io.IOException;

/**
 * @author turtlebender
 */
public abstract class AbstractWrapperWSDLLocator implements WSDLLocator {
    protected WSDLLocator parent;
    String wsdlUrl;
    InputSource last;
    String baseUri;
    boolean fromParent;

    public AbstractWrapperWSDLLocator(String wsdlUrl,
                                      WSDLLocator parent) {
        this.wsdlUrl = wsdlUrl;
        this.parent = parent;
    }

    public void close() {
        if (!fromParent) {
            try {
                if (last.getByteStream() != null) {
                    last.getByteStream().close();
                }
            } catch (IOException e) {
                //ignore
            }
        } else {
            parent.close();
        }
    }

    public abstract InputSource getInputSource();

    public InputSource getBaseInputSource() {
        InputSource is;
        if (parent != null) {
            is = parent.getBaseInputSource();
            fromParent = true;
            baseUri = is.getSystemId();
        } else {
            is = getInputSource();
            fromParent = false;
        }

        last = is;

        return is;
    }

    public String getBaseURI() {
        if (last == null) {
            getBaseInputSource();
            try {
                if (last.getByteStream() != null) {
                    last.getByteStream().close();
                }
            } catch (IOException e) {
                //ignore
            }
        }
        return baseUri;
    }

    public InputSource getImportInputSource(String parentLocation, String importLocation) {
        return parent.getImportInputSource(parentLocation, importLocation);
    }

    public String getLatestImportURI() {
        return parent.getLatestImportURI();
    }


}
