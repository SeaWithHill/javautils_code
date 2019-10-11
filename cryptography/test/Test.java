package test;

import java.security.KeyPair;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import security.AESUtils;
import security.MD5Utils;
import security.RSAUtils;
import security.SM3Utils;
import security.SM4Utils;

public class Test {
	// 128-32位16进制；256-64位16进制
	public static int DEFAULT_KEY_SIZE = 128;
	

	public static void aesTest() {
		// 示例
		String cipherText = AESUtils.ecodes("一片春愁待酒浇，江上舟摇，楼上帘招。秋娘渡与泰娘桥，风又飘飘，雨又萧萧。", "1qaz@SX#EDC4rfv", 256);
		System.out.println("密文: " + cipherText);

		String clearText = AESUtils.decodes(cipherText, "1qaz@SX#EDC4rfv", 256);
		System.out.println("明文：" + clearText);
	}

	public static void md5Test() {
		String srcStr = "春宵一刻值千金，花有清香月有阴；歌管楼台声细细，秋千院落夜沉沉！";
		System.out.println("原始信息：" + srcStr);
		System.out.println("MD5加密后(密文长度16位)：" + MD5Utils.Md5(srcStr, 16));
		System.out.println("MD5加密后(密文长度32位)：" + MD5Utils.Md5(srcStr, 32));
	}

	public static void rsaTest() throws Exception {
		String srcStr = "春宵一刻值千金，花有清香月有阴；歌管楼台声细细，秋千院落夜沉沉！";
		// 获取非对称秘钥
		KeyPair keyPair = RSAUtils.generateKeyPair(4196);

		System.out.println("公钥：" + RSAUtils.keyEncrypt(keyPair.getPublic()));
		System.out.println("私钥：" + RSAUtils.keyEncrypt(keyPair.getPrivate()));

		String encriptMsg = RSAUtils.encrypt(srcStr, keyPair.getPublic());
		System.out.println("原始信息：" + srcStr + " 加密后的密文为：");
		System.out.println(encriptMsg);

		System.out.println("密文解密后为：" + RSAUtils.decrypt(encriptMsg, keyPair.getPrivate()));
	}

	public static void sm3Test() throws Exception {
		String srcStr = "春宵一刻值千金，花有清香月有阴；歌管楼台声细细，秋千院落夜沉沉！";
		// 密钥，可用中文
		String key = "春宵";
		// ******************************自定义密钥加密及校验*****************************************
		String hexStrByKey = SM3Utils.encrypt(srcStr, key);
		System.out.println("        带密钥加密后的密文：" + hexStrByKey);

		System.out.println("明文(带密钥)与密文校验结果：" + SM3Utils.verify(srcStr, key, hexStrByKey));
		System.out.println(
				"++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		// ******************************无密钥的加密及校验******************************************
		String hexStrNoKey = SM3Utils.encrypt(srcStr);
		System.out.println("        不带密钥加密后的密文：" + hexStrNoKey);

		System.out.println("明文(不带密钥)与密文校验结果：" + SM3Utils.verify(srcStr, hexStrNoKey));
	}

	public static void sm4Test() throws Exception {
		String srcStr = "春宵一刻值千金，花有清香月有阴；歌管楼台声细细，秋千院落夜沉沉！";
		// 自定义的32位16进制密钥
		// String key = "86C63180C2806ED1F47B859DE501215B";
		// 自动生成密钥
		String key = ByteUtils.toHexString(SM4Utils.autoGenerateKey(DEFAULT_KEY_SIZE));
		// 加密
		String cipher = SM4Utils.encryptByEcb(srcStr, key);

		System.out.println("自动生成的密钥：" + key);
		// 密文输出
		System.out.println("加密后的密文：" + cipher);
		// 校验
		System.out.println("校验密文是否为明文加密所得：" + SM4Utils.verifyByEcb(key, cipher, srcStr));
		// 解密
		srcStr = SM4Utils.decryptEcb(key, cipher);
		System.out.println("采用密钥：" + key);
		System.out.println("解密后的明文：" + srcStr);
	}

	public static void main(String[] args) throws Exception {
		System.out.println("☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆CAT加解密测试☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆");
		Test.aesTest();
		System.out.println("");
		System.out.println("☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆");
		System.out.println("");
		Test.md5Test();
		System.out.println("");
		System.out.println("☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆");
		System.out.println("");
		Test.rsaTest();
		System.out.println("");
		System.out.println("☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆");
		System.out.println("");
		Test.sm3Test();
		System.out.println("");
		System.out.println("☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆");
		System.out.println("");
		Test.sm4Test();
	}

}
