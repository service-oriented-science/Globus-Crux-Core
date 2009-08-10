package org.globus.crux.wsrf.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author turtlebender
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XPathQuery", namespace = "http://www.globus.org/query/xpath",
        propOrder = {
                "nsMapping",
                "query"
        })
public class XPathQuery {
    @XmlElement(name = "nsMapping", required = false)
    private List<NamespaceMapping> nsMapping = new ArrayList<NamespaceMapping>();

    @XmlElement(name = "query", required = true)
    @XmlSchemaType(name = "string")
    private String query;

    public List<NamespaceMapping> getNsMapping() {
        return nsMapping;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
