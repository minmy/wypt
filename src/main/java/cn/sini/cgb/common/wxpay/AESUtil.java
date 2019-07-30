package cn.sini.cgb.common.wxpay;

import java.security.AlgorithmParameters;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Base64;

import cn.sini.cgb.common.json.JsonUtils;
import cn.sini.cgb.common.util.Environment;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class AESUtil {

	private static Logger LOGGER = Logger.getLogger(AESUtil.class);
	/**
	 * 密钥算法
	 */
	private static final String ALGORITHM = "AES";
	/**
	 * 加解密算法/工作模式/填充 方式
	 */
	private static final String ALGORITHM_MODE_PADDING = "AES/ECB/PKCS5Padding";
	private static final String ALGORITHM_PADDING = "AES/CBC/PKCS5Padding";
	/**
	 * 生成key
	 */
	private static SecretKeySpec key = new SecretKeySpec(MD5Util.MD5Encode(Environment.getProperty("apiKey"), "UTF-8").toLowerCase().getBytes(), ALGORITHM);

	/**
	 * AES加密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String encryptData(String data) throws Exception {
		// 创建密码器
		Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);
		// 初始化
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return Base64Util.encode(cipher.doFinal(data.getBytes()));
	}

	/**
	 * AES解密
	 * 
	 * @param base64Data
	 * @return
	 * @throws Exception
	 */
	public static String decryptData(String base64Data) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);
		cipher.init(Cipher.DECRYPT_MODE, key);
		return new String(cipher.doFinal(Base64Util.decode(base64Data)));
	}

	/**
	 * base64解密
	 * 
	 * @param encryptedData
	 * @param sessionkey
	 * @param iv
	 * @return
	 * */
	public static ObjectNode decryptData(String encryptedData, String sessionkey, String iv) {
		// 被加密的数据
		byte[] dataByte = Base64.decode(encryptedData);
		// 加密秘钥
		byte[] keyByte = Base64.decode(sessionkey);
		// 偏移量
		byte[] ivByte = Base64.decode(iv);
		int base = 16;
		try {
			if (keyByte.length % base != 0) {
				int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
				byte[] temp = new byte[groups * base];
				Arrays.fill(temp, (byte) 0);
				System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
				keyByte = temp;
			}
			// 初始化
			Security.addProvider(new BouncyCastleProvider());
			Cipher cipher = Cipher.getInstance(ALGORITHM_PADDING, "BC");
			SecretKeySpec spec = new SecretKeySpec(keyByte, ALGORITHM);
			AlgorithmParameters parameters = AlgorithmParameters.getInstance(ALGORITHM);
			parameters.init(new IvParameterSpec(ivByte));
			cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
			byte[] resultByte = cipher.doFinal(dataByte);
			if (null != resultByte && resultByte.length > 0) {
				String result = new String(resultByte, "UTF-8");
				return JsonUtils.toObjectNode(result);
			}
		} catch (Exception e) {
			LOGGER.error("微信用户信息解密异常", e);
		}
		return null;
	}
}