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
package com.counter;

import java.util.Properties;


import org.apache.cxf.transport.servlet.CXFServlet;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;

/**
 * FILL ME
 *
 * @author ranantha@mcs.anl.gov
 */
public class TestServer {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        Context context = new Context();
        
        ServletHolder servletHolder = new ServletHolder();

        servletHolder.setInitOrder(1);
        servletHolder.setServlet(new CXFServlet());
        servletHolder.setName("CXFServlet");
        servletHolder.setDisplayName("CXF Servlet");
        context.addServlet(servletHolder, "/*");
        context.addEventListener(new ContextLoaderListener());
        Properties initParams = new Properties();
        initParams.put("contextConfigLocation", "classpath:/beans.xml,classpath:/factorybeans.xml");
        context.setInitParams(initParams);
        server.addHandler(context);
        server.start();
    }
}
