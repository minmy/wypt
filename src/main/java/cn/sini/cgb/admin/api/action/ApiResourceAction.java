package cn.sini.cgb.admin.api.action;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.sini.cgb.api.identity.entity.ApiResource;
import cn.sini.cgb.api.identity.entity.ApiResource.LimitPeriod;
import cn.sini.cgb.api.identity.query.ApiResourceQuery;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.query.Page;

/**
 * 接口资源Action
 * 
 * @author 杨海彬
 */
@Controller
@RequestMapping("/apiResource")
public class ApiResourceAction {

	/** 前往接口资源管理列表页 */
	@RequestMapping("/list")
	public String list(HttpRequestWrapper request) {
		Page<ApiResource> page = new ApiResourceQuery().uriLike(request.getTrim("uri")).nameLike(request.getTrim("name")).orderBy("uri", true).readOnly().pageHasCount(request.getPageNum(), request.getPageSize());
		request.setAttribute("page", page);
		return "api/apiResource/list";
	}

	/** 前往接口资源管理编辑页 */
	@RequestMapping("/edit")
	public String edit(HttpRequestWrapper request) {
		Long id = request.getLong("id");
		if (id != null) {
			ApiResource apiResource = new ApiResourceQuery().id(id).readOnly().uniqueResult();
			request.setAttribute("apiResource", apiResource);
		}
		request.setAttribute("limitPeriods", LimitPeriod.values());
		return "api/apiResource/edit";
	}

	/** 保存接口资源 */
	@Transactional
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void save(HttpRequestWrapper request, HttpResponseWrapper response) {
		Long id = request.getLong("id");
		ApiResource apiResource = null;
		if (id != null) {
			apiResource = new ApiResourceQuery().id(id).uniqueResult();
		} else {
			apiResource = new ApiResource();
		}
		String name = request.getTrimMust("name");
		ApiResource tempApiResource = new ApiResourceQuery().name(name).readOnly().uniqueResult();
		if (tempApiResource != null && !tempApiResource.equals(apiResource)) {
			response.outputJson(false, "保存失败，名称已被使用");
			return;
		}
		String uri = request.getTrimMust("uri");
		tempApiResource = new ApiResourceQuery().uri(uri).readOnly().uniqueResult();
		if (tempApiResource != null && !tempApiResource.equals(apiResource)) {
			response.outputJson(false, "保存失败，URI已被使用");
			return;
		}
		apiResource.setName(name);
		apiResource.setUri(uri);
		apiResource.setEnableRateLimit(BooleanUtils.isTrue(request.getBoolean("enableRateLimit")));
		apiResource.setLimitPeriod(request.getEnumMust("limitPeriod", LimitPeriod.class));
		apiResource.setLimitCount(ObjectUtils.defaultIfNull(request.getInteger("limitCount"), 0));
		apiResource.saveOrUpdate();
		response.outputJson(true);
	}

	/** 删除接口资源 */
	@Transactional
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public void delete(HttpRequestWrapper request, HttpResponseWrapper response) {
		Serializable[] ids = request.getLongsMust("ids");
		List<ApiResource> list = new ApiResourceQuery().id(ids).list();
		for (ApiResource apiResource : list) {
			apiResource.remove();
		}
		response.outputJson(true);
	}
}