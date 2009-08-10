package org.globus.crux.wsrf.query;

import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.InvalidQueryExpressionFaultType;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.ResourceUnknownFaultType;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.UnknownQueryExpressionDialectFaultType;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.InvalidResourcePropertyQNameFaultType;
import org.oasis_open.docs.wsrf._2004._06.wsrf_ws_resourceproperties_1_2_draft_01.QueryEvaluationErrorFaultType;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;

/**
 * @author turtlebender
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NamespaceMapping", propOrder = {
        "prefix",
        "namespaceUri"
})
public class NamespaceMapping {
    @XmlElement(name = "prefix", required = true)
    @XmlSchemaType(name = "string")
    private String prefix;
    @XmlElement(name = "namespaceURI", required = true)
    @XmlSchemaType(name = "string")
    private String namespaceUri;

    public NamespaceMapping() {
    }

    public NamespaceMapping(String prefix, String namespaceUri) {
        this.prefix = prefix;
        this.namespaceUri = namespaceUri;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getNamespaceUri() {
        return namespaceUri;
    }

    public void setNamespaceUri(String namespaceUri) {
        this.namespaceUri = namespaceUri;
    }
}
