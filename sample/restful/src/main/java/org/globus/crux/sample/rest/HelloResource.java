package org.globus.crux.sample.rest;

import org.globus.crux.sample.service.SampleService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/hello")
public class HelloResource {
    private SampleService service;

    public SampleService getService() {
        return service;
    }

    public void setService(SampleService service) {
        this.service = service;
    }

    @GET
    @Path("{greeting}")
    public String sayHello(@PathParam("greeting") String greeting){
        return service.sayHello(greeting);
    }
}
