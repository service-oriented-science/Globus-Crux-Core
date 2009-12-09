package org.globus.crux.messaging.utils;

import javax.xml.namespace.QName;
import javax.persistence.Transient;
import javax.persistence.Basic;
import javax.persistence.Id;
import javax.persistence.Entity;
import java.net.URL;

/**
 * @author Tom Howe
 * @version 1.0
 * @since 1.0
 */
@Entity
public class EndpointReference {
    @Basic
    private URL address;
    @Transient
    private QName portName;
    @Basic
    private String portNameString;
    @Transient
    private QName serviceName;
    @Basic
    private String serviceNameString;
    @Id
    private String referenceKey;

    public URL getAddress() {
        return address;
    }

    public void setAddress(URL address) {
        this.address = address;
    }

    public QName getPortName() {
        return portName;
    }

    public String getPortNameString(){
        return portNameString;
    }

    public void setPortName(QName portName) {
        this.portName = portName;
        this.portNameString = portName.toString();
    }

    public QName getServiceName() {
        return serviceName;
    }

    public String getServiceNameString(){
        return serviceNameString;
    }

    public void setServiceName(QName serviceName) {
        this.serviceName = serviceName;
        this.serviceNameString = serviceName.toString();
    }

    public String getReferenceKey() {
        return referenceKey;
    }

    public void setReferenceKey(String referenceKey) {
        this.referenceKey = referenceKey;
    }

    public void setPortNameString(String portNameString) {
        this.portNameString = portNameString;
        portName = QName.valueOf(portNameString);
    }

    public void setServiceNameString(String serviceNameString) {
        this.serviceNameString = serviceNameString;
        serviceName = QName.valueOf(serviceNameString);
    }
}
