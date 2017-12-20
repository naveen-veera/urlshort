package net.orfdev;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Base62Test {

	private String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	@Test
	public void testEncodeBase62() {
		
		int n = 0;
		String str = "6JaY2";
		char[] chars = str.toCharArray();
		n += ALPHABET.indexOf(chars[0]) * (int) Math.pow(62, 4);
		n += ALPHABET.indexOf(chars[1]) * (int) Math.pow(62, 3);
		n += ALPHABET.indexOf(chars[2]) * (int) Math.pow(62, 2);
		n += ALPHABET.indexOf(chars[3]) * (int) Math.pow(62, 1);
		n += ALPHABET.indexOf(chars[4]) * (int) Math.pow(62, 0);
		assertEquals(str, Base62.encode(n));
	}
	
	@Test
	public void testDecodeBase62() {
		
		String str = "6JaY2";
		assertEquals(99424938, Base62.decode(str));
	}

}
