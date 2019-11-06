package hyperledger.api.utils;

import java.nio.charset.StandardCharsets;

import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

public class HASH {
	public static String sha3String(String plaintxt) {
		Keccak.Digest256 digest256 = new Keccak.Digest256();
		byte[] hashBytes = digest256.digest(plaintxt.getBytes(StandardCharsets.UTF_8));
		return new String(Hex.encode(hashBytes));
	}
	
	public static byte[] sha3Byte(String plaintxt) {
		Keccak.Digest256 digest256 = new Keccak.Digest256();
		byte[] hashBytes = digest256.digest(plaintxt.getBytes(StandardCharsets.UTF_8));
		return Hex.encode(hashBytes);
	}
}
