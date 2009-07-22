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

import javax.xml.namespace.QName;

public interface WSDLPreprocessorConstants
{
    public static final String WSDL_NS = "http://schemas.xmlsoap.org/wsdl/";
    public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema";
    public static final String WSDLPP_NS =
        "http://www.globus.org/namespaces/2004/10/WSDLPreprocessor";
    public static final String WSRP_NS =
        "http://docs.oasis-open.org/wsrf/rp-2";
    public static final QName EXTENDS = new QName(WSDLPP_NS,
                                                  "extends");
    public static final QName RP = new QName(WSRP_NS,
                                             "ResourceProperties");
    
    public static final String WSA_NS =
        "http://www.w3.org/2005/08/addressing";

    public static final String WSA_NS_WS =
            "http://www.w3.org/2006/05/addressing/wsdl";

    public static final QName WSA_ACTION =
        new QName(WSA_NS_WS, "Action");
}
