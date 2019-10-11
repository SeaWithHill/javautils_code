package security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	/**
	 * 提供16位和32位md5加密
	 * 
	 * @param src
	 *            明文
	 * @param length
	 *            加密长度 16或32位加密，默认32位
	 * @return
	 */
	public static String Md5(String src, int length) {
		try {
			if (null == src) {
				return null;
			}
			String returnValue = "";
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(src.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(i));
			}
			switch (length) {
			// 16位的加密
			case 16:
				returnValue = buf.toString().substring(8, 24);
				break;
			// 32位的加密
			case 32:
				returnValue = buf.toString();
				break;
			// 默认32位的加密
			default:
				returnValue = buf.toString();
				break;
			}
			return returnValue;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		String srcStr = "春宵一刻值千金，花有清香月有阴；歌管楼台声细细，秋千院落夜沉沉！";
		System.out.println("原始信息：" + srcStr);
		System.out.println("MD5加密后(密文长度16位)：" + Md5(srcStr, 16));
		System.out.println("MD5加密后(密文长度32位)：" + Md5(srcStr, 32));
	}
}
