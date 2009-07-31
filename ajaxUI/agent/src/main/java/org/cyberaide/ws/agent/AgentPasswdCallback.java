package org.cyberaide.ws.agent;

import java.io.Console;
import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

public class AgentPasswdCallback implements CallbackHandler {

  public void handle(Callback[] callbacks)
              throws IOException, UnsupportedCallbackException {
  
    WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
    // set the password for the message. Single user mode for now..
    //System.out.print("Password: ");
    //Console cons = System.console();
    //char[] passwd;
    //passwd = cons.readPassword();
    //String password = new String(passwd);
    String password = "welcome";
    pc.setPassword(password);
  }
}
