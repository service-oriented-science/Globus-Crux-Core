#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${artifactId}.impl;

import ${package}.${artifactId}.SampleService;

public class ${serviceName}Impl implements ${serviceName} {
    public String sayHello(String request) {
        return "Echoing: " + request;
    }
}
