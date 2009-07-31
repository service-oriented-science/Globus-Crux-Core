package org.cyberaide.ws.mediator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import org.apache.cxf.message.Message;
import org.apache.cxf.interceptor.Fault;

/*
 * Interceptor which "logs" to standard output
 */
public class MediatorServiceMsgMon extends AbstractPhaseInterceptor {

    public MediatorServiceMsgMon() {
        super(Phase.RECEIVE);        
    }

    public void handleMessage(Message message) throws Fault {
        processMessage(message);
    }
    
    public void handleFault(Message message) {
        processMessage(message);
    }
    
    protected void processMessage(Message message) {
        InputStream is = (InputStream) message.getContent(InputStream.class);

        /* Possible argument values for message.getContent(???) vary on the
         * phase and type of interceptor chain.
         * To determine possible values for the current interceptor:
         * Set myset = message.getContentFormats();
         * System.out.println(myset.toString()); 
         */
        
        if (is == null) {
            return;
        }
        
        System.out.println("----> Intercepted Soap Message is:");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = in.readLine()) != null) {
              buffer.append(line);
            }
            System.out.println(buffer.toString());

            // Necessary to re-insert message after it is used
            // so other interceptors can work with it.
            is = new ByteArrayInputStream(buffer.toString().getBytes());
            message.setContent(InputStream.class, is);
        } catch (Exception e) {
            System.out.println("Error reading message: " + e.getMessage());
        }
        System.out.println("<---- End message");
    }    
}
