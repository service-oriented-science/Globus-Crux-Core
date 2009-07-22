/*
 * Copyright 1999-2006 University of Chicago
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.globus.wsrf.tools.wsdl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.wsdl.Types;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSParticle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.globus.wsrf.utils.XmlUtils;

public class TypesProcessor implements WSDLPreprocessorConstants
{
    private Definition definition;
    private Map imports = null;
    private Map includes = null;

    private static Log logger =
        LogFactory.getLog(TypesProcessor.class.getName());

    //Todo: better error handling

    public TypesProcessor(Definition definition)
    {
        this.definition = definition;
    }

    private String addPrefix(String namespace, String prefix)
    {
        String pref = this.definition.getPrefix(namespace);
        if (pref == null)
        {
            if (this.definition.getNamespace(prefix) == null)
            {
                this.definition.addNamespace(prefix, namespace);
                pref = prefix;
            }
            else
            {
                int i = 0;
                while ( this.definition.getNamespace("ns" + i) != null )
                {
                    i++;
                }
                pref = "ns" + i;
                this.definition.addNamespace(pref, namespace);
            }
        }
        return pref;
    }

    public void addWSAImport(Map schemaDocumentLocations)
        throws Exception
    {
        Element schema = getSchema();
        addSchemaDefinitions(WSA_NS_WS, schemaDocumentLocations, schema);
        addPrefix(WSA_NS_WS, "wsaw");
    }

    private Element getSchema() throws Exception
    {
        Types types = this.definition.getTypes();
        if(types == null)
        {
            types = definition.createTypes();
            this.definition.setTypes(types);
        }
        Element schema = null;
        List elementList = types.getExtensibilityElements();
        if(elementList.isEmpty())
        {
            UnknownExtensibilityElement extensibilityElement =
                new UnknownExtensibilityElement();

            DocumentBuilder docBuilder =
                DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            schema = doc.createElementNS(XSD_NS, "xsd:schema");
            Attr nameSpace =
                doc.createAttributeNS("http://www.w3.org/2000/xmlns/",
                                      "xmlns:xsd");
            nameSpace.setValue(XSD_NS);
            schema.setAttributeNode(nameSpace);
            nameSpace = doc.createAttribute("targetNamespace");
            nameSpace.setValue(definition.getTargetNamespace());
            schema.setAttributeNode(nameSpace);
            schema.appendChild(doc.createTextNode("\n      "));
            doc.appendChild(schema);
            extensibilityElement.setElement(schema);
            types.addExtensibilityElement(extensibilityElement);
        }
        else
        {
            schema =
            ((UnknownExtensibilityElement) elementList.get(0)).getElement();
        }
        return schema;
    }

    public void addResourceProperties(
        QName portTypeName, Map resourcePropertyElements,
        Map schemaDocumentLocations) throws Exception
    {
        logger.debug("Starting to build resource properties element");
        PortType portType = this.getPortType(portTypeName);

        QName resourceProperties =
            (QName) portType.getExtensionAttribute(RP);

        addPrefix(WSRP_NS, "wsrp");

        Element schema = getSchema();
        Document doc = schema.getOwnerDocument();
        String schemaPrefix = "";
        if(schema.getPrefix() != null)
        {
            schemaPrefix = schema.getPrefix() + ":";
        }

        Element properties = null;
        if(resourceProperties == null)
        {
            resourceProperties =
            new QName(portType.getQName().getLocalPart() +
                      "GTWSDLResourceProperties");
            portType.setExtensionAttribute(RP, resourceProperties);
            properties = doc.createElementNS(
                XSD_NS,
                schemaPrefix + "element");
            properties.setAttribute("name", resourceProperties.getLocalPart());
            properties.appendChild(doc.createTextNode("\n        "));
            schema.appendChild(properties);
            schema.appendChild(doc.createTextNode("\n    "));
        }
        else
        {
            NodeList elementNodes = schema.getElementsByTagNameNS(
                XSD_NS, "element");
            String name;
            Element element;
            for(int i = 0; i < elementNodes.getLength(); i++)
            {
                element = (Element) elementNodes.item(i);
                name = element.getAttribute("name");
                if(name != null &&
                   XmlUtils.getFullQNameFromString(name, element).
                    getLocalPart().equals(
                        resourceProperties.getLocalPart()))
                {
                    properties = element;
                    break;
                }
            }

            if(properties == null)
            {
                throw new Exception("Unable to find '" +  resourceProperties + "' element in WSDL schema section of '" + this.definition.getDocumentBaseURI() + "' document.");
            }
        }
        String type = properties.getAttribute("type");
        NodeList complexTypeElements;
        Element complexType = null;

        if(type != null && !type.equals(""))
        {
            complexTypeElements = schema.getElementsByTagNameNS(
                XSD_NS,
                "complexType");
            Element currentType;
            QName currentName;
            QName typeName = XmlUtils.getFullQNameFromString(type,
                                                             properties);
            for(int i = 0; i < complexTypeElements.getLength(); i++)
            {
                currentType = (Element) complexTypeElements.item(i);
                currentName = XmlUtils.getFullQNameFromString(
                    currentType.getAttribute("name"),
                    currentType);
                if(currentName.getLocalPart().equals(
                    typeName.getLocalPart()))
                {
                    complexType = currentType;
                    break;
                }
            }
            if(complexType == null)
            {
                throw new Exception("Unable to find type entry for '" + resourceProperties + "' element");
            }
        }
        else
        {
            complexTypeElements = properties.getElementsByTagNameNS(
                XSD_NS,
                "complexType");
            if(complexTypeElements.getLength() > 0)
            {
                complexType = (Element) complexTypeElements.item(0);
            }
            else
            {
                complexType =
                doc.createElementNS(
                    XSD_NS,
                    schemaPrefix + "complexType");
                complexType.appendChild(doc.createTextNode("\n          "));
                properties.appendChild(complexType);
                properties.appendChild(doc.createTextNode("\n      "));
            }
        }

        NodeList sequenceElements = complexType.getElementsByTagNameNS(
            XSD_NS, "sequence");
        Element sequence;
        if(sequenceElements.getLength() > 0)
        {
            sequence = (Element) sequenceElements.item(0);
        }
        else
        {
            sequence = doc.createElementNS(XSD_NS,
                                           schemaPrefix + "sequence");
            complexType.appendChild(sequence);
            complexType.appendChild(doc.createTextNode("\n        "));
        }

        Collection resourcePropertiesList =
            resourcePropertyElements.values();
        Iterator elementIterator = resourcePropertiesList.iterator();
        int nsCounter = 0;

        while(elementIterator.hasNext())
        {
            sequence.appendChild(doc.createTextNode("\n            "));
            Element resourcePropertyElement = doc.createElementNS(
                XSD_NS,
                schemaPrefix + "element");
            XSParticle particle = (XSParticle) elementIterator.next();
            XSElementDeclaration element =
                (XSElementDeclaration) particle.getTerm();
            String prefix = XmlUtils.getPrefix(element.getNamespace(),
                                               schema);

            addSchemaDefinitions(element,
                                 schemaDocumentLocations,
                                 schema);

            if(prefix == null)
            {
                prefix = "rpns" + String.valueOf(nsCounter++);
                Attr nameSpace =
                    doc.createAttributeNS("http://www.w3.org/2000/xmlns/",
                                          "xmlns:" + prefix);
                nameSpace.setValue(element.getNamespace());
                schema.setAttributeNode(nameSpace);
            }

            resourcePropertyElement.setAttribute("ref",
                                                 prefix + ":"
                                                 + element.getName());
            resourcePropertyElement.setAttribute(
                "minOccurs",
                String.valueOf(
                    particle.getMinOccurs()));
            if(particle.getMaxOccursUnbounded())
            {
                resourcePropertyElement.setAttribute(
                    "maxOccurs",
                    String.valueOf(
                        "unbounded"));
            }
            else
            {
                resourcePropertyElement.setAttribute(
                    "maxOccurs",
                    String.valueOf(
                        particle.getMaxOccurs()));
            }
            sequence.appendChild(resourcePropertyElement);
        }
        sequence.appendChild(doc.createTextNode("\n          "));
        logger.debug("Resource properties element: " +
                     XmlUtils.getElementAsString(properties));
    }

    private void addSchemaDefinitions(XSElementDeclaration element,
                                      Map schemaDocumentLocations,
                                      Element schema)
        throws Exception
    {
        String elementNS = element.getNamespace();
        addSchemaDefinitions(elementNS, schemaDocumentLocations, schema);
    }

    private void addSchemaDefinitions(String elementNS,
                                      Map schemaDocumentLocations,
                                      Element schema)
        throws Exception
    {
        Map map = (Map) schemaDocumentLocations.get(elementNS);
        if (map == null)
        {
            return;
        }
        Set locations = map.keySet();
        Iterator locationsIterator = locations.iterator();
        String targetNS = schema.getAttribute("targetNamespace");
        Document owner = schema.getOwnerDocument();
        String schemaPrefix = schema.getPrefix();
        if(schemaPrefix == null)
        {
            schemaPrefix = "";
        }
        else
        {
            schemaPrefix += ":";
        }
        this.populateImportsMap(schema);
        this.populateIncludesMap(schema);

        while(locationsIterator.hasNext())
        {
            String location = (String) locationsIterator.next();
            if(!location.equals(definition.getDocumentBaseURI()))
            {
                location = WSDLPreprocessor.getRelativePath(
                    definition.getDocumentBaseURI(),
                    location);

                if(elementNS.equals(targetNS))
                {
                    Map includes = (Map) this.includes.get(elementNS);

                    if(includes == null)
                    {
                        includes = new HashMap();
                        this.includes.put(elementNS, includes);
                    }

                    if(!includes.containsKey(location))
                    {
                        Element include = owner.createElementNS(
                            XSD_NS, schemaPrefix + "include");
                        include.setAttribute("schemaLocation", location);
                        schema.insertBefore(owner.createTextNode("\n"),
                                            schema.getFirstChild());
                        schema.insertBefore(include,
                                            schema.getFirstChild());
                        schema.insertBefore(owner.createTextNode("\n"),
                                            schema.getFirstChild());
                        includes.put(location, null);
                    }
                }
                else
                {
                    Map imports = (Map) this.imports.get(elementNS);

                    if(imports == null)
                    {
                        imports = new HashMap();
                        this.imports.put(elementNS, imports);
                    }

                    if(!imports.containsKey(location))
                    {
                        Element xsdImport = owner.createElementNS(
                            XSD_NS, schemaPrefix + "import");
                        xsdImport.setAttribute("namespace", elementNS);
                        xsdImport.setAttribute("schemaLocation", location);
                        schema.insertBefore(owner.createTextNode("\n"),
                                            schema.getFirstChild());
                        schema.insertBefore(xsdImport,
                                            schema.getFirstChild());
                        schema.insertBefore(owner.createTextNode("\n"),
                                            schema.getFirstChild());
                        imports.put(location, null);
                    }
                }
            }
        }
    }

    private void populateImportsMap(Element schema)
    {
        if(this.imports == null)
        {
            this.imports = new HashMap();
            NodeList importNodes =
                schema.getElementsByTagNameNS(XSD_NS, "import");
            for(int i = 0; i < importNodes.getLength(); i++)
            {
                Element xsdImport = (Element) importNodes.item(i);
                String namespace = xsdImport.getAttribute("namespace") == null ?
                                   schema.getAttribute("targetNamespace") :
                                   xsdImport.getAttribute("namespace");
                Map locations = (Map) this.imports.get(namespace);
                if(locations == null)
                {
                    locations = new HashMap();
                    this.imports.put(namespace, locations);
                }
                locations.put(xsdImport.getAttribute("schemaLocation"), null);
            }
        }
    }

    private void populateIncludesMap(Element schema)
    {
        if(this.includes == null)
        {
            this.includes = new HashMap();
            NodeList includeNodes =
                schema.getElementsByTagNameNS(XSD_NS, "include");
            for(int i = 0; i < includeNodes.getLength(); i++)
            {
                Element include = (Element) includeNodes.item(i);
                String namespace = schema.getAttribute("targetNamespace");
                Map locations = (Map) this.includes.get(namespace);
                if(locations == null)
                {
                    locations = new HashMap();
                    this.includes.put(namespace, locations);
                }
                locations.put(include.getAttribute("schemaLocation"), null);
            }
        }
    }

    public PortType getPortType(QName portTypeName) throws Exception
    {
        Map portTypes = definition.getPortTypes();
        PortType portType = null;

        if(portTypeName != null)
        {
            if(portTypeName.getNamespaceURI() == null ||
               portTypeName.getNamespaceURI().length() == 0)
            {
                portTypeName = new QName(definition.getTargetNamespace(),
                                         portTypeName.getLocalPart());
            }

            portType = (PortType) portTypes.get(portTypeName);
            if(portType == null)
            {
                throw new Exception("PortType not found: " + 
                                    portTypeName.getLocalPart());
            }
        }
        else
        {
            // Just pick the first one
            Set portTypeSet = portTypes.entrySet();
            Object portTypeArray[] = portTypeSet.toArray();

            if(portTypeArray.length < 1)
            {
                throw new Exception("Could not find any portTypes");
            }
            portType = (PortType) ((Map.Entry) portTypeArray[0]).getValue();
        }
        return portType;
    }
}
