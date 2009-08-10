package com.counter;


public interface CounterResource {

    public javax.xml.ws.wsaddressing.W3CEndpointReference createCounter();

    public int add(int value);
}
