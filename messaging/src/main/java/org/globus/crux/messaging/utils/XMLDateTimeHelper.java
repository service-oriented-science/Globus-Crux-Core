package org.globus.crux.messaging.utils;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.Duration;
import java.util.GregorianCalendar;

public class XMLDateTimeHelper {
    private static DatatypeFactory datatypeFactory;

    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static XMLGregorianCalendar parseTerminationTime(String value) {
        try {
            Duration d = datatypeFactory.newDuration(value);
            XMLGregorianCalendar c = getCurrentTime();
            c.add(d);
            return c;
        } catch (Exception e) {
            // Ignore
        }
        try {
            Duration d = datatypeFactory.newDurationDayTime(value);
            XMLGregorianCalendar c = getCurrentTime();
            c.add(d);
            return c;
        } catch (Exception e) {
            // Ignore
        }
        try {
            Duration d = datatypeFactory.newDurationYearMonth(value);
            XMLGregorianCalendar c = getCurrentTime();
            c.add(d);
            return c;
        } catch (Exception e) {
            // Ignore
        }
        try {
            return datatypeFactory.newXMLGregorianCalendar(value);
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }

    protected static XMLGregorianCalendar getCurrentTime() {
        return datatypeFactory.newXMLGregorianCalendar(new GregorianCalendar());
    }
}