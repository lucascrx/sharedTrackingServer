package security;

import java.util.Random;

/**provides guidance public and private ID and ensures they are unique*/
public class TokenGenerator {

	static final String Alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static final int tokenLength = 25;
	static Random rnd = new Random();

	public String randomString(){
	   StringBuilder sb = new StringBuilder(tokenLength);
	   for( int i = 0; i < tokenLength; i++ ) 
	      sb.append( Alphabet.charAt( rnd.nextInt(Alphabet.length()) ) );
	   return sb.toString();
	}



}
