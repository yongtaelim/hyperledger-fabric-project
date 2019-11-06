package hyperledger.api.utils;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.cxf.common.util.Base64Utility;

public class RSA {
	public static final String KEY_ALGORITHM = "RSA";
	
	private static final int DEFAULT_KEY_SIZE = 2048;
	private static final String PUBLIC_KEY = "publicKey";
    private static final String PRIVATE_KEY = "privateKey";
	
	public static Map<String, Object> genKeyPair() throws Exception {
		return genKeyPair(DEFAULT_KEY_SIZE);
	}
	public static Map<String, Object> genKeyPair(int key_size) throws Exception {
	    KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
	    keyPairGen.initialize(key_size);
	    KeyPair keyPair = keyPairGen.generateKeyPair();
	    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
	    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
	    Map<String, Object> keyMap = new HashMap<String, Object>(2);
	    keyMap.put(PUBLIC_KEY, publicKey);
	    keyMap.put(PRIVATE_KEY, privateKey);
	    return keyMap;
	}
	
	// RSA - Encrypt with public key
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
    		return encryptByPublicKey(data, publicKey, DEFAULT_KEY_SIZE);
    }
    public static byte[] encryptByPublicKey(byte[] data, String publicKey,int key_size) throws Exception {
        byte[] keyBytes = Base64Utility.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        //return cipher.doFinal(data);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        int max_encrypt_block = key_size / 8;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > max_encrypt_block) {
                cache = cipher.doFinal(data, offSet, max_encrypt_block);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * max_encrypt_block;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }
    
    
    
    // RSA - Decrypt with private key
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
    		return decryptByPrivateKey(encryptedData, privateKey, DEFAULT_KEY_SIZE);
    }
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey , int key_size) throws Exception {
        byte[] keyBytes = Base64Utility.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        
        //return cipher.doFinal(encryptedData);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        int max_decrypt_block = key_size / 8 ;
        
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > max_decrypt_block) {
                cache = cipher.doFinal(encryptedData, offSet, max_decrypt_block);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * max_decrypt_block;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }
    
 // RSA - Encrypt with public key
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
    		return encryptByPrivateKey(data, privateKey, DEFAULT_KEY_SIZE);
    }
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey,int key_size) throws Exception {
        byte[] keyBytes = Base64Utility.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        //return cipher.doFinal(data);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        int max_encrypt_block = key_size / 8;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > max_encrypt_block) {
                cache = cipher.doFinal(data, offSet, max_encrypt_block);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * max_encrypt_block;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }
    
    // RSA - Decrypt with private key
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception {
    		return decryptByPublicKey(encryptedData, publicKey, DEFAULT_KEY_SIZE);
    }
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey , int key_size) throws Exception {
        byte[] keyBytes = Base64Utility.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        
        //return cipher.doFinal(encryptedData);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        int max_decrypt_block = key_size / 8 ;
        
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > max_decrypt_block) {
                cache = cipher.doFinal(encryptedData, offSet, max_decrypt_block);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * max_decrypt_block;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }
    
    public static Map<String, Object> requestKey() {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> keyMap = new HashMap<String, Object>();
		try {
				keyMap = genKeyPair();
				result.put(PUBLIC_KEY, getPublicKey(keyMap));
				result.put(PRIVATE_KEY, getPrivateKey(keyMap));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
    }
    
    public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get(PRIVATE_KEY);
		return Base64Utility.encode(key.getEncoded());
	}
    
	public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
	    Key key = (Key) keyMap.get(PUBLIC_KEY);
	    return Base64Utility.encode(key.getEncoded());
	}
}
