package org.cyberaide.account;

public interface IUserAccount{
  public boolean addUser(String username, String password);
  public boolean delUser(String username);
  public boolean changePasswd(String username, String newPasswd);
  public String resetPasswd(String username);
  public boolean isValid(String username, String passwd);
  public String getPassword(String username);
}
