package org.globus.crux.stateful.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Basic;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Calendar;

/**
 * @author turtlebender
 */
@Entity
public class MyPersistentStatefulBean {
    @Id
    private int id;

    @Basic
    private String name;

    @Temporal(TemporalType.DATE)
    private Calendar lastUpdate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Calendar lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
