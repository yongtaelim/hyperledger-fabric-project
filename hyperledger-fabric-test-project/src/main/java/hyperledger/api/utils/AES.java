package hyperledger.api.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AES {

	public static final String AES = "AES";
	public static final String AES_MODE = "AES/CBC/PKCS5Padding";
	
	
	public Map<String, Object> requestKey() {
		Map<String, Object> result = new HashMap<String, Object>();
		return result;
	}
	
	
	public static String encrypt(String symmetricKey, String initialVector, String plainTxt) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] keyData = symmetricKey.getBytes();
		SecretKey secureKey = new SecretKeySpec(keyData, AES);
		 
		// ��ȣȭ ���
		// ECB (Electronic Code Block) Mode
		// CBC(Cipher Block Chaining) Mode
		// CFB(Cipher FeedBack) Mode
		// OFB(Output FeedBack) Mode
		// CTR (CounTeR) Mode
		Cipher c = Cipher.getInstance(AES_MODE);
		c.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(initialVector.getBytes()));
		 
		byte[] encrypted = c.doFinal(plainTxt.getBytes("UTF-8"));
		String result = new String(Base64.encodeBase64(encrypted));
		
		return result;
	}
	
	
	public static String decrypt(String symmetricKey, String initialVector, String cipherTxt) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] keyData = symmetricKey.getBytes();
		SecretKey secureKey = new SecretKeySpec(keyData, AES);
		Cipher c = Cipher.getInstance(AES_MODE);
		c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(initialVector.getBytes("UTF-8")));
		 
		byte[] byteStr = Base64.decodeBase64(cipherTxt.getBytes());
		 
		return new String(c.doFinal(byteStr),"UTF-8");
	}
}
