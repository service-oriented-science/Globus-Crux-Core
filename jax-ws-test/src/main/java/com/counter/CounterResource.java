package com.counter;


public class CounterResource {
    private long id;
    private CounterRP counterRP;
    private int status;
    public static final int INITIALIZED =0;

    public CounterResource() {
    }

    public CounterResource(long id){
        this.id = id;
        this.counterRP = new CounterRP();
        this.status = INITIALIZED;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CounterRP getCounterRP() {
        return counterRP;
    }

    public void setCounterRP(CounterRP counterRP) {
        this.counterRP = counterRP;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
