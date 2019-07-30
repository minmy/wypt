package cn.sini.cgb.admin.annex.action;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import cn.sini.cgb.admin.annex.entity.Annex;
import cn.sini.cgb.admin.annex.entity.Annex.AnnexType;
import cn.sini.cgb.admin.annex.query.AnnexQuery;
import cn.sini.cgb.admin.annex.util.AnnexUtils;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.json.JsonUtils;
import cn.sini.cgb.common.util.CommonUtils;
import cn.sini.cgb.common.util.Environment;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 附件Action
 * 
 * @author 杨海彬
 */
@Controller
@RequestMapping("/annex")
public class AnnexAction {

	/** 上传文件 */
	@Transactional
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public void upload(HttpRequestWrapper request, HttpResponseWrapper response, @RequestParam("file") MultipartFile multipartFile) throws Exception {
		ObjectNode objectNode = JsonUtils.createObjectNode();
		String fileName = multipartFile.getOriginalFilename();
		AnnexType annexType = request.getEnum("annexType", AnnexType.class);
		Long sort = request.getLong("sort");
		if (annexType == null) {
			annexType = AnnexType.OTHER_PIC;
		}
		Annex annex = AnnexUtils.saveAnnex(annexType, fileName, sort, multipartFile.getBytes());
		objectNode.put("annexId", annex.getId());
		response.outputJson(0, objectNode);
	}

	/** 下载文件 */
	@RequestMapping("/download")
	public void download(HttpRequestWrapper request, HttpResponseWrapper response) {
		String id = request.getStringMust("id");
		Annex annex = new AnnexQuery().id(id).readOnly().uniqueResult();
		if (annex == null) {
			response.outputString("文件不存在");
			return;
		}
		File file = new File(Environment.getProperty("annex.path") + annex.getFilePath());
		if (!file.exists()) {
			response.outputString("文件不存在");
			return;
		}
		if (annex.getAnnexType() == AnnexType.GROUP_ORDER_PSD_EXCEL) {
			long thisTime = System.currentTimeMillis();
			long createTime = annex.getCreateTime().getTime();
			String day = Environment.getProperty("psd.effective.time");
			Integer dayMillisecond = Integer.valueOf(day) * 86400000;// 86400000 一天的毫秒数
			if (createTime + dayMillisecond < thisTime) {
				response.outputString("配送单已失效");
				return;
			}
		}
		String fileName = annex.getFileName();
		response.setHeader("Content-Type", "application/force-download");
		response.setHeader("Content-Disposition", "attachment;filename=" + CommonUtils.encode(fileName));
		response.setHeader("Content-Length", file.length() + "");
		response.setHeader("Content-Transfer-Encoding", "binary");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");
		try {
			FileUtils.copyFile(file, response.getOutputStream());
		} catch (Exception e) {
		}
	}

	/** 查看文件 */
	@RequestMapping("/view")
	public void view(HttpRequestWrapper request, HttpResponseWrapper response) {
		String id = request.getStringMust("id");
		Annex annex = new AnnexQuery().id(id).readOnly().uniqueResult();
		if (annex == null) {
			response.outputString("文件不存在");
			return;
		}
		File file = new File(Environment.getProperty("annex.path") + annex.getFilePath());
		if (!file.exists()) {
			response.outputString("文件不存在");
			return;
		}
		String fileName = annex.getFileName();
		String mimeType = Environment.getServletContext().getMimeType(fileName);
		boolean download = false;
		if (mimeType == null || !mimeType.startsWith("image/")) {
			download = true;
			response.setHeader("Content-Type", "application/force-download");
		}
		response.setHeader("Content-Type", mimeType);
		response.setHeader("Content-Disposition", (download ? "attachment;" : "") + "filename=" + CommonUtils.encode(fileName));
		response.setHeader("Content-Length", file.length() + "");
		response.setHeader("Content-Transfer-Encoding", "binary");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");
		try {
			FileUtils.copyFile(file, response.getOutputStream());
		} catch (Exception e) {
		}
	}

	/** 删除文件 */
	@Transactional
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public void delete(HttpRequestWrapper request, HttpResponseWrapper response) {
		Annex annex = new AnnexQuery().id(request.getStringMust("id")).uniqueResult();
		if (annex != null) {
			annex.remove();
			response.outputJson(0, "删除成功");
		} else {
			response.outputJson(-1, "删除失败，文件不存在");
		}
	}
}