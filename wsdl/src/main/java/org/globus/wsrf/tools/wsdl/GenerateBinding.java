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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.BindingFault;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Import;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPFault;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;

import com.ibm.wsdl.extensions.PopulatedExtensionRegistry;
import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;
import com.ibm.wsdl.extensions.soap.SOAPBindingImpl;
import com.ibm.wsdl.extensions.soap.SOAPBodyImpl;
import com.ibm.wsdl.extensions.soap.SOAPFaultImpl;
import com.ibm.wsdl.extensions.soap.SOAPOperationImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateBinding
{
    private String address = "localhost:8080/wsrf/services/";
    private String protocol = "http";
    private String portTypeFile;
    private String fileRoot;
    private boolean quiet;
    
    public static final String SOAP_NS =
            "http://schemas.xmlsoap.org/wsdl/soap/";
    public static final String HTTP_NS = 
            "http://schemas.xmlsoap.org/soap/http";
    
    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }
    
    public void setPortTypeFile(String portTypeFile)
    {
        this.portTypeFile = portTypeFile;
    }
      
    public void setFileRoot(String fileRoot)
    {
        this.fileRoot = fileRoot;
    }
       
    public void setQuiet(boolean quiet)
    {
        this.quiet = quiet;
    }
    
    public void execute() throws Exception {
        Log log = LogFactory.getLog(getClass());
        if (this.portTypeFile == null || this.portTypeFile.length() == 0) 
        {
            throw new Exception("Input file not specified");
        }
        if (this.fileRoot == null || this.fileRoot.length() == 0)
        {
            throw new Exception("File root not specified");
        }
        
        Definition portTypeDefinition;
        Definition bindingDefinition;
        Definition serviceDefinition;
        FileOutputStream bindingDefinitionOutput = null;
        FileOutputStream serviceDefinitionOutput = null;
        FileInputStream portTypeInput = null;

        try
        {
            portTypeFile = new File(this.portTypeFile).getCanonicalPath();
            portTypeInput = new FileInputStream(portTypeFile);
            WSDLFactory factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();
            WSDLWriter writer = factory.newWSDLWriter();
            reader.setFeature("javax.wsdl.verbose", false);
            reader.setFeature("javax.wsdl.importDocuments", true);
            portTypeDefinition = reader.readWSDL(null, portTypeFile);
            bindingDefinition = factory.newDefinition();
            serviceDefinition = factory.newDefinition();
            QName name = portTypeDefinition.getQName();
            if(name == null)
            {
                String baseName = (new File(portTypeFile)).getName();
                int pos = baseName.indexOf('.');
                name = new QName(portTypeDefinition.getTargetNamespace(),
                                 (pos == -1) ? 
                                 baseName : baseName.substring(0, pos));
            }

            bindingDefinition.setQName(new QName(name.getLocalPart()));
            serviceDefinition.setQName(new QName(name.getLocalPart()));

            bindingDefinition.setTargetNamespace(
                    portTypeDefinition.getTargetNamespace() +
                    "/bindings");
            bindingDefinition.setExtensionRegistry(
                    new PopulatedExtensionRegistry());
            bindingDefinition.addNamespace("soap", SOAP_NS);
            bindingDefinition.addNamespace(
                "porttype",
                portTypeDefinition.getTargetNamespace());
            String bindingFile = this.fileRoot + "_bindings.wsdl";
            bindingFile = new File(bindingFile).getCanonicalPath();
            bindingDefinitionOutput = new FileOutputStream(bindingFile);
            String relativePortTypeFile =
                    RelativePathUtil.getRelativeFileName(new File(portTypeFile),
                                                         new File(bindingFile));

            Import portTypeImport = bindingDefinition.createImport();
            portTypeImport.setLocationURI(relativePortTypeFile);
            portTypeImport.setNamespaceURI(
                    portTypeDefinition.getTargetNamespace());
            bindingDefinition.addImport(portTypeImport);
            serviceDefinition.setTargetNamespace(
                    portTypeDefinition.getTargetNamespace() +
                    "/service");
            serviceDefinition.setExtensionRegistry(
                    new PopulatedExtensionRegistry());
            serviceDefinition.addNamespace("soap", SOAP_NS);
            serviceDefinition.addNamespace(
                    "binding",
                    bindingDefinition.getTargetNamespace());
            String serviceFile = this.fileRoot + "_service.wsdl";
            serviceFile = new File(serviceFile).getCanonicalPath();
            serviceDefinitionOutput = new FileOutputStream(serviceFile);
            String relativeBindingFile =
                    RelativePathUtil.getRelativeFileName(new File(bindingFile),
                                                         new File(serviceFile));
            Import bindingImport = serviceDefinition.createImport();
            bindingImport.setLocationURI(relativeBindingFile);
            bindingImport.setNamespaceURI(
                    bindingDefinition.getTargetNamespace());
            serviceDefinition.addImport(bindingImport);
            Service service = serviceDefinition.createService();
            if(serviceDefinition.getQName().getLocalPart().endsWith("Service"))
            {
                service.setQName(serviceDefinition.getQName());
            }
            else
            {
                service.setQName(
                        new QName(serviceDefinition.getQName().getLocalPart()
                                  + "Service"));
            }
            Iterator portTypeIterator =
                    portTypeDefinition.getPortTypes().values().iterator();
            Binding binding;
            PortType portType;
            while(portTypeIterator.hasNext())
            {
                portType = (PortType) portTypeIterator.next();
                binding= processPortType(bindingDefinition,
                                         portType);
                bindingDefinition.addBinding(binding);
                service.addPort(createPort(serviceDefinition,
                                           portType,
                                           binding));
            }
            writer.writeWSDL(bindingDefinition, bindingDefinitionOutput);
            serviceDefinition.addService(service);
            writer.writeWSDL(serviceDefinition, serviceDefinitionOutput);
            if (!this.quiet)
            {
                log.info("Generated " + bindingFile);
                log.info("Generated " + serviceFile);                              
            }
        }
        finally
        {
            if(portTypeInput != null)
            {
                try
                {
                    portTypeInput.close();
                }
                catch(Exception io)
                {
                }
            }
            if (bindingDefinitionOutput != null)
            {
                try
                {
                    bindingDefinitionOutput.close();
                }
                catch(Exception io)
                {
                }
            }
            if (serviceDefinitionOutput != null)
            {
                try
                {
                    serviceDefinitionOutput.close();
                }
                catch(Exception io)
                {
                }
            }
        }
    }

    private Port createPort(Definition serviceDefinition,
                            PortType portType,
                            Binding binding)
    {
        Port port = serviceDefinition.createPort();
        port.setName(portType.getQName().getLocalPart()
                     + "Port");
        port.setBinding(binding);
        SOAPAddress soapAddress = new SOAPAddressImpl();
        soapAddress.setLocationURI(this.protocol + "://" + this.address);
        port.addExtensibilityElement(soapAddress);
        return port;
    }

    private Binding processPortType(Definition bindingDefinition, 
                                    PortType portType)
        throws Exception
    {
        Binding binding = bindingDefinition.createBinding();
        binding.setPortType(portType);
        binding.setUndefined(false);
        binding.setQName(
                new QName(bindingDefinition.getTargetNamespace(),
                          portType.getQName().getLocalPart()
                          + "SOAPBinding"));
        SOAPBinding soapBinding = new SOAPBindingImpl();
        soapBinding.setTransportURI(HTTP_NS);
        soapBinding.setStyle("document");
        binding.addExtensibilityElement(soapBinding);
        Iterator operationIterator =
                portType.getOperations().iterator();
        BindingOperation bindingOperation;
        while(operationIterator.hasNext())
        {
            try
            {
                bindingOperation =
                processOperation(bindingDefinition,
                                 portType.getQName(),
                                 (Operation) operationIterator.next());
                binding.addBindingOperation(bindingOperation);
            }
            catch(Exception e)
            {
                throw new Exception("Failed to add operation to binding", e);
            }
        }
        return binding;
    }

    private BindingOperation processOperation(Definition bindingDefinition, 
                                              QName portTypeQName,
                                              Operation operation)
        throws Exception
    {
        BindingOperation bindingOperation =
                bindingDefinition.createBindingOperation();
        bindingOperation.setOperation(operation);
        bindingOperation.setName(operation.getName());
        Input input = operation.getInput();
        if(input == null)
        {
            throw new Exception("Operation is lacking a <input> element");
        }
        Message inputMessage = input.getMessage();
        if(inputMessage == null)
        {
            throw new Exception("Input is lacking a <message> element");
        }
        List inputParts = inputMessage.getOrderedParts(null);
        if (inputParts == null || inputParts.isEmpty())
        {
            throw new Exception("No <part> element for input message: "  +
                                inputMessage.getQName());

        }
        SOAPOperation soapOperation = new SOAPOperationImpl();
//        String action = AddressingUtils.getInputAction(portTypeQName,
//                                                       operation);
//        soapOperation.setSoapActionURI(action);
        bindingOperation.addExtensibilityElement(soapOperation);
        BindingInput bindingInput =
                bindingDefinition.createBindingInput();
        SOAPBody soapBody = new SOAPBodyImpl();
        soapBody.setUse("literal");
        bindingInput.addExtensibilityElement(soapBody);
        bindingOperation.setBindingInput(bindingInput);
        if(operation.getOutput() != null)
        {
            BindingOutput bindingOutput =
                    bindingDefinition.createBindingOutput();
            soapBody = new SOAPBodyImpl();
            soapBody.setUse("literal");
            bindingOutput.addExtensibilityElement(soapBody);
            bindingOperation.setBindingOutput(bindingOutput);
        }
        Iterator faulIterator =
                operation.getFaults().values().iterator();
        BindingFault bindingFault;
        while(faulIterator.hasNext())
        {
            bindingFault = processFault(
                    (Fault) faulIterator.next(),
                    bindingDefinition);
            bindingOperation.addBindingFault(bindingFault);
        }
        return bindingOperation;
    }

    private BindingFault processFault(Fault fault,
                                      Definition bindingDefinition)
    {
        SOAPFault soapFault = new SOAPFaultImpl();
        soapFault.setName(fault.getName());
        soapFault.setUse("literal");
        BindingFault bindingFault =
                bindingDefinition.createBindingFault();
        bindingFault.addExtensibilityElement(soapFault);
        bindingFault.setName(fault.getName());
        return bindingFault;
    }
}
