package com.counter;

import org.globus.crux.IDGenerator;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author turtlebender
 */
public class SimpleIDGenerator implements IDGenerator<Long>{
    AtomicLong id = new AtomicLong(0);
    
    public Long getNextId() {
        return id.getAndIncrement();
    }
}
