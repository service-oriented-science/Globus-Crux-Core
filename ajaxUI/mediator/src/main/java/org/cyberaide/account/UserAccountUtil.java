package org.cyberaide.account;

import java.util.Random;

public class UserAccountUtil{
  private static final int MAXASCIIVAL = 127;
  private static final int MINASCIIVAL = 32;
  private static final int PASSWDLEN = 10;
  private static Random random = new Random();
  public static String generateRandomPasswd(){
    StringBuffer pass = new StringBuffer();
    int passlen = 0;
    while(passlen < PASSWDLEN){
      int randChar = random.nextInt(MAXASCIIVAL);
      if(randChar > MINASCIIVAL){
        pass.append(randChar);
        passlen++;
      }
    }
    return new String(pass);
  }
  
  public static boolean isValidPasswd(String passwd){
    return true;
  }
}
