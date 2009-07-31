package org.cyberaide.account;

import java.util.Map;
import java.util.Properties;

//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;

public class UserAccountMem implements IUserAccount{
  protected Properties users;
  //protected MessageDigest md;
  public UserAccountMem(){
    users = new Properties();
    //try{
    //    md = MessageDigest.getInstance("SHA-1");
    //} catch (NoSuchAlgorithmException e) {}
  }
  
  public synchronized boolean addUser(String username, String password){
    if(users.containsKey(username)){
      return false;
    } else {
      if(UserAccountUtil.isValidPasswd(password)){
        //String passwordDigest = new String(md.digest(password.getBytes()));
        //users.setProperty(username, passwordDigest);
        users.setProperty(username, password);
        return true;
      } else {
        return false;
      }
    }
  }
  
  public synchronized boolean delUser(String username){
    if(users.containsKey(username)){
      return (users.remove(username) != null);
    } else {
      return false;
    }
  }
  
  public synchronized boolean changePasswd(String username, String newPasswd){
    if(users.containsKey(username)){
      if(UserAccountUtil.isValidPasswd(newPasswd)){
        //String passwordDigest = new String(md.digest(newPasswd.getBytes()));
        //users.setProperty(username, passwordDigest);
        users.setProperty(username, newPasswd);
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }
  
  public synchronized String resetPasswd(String username){
    String newPasswd = UserAccountUtil.generateRandomPasswd();
    if(users.containsKey(username)){
      //String passwordDigest = new String(md.digest(newPasswd.getBytes()));
      //users.setProperty(username, passwordDigest);
      users.setProperty(username, newPasswd);
      return newPasswd;
    } else {
      return null;
    }
  }
  
  public synchronized boolean isValid(String username, String passwd){
    if(users.containsKey(username)){
      return (users.getProperty(username).equals(passwd));
    } else {
      return false;
    }
  }
      
  public synchronized String getPassword(String username){
    if(users.containsKey(username)){
      return users.getProperty(username);
    } else {
      return null;
    }
  }
}
