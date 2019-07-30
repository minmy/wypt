package cn.sini.cgb.common.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

/**
 * 中文转拼音
 * 
 * @author gaowei
 */
public class PinYinUtils {

	/**
	 * @description 中文转拼音首字母
	 * @return String
	 */
	public static String chineseToPinyin(char... para) {
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);// 小写字母
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 无音调
		StringBuilder sb = new StringBuilder();
		String str = null;
		for (char c : para) {
			try {
				sb.append(PinyinHelper.toHanyuPinyinStringArray(c, defaultFormat)[0].toCharArray()[0]);
			} catch (Exception e) {
				sb.append(c);
			}
			str = sb.toString();
		}
		return str;
	}
}
