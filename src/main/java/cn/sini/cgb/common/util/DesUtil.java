package cn.sini.cgb.common.util;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * des加密工具
 *
 * @author huangwb
 * @version 1.0
 * @ClassName: DesUtil
 * @Desc:
 * @date 2016/12/17
 * @history v1.0
 */
public class DesUtil {
	private final static String DES = "DES";
	public static final int DESMODELDESENCRYPT = 1;
	public static final int DESMODELDESDECODE = 2;
	public static final int DESMODELTHREEDESENCRYPT = 3;
	public static final int DESMODELTHREEDESDECODE = 4;

	public static String encryptDES(String encryptString, String encryptKey) {
		IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
		SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
		byte[] encryptedData = null;
		try {
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
			encryptedData = cipher.doFinal(encryptString.getBytes("UTF8"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Base64.encode(encryptedData);
	}

	public static String decryptDES(String decryptString, String decryptKey) throws UnsupportedEncodingException {
		byte[] byteMi = new Base64().decode(decryptString);
		IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
		SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
		byte[] decryptedData = null;
		try {
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
			decryptedData = cipher.doFinal(byteMi);
		} catch (Exception e) {
			throw new UnsupportedEncodingException();
		}
		return decryptedData == null ? null : new String(decryptedData, "UTF8");
	}

	// 输入一串数据,每8字节分段异或1
	public static void Xor(byte[] in, byte[] out) {
		int i, j, len;

		// 赋初值0x00
		for (i = 0; i < 8; i++)
			out[i] = (byte) 0x00;

		len = 8;
		for (i = 0; i * 8 < in.length; i++) {
			// 最后一段若不足8字节按实际长度异或
			if (in.length - i * 8 < 8)
				len = in.length - i * 8;
			for (j = 0; j < len; j++)
				out[j] ^= in[i * 8 + j];
		}

		return;
	}

	// 输入一串数据,每8字节分段异或2
	public static byte[] Xor(byte[] in) {

		byte[] out = new byte[8];
		Xor(in, out);
		return out;
	}

	// 按指定长度异或1
	public static void Xor(byte[] in1, byte[] in2, int len, byte[] out) {
		for (int i = 0; i < len; i++)
			out[i] ^= in1[i] ^ in2[i];
		return;
	}

	// 按指定长度异或2
	public byte[] Xor(byte[] in1, byte[] in2, int len) {
		byte[] out = new byte[len];
		Xor(in1, in2, len, out);
		return out;
	}

	// desMode 算法:1-des加密 2-des解密 3-3des加密 4-3des解密
	// in: 长度必须为8的倍数
	// key: 单des加密时8字节长，3des加密时16字节或24字节长
	public static void Des(int desMode, byte[] in, byte[] key, byte[] out) {
		int mode = 0;
		String alg;
		byte[] currKey;

		if (desMode < 1 || desMode > 4 || key.length < 8 || (in.length % 8) != 0)
			return;
		if (desMode == 1 || desMode == 3)
			mode = Cipher.ENCRYPT_MODE;

		if (desMode == 2 || desMode == 4)
			mode = Cipher.DECRYPT_MODE;

		if (desMode <= 2 || key.length == 8) {
			alg = "DES";
			currKey = new byte[8];
			for (int i = 0; i < 8; i++)
				currKey[i] = key[i];
		} else {
			if (key.length < 16)
				return;
			alg = "DESede";
			currKey = new byte[24];
			for (int i = 0; i < 16; i++)
				currKey[i] = key[i];
			int k;
			if (key.length >= 24)
				k = 16;
			else
				k = 0;
			for (int i = 0; i < 8; i++)
				currKey[16 + i] = key[k + i];
		}

		try {
			SecretKey deskey = new SecretKeySpec(currKey, alg);
			Cipher cipher = Cipher.getInstance(alg + "/ECB/NoPadding");
			cipher.init(mode, deskey);

			byte[] outTmp = cipher.doFinal(in);

			for (int i = 0; i < outTmp.length; i++)
				out[i] = outTmp[i];

			return;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return;
	}

	/**
	 * 加解密
	 *
	 * @param desMode 算法:1-des加密 2-des解密 3-3des加密 4-3des解密
	 * @param in 长度必须为8的倍数
	 * @param key 单des加密时8字节长，3des加密时16字节或24字节长
	 * @return
	 */
	public static byte[] Des(int desMode, byte[] in, byte[] key) {
		byte[] out = new byte[in.length];
		Des(desMode, in, key, out);
		return out;
	}

	// 16进制byte数组转为可显示字符串(OneTwo)
	public static String Hex2Str(byte[] in) {
		final byte[] hexString = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		byte[] out = new byte[in.length * 2];

		for (int i = 0; i < in.length; i++) {
			out[2 * i] = hexString[(in[i] & 0xf0) >> 4];
			out[2 * i + 1] = hexString[(in[i] & 0x0f)];
		}
		return new String(out);
	}

	// 字符串压缩为16进制数据(TwoOne)
	public static byte[] Str2Hex(String in) {
		final byte[] hexString = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		byte[] out = new byte[in.length() / 2];
		char ch;
		int tmp;
		// 将每2位16进制整数组装成一个字节
		for (int i = 0; i < in.length(); i += 2) {
			ch = in.charAt(i);
			if (ch > '9') {
				if (ch > 'a' && ch < 'f')
					tmp = ch - 'a' + 0x0A;
				else
					tmp = ch - 'A' + 0x0A;
			} else
				tmp = ch - '0';
			out[i / 2] = (byte) (tmp << 4);

			ch = in.charAt(i + 1);
			if (ch > '9') {
				if (ch >= 'a' && ch <= 'f')
					tmp = ch - 'a' + 0x0A;
				else
					tmp = ch - 'A' + 0x0A;
			} else
				tmp = ch - '0';
			out[i / 2] += tmp;
		}
		return out;
	}

	/**
	 * Description 根据键值进行加密
	 *
	 * @param data
	 * @param key 加密键byte数组
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(byte[] data, byte[] key) throws Exception {
		// 生成一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密钥数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey secretKey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance(DES);
		// 用密钥初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);
		return new String(cipher.doFinal(data), "UTF-8");
	}

	/**
	 * Description 根据键值进行解密
	 *
	 * @param data
	 * @param key 加密键byte数组
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(byte[] data, byte[] key) throws Exception {
		// 生成一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密钥数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey secretKey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance(DES);
		// 用密钥初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);
		return new String(cipher.doFinal(data), "UTF-8");
	}

	public static void main(String paramArrayOfString[]) throws UnsupportedEncodingException {

		System.out.println(DesUtil.encryptDES("15362198620-1-123456", "123456"));
		// System.out.println(DesUtil.decryptDES("Mi8kDGzkMYD+tlha89yUhvT7rSGtbKOf/co8ECeTVcZQKissgJdVXE/mvcizfwEX", "123456"));

	}
}
