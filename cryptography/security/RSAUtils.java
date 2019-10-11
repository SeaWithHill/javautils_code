package security;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

/**
 * 
 * RSA加密与解密工具类
 * 
 * @author 鬼面书生
 * 
 *
 */
public class RSAUtils {

	/**
	 * 生成密钥对
	 * 
	 * @param keySize
	 *            密钥长度
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static KeyPair generateKeyPair(int keySize) {
		try {
			KeyPairGenerator pairgen = KeyPairGenerator.getInstance("RSA");
			SecureRandom random = new SecureRandom();
			pairgen.initialize(keySize, random);
			return pairgen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将密钥采用Base64加密并返回加密后的密文
	 * 
	 * @param key
	 *            密钥
	 * @return
	 */
	public static String keyEncrypt(Key key) {
		return new String(Base64.encodeBase64(key.getEncoded()));
	}

	/**
	 * 利用公钥进行加密
	 * 
	 * @param str
	 * @param publicKey(encodeBase64后的公钥)
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String str, String publicKey) throws Exception {

		// 对公钥进行base64编码
		byte[] decoded = Base64.decodeBase64(publicKey);
		RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
				.generatePublic(new X509EncodedKeySpec(decoded));
		// RSA加密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		return Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));

	}

	public static String encrypt(String str, byte[] decoded) throws Exception {

		// 对公钥进行base64编码
		RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
				.generatePublic(new X509EncodedKeySpec(decoded));
		// RSA加密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		return Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));

	}

	/**
	 * 直接利用公钥加密
	 * 
	 * @param str
	 *            明文
	 * @param publicKey
	 *            公钥
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String str, Key publicKey) throws Exception {
		RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
				.generatePublic(new X509EncodedKeySpec(publicKey.getEncoded()));
		// RSA加密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		return Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
	}

	/**
	 * 利用私钥解密
	 * 
	 * @param str
	 *            加密字符串
	 * @param privateKey
	 *            私钥（encodeBase64后的私钥）
	 * @return 明文
	 * @throws Exception
	 *             解密过程中的异常信息
	 */
	public static String decrypt(String str, String privateKey) throws Exception {
		// 64位解码加密后的字符串
		byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));

		// base64编码的私钥
		byte[] decoded = Base64.decodeBase64(privateKey);
		RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
				.generatePrivate(new PKCS8EncodedKeySpec(decoded));
		// RSA解密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, priKey);
		return new String(cipher.doFinal(inputByte));
	}

	public static String decrypt(String str, byte[] decoded) throws Exception {
		// 64位解码加密后的字符串
		byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));

		RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
				.generatePrivate(new PKCS8EncodedKeySpec(decoded));
		// RSA解密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, priKey);
		return new String(cipher.doFinal(inputByte));
	}

	/**
	 * 直接利用私钥解密
	 * 
	 * @param str
	 *            密文
	 * @param privateKey
	 *            私钥
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String str, Key privateKey) throws Exception {
		// 64位解码加密后的字符串
		byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));

		RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
				.generatePrivate(new PKCS8EncodedKeySpec(privateKey.getEncoded()));
		// RSA解密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, priKey);
		return new String(cipher.doFinal(inputByte));
	}

	public static void main(String[] args) {
		try {
			String srcStr = "春宵一刻值千金，花有清香月有阴；歌管楼台声细细，秋千院落夜沉沉！";
			// 获取非对称密钥
			KeyPair keyPair = RSAUtils.generateKeyPair(4196);

			System.out.println("公钥：" + keyEncrypt(keyPair.getPublic()));
			System.out.println("私钥：" + keyEncrypt(keyPair.getPrivate()));

			String encriptMsg = RSAUtils.encrypt(srcStr, keyPair.getPublic());
			System.out.println("原始信息：" + srcStr + " 加密后的密文为：");
			System.out.println(encriptMsg);

			System.out.println("密文解密后为：" + RSAUtils.decrypt(encriptMsg, keyPair.getPrivate()));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
