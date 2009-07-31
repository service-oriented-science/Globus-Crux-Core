package org.cyberaide.ws.mediator;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

import org.cyberaide.account.IUserAccount;
import org.cyberaide.account.UserAccountMem;
import org.cyberaide.account.UserAccountUtil;
import org.cyberaide.account.UserAccountFile;

public class MediatorServicePasswdCallback implements CallbackHandler{
  private static IUserAccount accounts;

  public MediatorServicePasswdCallback(){
    //accounts = new UserAccountMem();
    //accounts.addUser("grid", "welcome");
    //accounts.addUser("grid2", "welcome2");
    //~/.cyberaide.users
    accounts = new UserAccountFile();
  }
  
  public void handle(Callback[] callbacks)
                throws IOException, UnsupportedCallbackException {
      for(Callback cb: callbacks){
        if(cb instanceof WSPasswordCallback){
          WSPasswordCallback pc = (WSPasswordCallback)cb;
          int usage = pc.getUsage();
          String user = pc.getIdentifer();
          String password = pc.getPassword();
          //System.out.println("User: " + user + " / Password: " + password);
          
          if(usage == WSPasswordCallback.USERNAME_TOKEN_UNKNOWN){
            if(!accounts.isValid(user, password)){
              throw new SecurityException("Wrong user credential");
            }
          } else if(usage == WSPasswordCallback.USERNAME_TOKEN){
            pc.setPassword(accounts.getPassword(user));
          }
        }
      }
	}
}
