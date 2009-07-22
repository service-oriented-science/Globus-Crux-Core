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

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.net.URLDecoder;

import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Import;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.PortType;
import javax.wsdl.Types;
import javax.wsdl.extensions.AttributeExtensible;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;

import org.apache.xerces.dom.DOMInputImpl;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSNamespaceItemList;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.ls.LSInput;

import static org.globus.wsrf.tools.wsdl.WSDLPreprocessorConstants.*;
import org.globus.wsrf.utils.XmlUtils;

// TODO: need to i18n
public class WSDLPreprocessor {
    private static Log logger =
            LogFactory.getLog(WSDLPreprocessor.class.getName());

    private boolean wsaImport = false;
    private boolean quiet = false;
    private int nameSpaceCounter = 0;

    private String inFileName;
    private String outFileName;
    private QName portTypeName;

    private static final String ENC = "UTF-8";


    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public void setInputFile(String file) {
        this.inFileName = file;
    }

    public void setOutputFile(String file) {
        this.outFileName = file;
    }

    public void setPortTypeName(String name) {
        this.portTypeName = new QName(name);
    }

    public void execute()
            throws Exception {
        if (this.inFileName == null || this.inFileName.length() == 0) {
            throw new Exception("Input file not specified");
        }

        WSDLFactory factory = WSDLFactory.newInstance();

        WSDLReader reader = factory.newWSDLReader();
        WSDLWriter writer = factory.newWSDLWriter();

        // Don't import docs for now
        reader.setFeature("javax.wsdl.verbose", false);
        reader.setFeature("javax.wsdl.importDocuments", false);

        Definition definition = null;

        try {
            definition = reader.readWSDL(null, this.inFileName);
        }
        catch (Exception e) {
            throw new Exception("Could not read WSDL input file: " +
                    this.inFileName, e);
        }

        TypesProcessor typesProcessor = new TypesProcessor(definition);

        PortType portType = null;

        try {
            portType = typesProcessor.getPortType(this.portTypeName);
        }
        catch (Exception e) {
            throw new Exception("Could not find "
                    + (portTypeName == null ? "any" : portTypeName.toString())
                    + " portType.", e);
        }

        this.portTypeName = portType.getQName();

        ExtensionRegistry extensionAttributes = new ExtensionRegistry();
        extensionAttributes.registerExtensionAttributeType(
                PortType.class,
                EXTENDS,
                AttributeExtensible.LIST_OF_QNAMES_TYPE);
        extensionAttributes.registerExtensionAttributeType(
                PortType.class, RP,
                AttributeExtensible.QNAME_TYPE);
        reader.setExtensionRegistry(extensionAttributes);

        if (!this.quiet) {
            reader.setFeature("javax.wsdl.verbose", true);
        }
        reader.setFeature("javax.wsdl.importDocuments", true);

        try {
            definition = reader.readWSDL(null, this.inFileName);
        }
        catch (Exception e) {
            throw new Exception("Could not read WSDL input file: " +
                    this.inFileName, e);
        }

        typesProcessor = new TypesProcessor(definition);
        portType = definition.getPortType(this.portTypeName);
        List portTypeNames = getDependencies(portType);
        Map resourcePropertyElements = new HashMap();
        Map schemaDocumentLocations = new HashMap();
        try {
            flatten(portType, definition, definition, portTypeNames,
                    resourcePropertyElements,
                    schemaDocumentLocations);
        }
        catch (Exception e) {
            throw new Exception("Failed to flatten input file " +
                    this.inFileName + ": " + e.getMessage(), e);
        }

        if (!resourcePropertyElements.isEmpty()) {
            try {
                typesProcessor.addResourceProperties(this.portTypeName,
                        resourcePropertyElements,
                        schemaDocumentLocations);
            }
            catch (Exception e) {
                throw new Exception("Failed to add resource properties: " +
                        e.getMessage(), e);
            }
        }

        if (this.wsaImport) {
            try {
                typesProcessor.addWSAImport(schemaDocumentLocations);
            }
            catch (Exception e) {
                throw new Exception("Failed to add WSA XSD import: " +
                        e.getMessage(), e);
            }
        }

        FileOutputStream outFile = null;
        try {
            if (this.outFileName != null) {
                outFile = new FileOutputStream(this.outFileName);
            } else {
                outFile = new FileOutputStream(FileDescriptor.out);
            }
        }
        catch (Exception e) {
            throw new Exception("Could not open output file: " +
                    (outFileName == null ? "STDOUT" : outFileName), e);
        }

        try {
            writer.writeWSDL(definition, outFile);
        }
        catch (Exception e) {
            throw new Exception("Failed to write processed WSDL: " +
                    e.getMessage(), e);
        }
        finally {
            if (this.outFileName != null) {
                try {
                    outFile.close();
                }
                catch (Exception e) {
                }
            }
        }
        if (this.outFileName != null && !this.quiet) {
            System.out.println("Generated " + this.outFileName);
        }
    }

    private PortType flatten(PortType portType,
                             Definition definition,
                             Definition parentDefinition,
                             List portTypeNames,
                             Map resourcePropertyElements,
                             Map schemaDocumentLocations)
            throws Exception {
        Map portTypes = definition.getPortTypes();
        Iterator portTypeIterator = portTypes.values().iterator();

        while (portTypeIterator.hasNext()) {
            PortType currentPortType = (PortType) portTypeIterator.next();

            if (portTypeNames != null
                    && portTypeNames.contains(currentPortType.getQName())) {
                logger.debug("Found porttype: " +
                        currentPortType.getQName());

                QName resourceProperties = (QName)
                        currentPortType.getExtensionAttribute(RP);

                if (resourceProperties != null) {
                    logger.debug("Adding resource properties for porttype: " +
                            currentPortType.getQName());
                    resourcePropertyElements.putAll(
                            getResourceProperties(resourceProperties,
                                    definition,
                                    schemaDocumentLocations,
                                    this.quiet));
                }

                List newDependencies = getDependencies(currentPortType);

                if (newDependencies != null && !newDependencies.isEmpty()) {
                    flatten(currentPortType, definition, parentDefinition,
                            newDependencies,
                            resourcePropertyElements,
                            schemaDocumentLocations);
                    if (!newDependencies.isEmpty() &&
                            definition != parentDefinition &&
                            !quiet) {
                        System.err.println(
                                "WARNING: The following porttypes are missing:");
                        Iterator portTypeNamesIterator =
                                newDependencies.iterator();
                        while (portTypeNamesIterator.hasNext()) {
                            System.err.println(
                                    "\t" + ((QName) portTypeNamesIterator.next()).toString());
                        }
                    }
                }

                fixNameSpaces(currentPortType, parentDefinition);
                List operations = currentPortType.getOperations();
                ListIterator operationsIterator = operations.listIterator();
                Operation operation;
                Input input;
                Output output;

                while (operationsIterator.hasNext()) {
                    operation = (Operation) operationsIterator.next();
                    input = operation.getInput();
                    output = operation.getOutput();

                    if (portType.getOperation(
                            operation.getName(),
                            input == null ? null : input.getName(),
                            output == null ? null : output.getName()) == null) {
                        if (input != null &&
                                input.getExtensionAttribute(WSA_ACTION) != null) {
                            this.wsaImport = true;
                            Element schema = getSchemaElement(definition);
                            if (schema != null) {
                                XSModel schemaModel = loadSchema(schema, definition);
                                populateLocations(schemaModel,
                                        schemaDocumentLocations,
                                        definition);
                            }
                        }
                        portType.addOperation(operation);
                    }
                }

                addImports(definition, parentDefinition);

                portTypeNames.remove(currentPortType.getQName());
            }
        }

        if (portTypeNames == null || portTypeNames.isEmpty()) {
            return portType;
        } else {
            // Only go to immediate imports - nested imports are not processed
            if (definition == parentDefinition) {
                Map imports = new HashMap();
                imports.putAll(definition.getImports());
                Iterator importNSIterator = imports.values().iterator();

                while (importNSIterator.hasNext() && !portTypeNames.isEmpty()) {
                    Vector importVector = (Vector) importNSIterator.next();
                    Iterator importIterator = importVector.iterator();
                    while (importIterator.hasNext() && !portTypeNames.isEmpty()) {
                        // Name space?
                        // I think the rule is to set the target name space to the
                        // namespace
                        // specified by import statement if not already defined
                        Import importDef = (Import) importIterator.next();
                        flatten(portType, importDef.getDefinition(),
                                parentDefinition, portTypeNames,
                                resourcePropertyElements,
                                schemaDocumentLocations);
                    }
                }

                if (!portTypeNames.isEmpty() && !quiet) {
                    System.err.println(
                            "WARNING: The following porttypes are missing:");
                    Iterator portTypeNamesIterator = portTypeNames.iterator();
                    while (portTypeNamesIterator.hasNext()) {
                        System.err.println(
                                "\t" +
                                        ((QName) portTypeNamesIterator.next()).toString());
                    }
                }
            }

            return portType;
        }
    }

    private static void addImports(Definition definition,
                                   Definition parentDefinition)
            throws Exception {
        Collection imports = definition.getImports().values();
        Iterator importsIterator = imports.iterator();

        while (importsIterator.hasNext()) {
            Vector importsVector = (Vector) importsIterator.next();
            Iterator importsVectorIterator = importsVector.iterator();
            boolean addImport = true;
            while (importsVectorIterator.hasNext()) {
                Import currentImport =
                        (Import) importsVectorIterator.next();
                String location = currentImport.getLocationURI();
                if (location != null && (location.startsWith(".") ||
                        location.indexOf('/') == -1)) {
                    location =
                            getRelativePath(
                                    parentDefinition.getDocumentBaseURI(),
                                    currentImport.getDefinition().getDocumentBaseURI());
                    currentImport.setLocationURI(location);
                }
                List parentImports = parentDefinition.getImports(
                        currentImport.getNamespaceURI());
                if (parentImports != null) {
                    Iterator parentImportsIterator = parentImports.iterator();
                    while (parentImportsIterator.hasNext()) {
                        Import parentImport =
                                (Import) parentImportsIterator.next();
                        if (parentImport.getLocationURI().equals(location)) {
                            addImport = false;
                            break;
                        }
                    }
                }
                if (addImport) {
                    parentDefinition.addImport(currentImport);
                }
                addImport = true;
            }
        }
    }

    private static List getDependencies(PortType portType) {
        List result = (List) portType.getExtensionAttribute(EXTENDS);

        if (result != null) {
            portType.getExtensionAttributes().remove(EXTENDS);
        }

        return result;
    }

    private static Element getSchemaElement(Definition def) {
        Types types = def.getTypes();
        if (types == null) {
            return null;
        }
        List elementList = types.getExtensibilityElements();
        if (elementList.isEmpty()) {
            return null;
        } else {
            return ((UnknownExtensibilityElement) elementList.get(0)).getElement();
        }
    }

    private static XSModel loadSchema(Element schema, Definition def) {
        // add namespaces from definition element
        Map definitionNameSpaces = def.getNamespaces();
        Set nameSpaces = definitionNameSpaces.entrySet();
        Iterator nameSpacesIterator = nameSpaces.iterator();

        while (nameSpacesIterator.hasNext()) {
            Entry nameSpaceEntry = (Entry) nameSpacesIterator.next();
            if (!"".equals((String) nameSpaceEntry.getKey()) &&
                    !schema.hasAttributeNS("http://www.w3.org/2000/xmlns/",
                            (String) nameSpaceEntry.getKey())) {
                Attr nameSpace =
                        schema.getOwnerDocument().createAttributeNS(
                                "http://www.w3.org/2000/xmlns/",
                                "xmlns:" + nameSpaceEntry.getKey());
                nameSpace.setValue((String) nameSpaceEntry.getValue());
                schema.setAttributeNode(nameSpace);
            }
        }

        LSInput schemaInput = new DOMInputImpl();
        schemaInput.setStringData(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + XmlUtils.getElementAsString(schema));
        logger.debug("Loading schema in types section of definition " +
                def.getDocumentBaseURI());
        schemaInput.setSystemId(def.getDocumentBaseURI());
        XMLSchemaLoader schemaLoader = new XMLSchemaLoader();
        XSModel schemaModel = schemaLoader.load(schemaInput);
        logger.debug("Done loading");
        return schemaModel;
    }

    private static void populateLocations(XSModel schemaModel,
                                          Map documentLocations,
                                          Definition def)
            throws Exception {
        String documentURI = def.getDocumentBaseURI();
        XSNamespaceItemList namespaceItemList =
                schemaModel.getNamespaceItems();
        for (int i = 0; i < namespaceItemList.getLength(); i++) {
            XSNamespaceItem namespaceItem = namespaceItemList.item(i);
            Map locations = (Map) documentLocations.get(
                    namespaceItem.getSchemaNamespace());
            if (locations == null) {
                locations = new HashMap();
                documentLocations.put(namespaceItem.getSchemaNamespace(),
                        locations);
            }

            StringList list = namespaceItem.getDocumentLocations();

            for (int j = 0; j < list.getLength(); j++) {
                String uri = URLDecoder.decode(list.item(j), ENC);
                if (!uri.equals(documentURI)) {
                    locations.put(list.item(j), null);
                }
            }
        }
    }

    public static Map getResourceProperties(QName resourceProperties,
                                            Definition def,
                                            Map documentLocations)
            throws Exception {
        return getResourceProperties(resourceProperties,
                def,
                documentLocations,
                true);
    }

    public static Map getResourceProperties(QName resourceProperties,
                                            Definition def,
                                            Map documentLocations,
                                            boolean quiet)
            throws Exception {
        HashMap resourcePropertyElements = new HashMap();

        if (resourceProperties != null) {
            Types types = def.getTypes();
            if (types == null) {
                if (!quiet) {
                    System.err.println(
                            "WARNING: The " + def.getDocumentBaseURI()
                                    + " definition does not have a types section");
                }
                return resourcePropertyElements;
            }

            List elementList = types.getExtensibilityElements();
            // assume only on schema element for now
            if (elementList.size() > 1) {
                if (!quiet) {
                    System.err.println(
                            "WARNING: The types section in "
                                    + def.getDocumentBaseURI()
                                    + " contains more than one top level element.");
                }
                // maybe throw error
            }
            Element schema =
                    ((UnknownExtensibilityElement)
                            elementList.get(0)).getElement();

            XSModel schemaModel = loadSchema(schema, def);

            XSElementDeclaration resourcePropertiesElement =
                    schemaModel.getElementDeclaration(
                            resourceProperties.getLocalPart(),
                            resourceProperties.getNamespaceURI());

            if (resourcePropertiesElement != null) {
                XSComplexTypeDecl type =
                        (XSComplexTypeDecl)
                                resourcePropertiesElement.getTypeDefinition();
                XSParticle particle = type.getParticle();
                XSModelGroup sequence = (XSModelGroup) particle.getTerm();
                XSObjectList objectList = sequence.getParticles();

                for (int i = 0; i < objectList.getLength(); i++) {
                    XSParticle part = (XSParticle) objectList.item(i);
                    Object term = part.getTerm();
                    if (term instanceof XSElementDeclaration) {
                        XSElementDeclaration resourceProperty =
                                (XSElementDeclaration) term;
                        resourcePropertyElements.put(
                                new QName(resourceProperty.getNamespace(),
                                        resourceProperty.getName()), part);
                    } else {
                        throw new Exception("Invalid resource properties document " +
                                resourceProperties.toString());
                    }
                }
            } else {
                Map imports = def.getImports();
                Iterator importNSIterator = imports.values().iterator();
                while (importNSIterator.hasNext()
                        && resourcePropertyElements.isEmpty()) {
                    Vector importVector = (Vector) importNSIterator.next();
                    Iterator importIterator = importVector.iterator();
                    while (importIterator.hasNext()
                            && resourcePropertyElements.isEmpty()) {
                        Import importDef = (Import) importIterator.next();
                        // process imports
                        resourcePropertyElements.putAll(
                                getResourceProperties(
                                        resourceProperties,
                                        importDef.getDefinition(),
                                        documentLocations,
                                        quiet));
                    }
                }

                if (resourcePropertyElements.isEmpty()) {
                    throw new Exception("Unable to resolve resource properties " +
                            resourceProperties.toString());
                }
            }

            populateLocations(schemaModel, documentLocations, def);
        }
        return resourcePropertyElements;
    }

    private void fixNameSpaces(PortType porttype,
                               Definition definition) {
        List operations = porttype.getOperations();
        Iterator operationsIterator = operations.iterator();

        while (operationsIterator.hasNext()) {
            Operation operation = (Operation) operationsIterator.next();
            Input input = operation.getInput();
            if (input != null) {
                Message inMessage = input.getMessage();

                if (inMessage != null &&
                        definition.getPrefix(
                                inMessage.getQName().getNamespaceURI()) == null) {
                    definition.addNamespace(
                            "gtwsdl"
                                    + String.valueOf(
                                    this.nameSpaceCounter++),
                            inMessage.getQName().getNamespaceURI());
                }
            }

            Output output = operation.getOutput();

            if (output != null) {
                Message outMessage = output.getMessage();
                if (outMessage != null &&
                        definition.getPrefix(
                                outMessage.getQName().getNamespaceURI()) == null) {
                    definition.addNamespace(
                            "gtwsdl"
                                    + String.valueOf(
                                    this.nameSpaceCounter++),
                            outMessage.getQName().getNamespaceURI());
                }
            }

            Map faults = operation.getFaults();

            if (faults != null) {
                Iterator faultIterator = faults.values().iterator();
                while (faultIterator.hasNext()) {
                    Message faultMessage =
                            ((Fault) faultIterator.next()).getMessage();
                    if (definition.getPrefix(
                            faultMessage.getQName().getNamespaceURI()) == null) {
                        definition.addNamespace(
                                "gtwsdl"
                                        + String.valueOf(
                                        this.nameSpaceCounter++),
                                faultMessage.getQName().getNamespaceURI());
                    }
                }
            }
        }
    }

    //Todo: move to utility class?

    protected static String getRelativePath(String srcPathURI,
                                            String destPathURI)
            throws IOException {
        String destPath = URLDecoder.decode(destPathURI.substring(5), ENC);
        String srcPath = URLDecoder.decode(srcPathURI.substring(5), ENC);

        return RelativePathUtil.getRelativeFileName(new File(destPath),
                new File(srcPath));
    }

}
