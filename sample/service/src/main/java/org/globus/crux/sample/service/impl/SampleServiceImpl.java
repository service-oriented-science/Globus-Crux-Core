package org.globus.crux.sample.service.impl;

import org.globus.crux.sample.service.SampleService;

public class SampleServiceImpl implements SampleService {
    public String sayHello(String request) {
        return "Echoing: " + request;
    }
}
