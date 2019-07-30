package cn.sini.cgb.admin.annex.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import cn.sini.cgb.admin.annex.entity.Annex;
import cn.sini.cgb.admin.annex.entity.Annex.AnnexType;
import cn.sini.cgb.common.util.Environment;

/**
 * 附件工具类
 * 
 * @author 杨海彬
 */
public class AnnexUtils {

	/**
	 * 保存附件
	 * 
	 * @param sort
	 */
	public static Annex saveAnnex(AnnexType annexType, String fileName, Long sort, byte[] fileData) throws Exception {
		String extension = org.springframework.util.StringUtils.getFilenameExtension(fileName);
		Assert.isTrue(ArrayUtils.contains(annexType.getAllowSuffix().split(","), StringUtils.lowerCase(extension)), "保存附件失败，不支持的文件类型");
		extension = StringUtils.isEmpty(extension) ? "" : ("." + extension);
		String filePath = Environment.getProperty("annex") + "/" + annexType.getName() + new SimpleDateFormat("/yyyyMMdd/").format(new Date()) + UUID.randomUUID().toString() + extension;
		File file = new File(Environment.getProperty("annex.path") + filePath);
		FileUtils.forceMkdir(file.getParentFile());
		FileUtils.writeByteArrayToFile(file, fileData);
		Annex annex = new Annex();
		annex.setAnnexType(annexType);
		annex.setFileName(fileName);
		annex.setFilePath(filePath);
		annex.setSort(sort);
		annex.saveOrUpdate();
		return annex;
	}
}