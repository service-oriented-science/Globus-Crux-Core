package com.counter;


public class CounterResource {
    private long id;
    private int value;
    private int status;
    public static final int INITIALIZED =0;

    public CounterResource() {
    }

    public CounterResource(long id){
        this.id = id;
        this.value = 0;
        this.status = INITIALIZED;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
