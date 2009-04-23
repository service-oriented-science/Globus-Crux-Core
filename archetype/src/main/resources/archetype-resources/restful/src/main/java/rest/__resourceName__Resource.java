#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.rest;

import ${package}.service.SampleService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;


@Path("/hello")
public class ${resourceName}Resource {
    private ${serviceName} service;

    public ${serviceName} getService() {
        return service;
    }

    public void setService(${serviceName} service) {
        this.service = service;
    }

    @GET
    @Path("{greeting}")
    public String sayHello(@PathParam("greeting") String greeting){
        return service.sayHello(greeting);
    }
}
