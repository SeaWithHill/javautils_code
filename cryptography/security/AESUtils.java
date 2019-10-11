package security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 
 * AES加密与解密工具类
 * 
 * @author 鬼面书生
 * 
 *
 */
public class AESUtils {
	static final String AES = "AES";
	static final String UTF_8 = "UTF-8";
	static final String SHA1_PRNG = "SHA1PRNG";

	/**
	 * 
	 * Discription:加密
	 * 
	 * @param clearText
	 *            明文
	 * @param key
	 *            加解密密钥
	 * @param keySize
	 *            密钥长度
	 * @return String 密文
	 * 
	 */
	public static String ecodes(String clearText, String key, int keySize) {
		if (clearText == null || clearText.length() < 1) {
			return null;
		}
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(AES);
			SecureRandom random = SecureRandom.getInstance(SHA1_PRNG);
			random.setSeed(key.getBytes());
			kgen.init(keySize, random);
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, AES);
			Cipher cipher = Cipher.getInstance("AES");
			byte[] byteContent = clearText.getBytes(UTF_8);
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			byte[] byteRresult = cipher.doFinal(byteContent);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteRresult.length; i++) {
				String hex = Integer.toHexString(byteRresult[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sb.append(hex.toUpperCase());
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * Discription:解密
	 * 
	 * @param cipherText
	 *            密文
	 * @param key
	 *            加解密密钥
	 * @param keySize
	 *            密钥长度
	 * @return String 明文
	 * 
	 * 
	 */
	public static String decodes(String cipherText, String key, int keySize) {
		if (cipherText == null || cipherText.length() < 1) {
			return null;
		}
		if (cipherText.trim().length() < 19) {
			return cipherText;
		}
		byte[] byteRresult = new byte[cipherText.length() / 2];
		for (int i = 0; i < cipherText.length() / 2; i++) {
			int high = Integer.parseInt(cipherText.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(cipherText.substring(i * 2 + 1, i * 2 + 2), 16);
			byteRresult[i] = (byte) (high * 16 + low);
		}
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(AES);
			SecureRandom random = SecureRandom.getInstance(SHA1_PRNG);
			random.setSeed(key.getBytes());
			kgen.init(keySize, random);
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, AES);
			Cipher cipher = Cipher.getInstance(AES);
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			byte[] result = cipher.doFinal(byteRresult);
			return new String(result);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		try {
			// 示例
			String cipherText = AESUtils.ecodes("一片春愁待酒浇，江上舟摇，楼上帘招。秋娘渡与泰娘桥，风又飘飘，雨又萧萧。", "1qaz@SX#EDC4rfv", 256);
			System.out.println("密文: " + cipherText);

			String clearText = AESUtils.decodes(cipherText, "1qaz@SX#EDC4rfv", 256);
			System.out.println("明文：" + clearText);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
