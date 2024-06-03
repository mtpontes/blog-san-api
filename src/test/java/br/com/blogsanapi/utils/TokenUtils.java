package br.com.blogsanapi.utils;

import java.util.Base64;

public class TokenUtils {

	public static boolean isValidTokenFormat(String token) {
		String[] parts = token.split("\\.");

		if(parts.length != 3) return false;

		try {
			Base64.getUrlDecoder().decode(parts[0]);
			Base64.getUrlDecoder().decode(parts[1]);
			Base64.getUrlDecoder().decode(parts[2]);
			return true; 

		} catch (IllegalArgumentException ex) {
			return false;
		}
	}
}