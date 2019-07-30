package cn.sini.cgb.admin.api.action;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.sini.cgb.api.identity.entity.ApiMenu;
import cn.sini.cgb.api.identity.entity.ApiResource;
import cn.sini.cgb.api.identity.entity.ApiRole;
import cn.sini.cgb.api.identity.query.ApiMenuQuery;
import cn.sini.cgb.api.identity.query.ApiResourceQuery;
import cn.sini.cgb.api.identity.query.ApiRoleQuery;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.json.JsonUtils;
import cn.sini.cgb.common.json.JsonWrapper;
import cn.sini.cgb.common.query.Page;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 接口角色Action
 * 
 * @author 杨海彬
 */
@Controller
@RequestMapping("/apiRole")
public class ApiRoleAction {

	/** 前往接口角色管理列表页 */
	@RequestMapping("/list")
	public String list(HttpRequestWrapper request) {
		Page<ApiRole> page = new ApiRoleQuery().nameLike(request.getTrim("name")).orderBy("createTime", true).readOnly().pageHasCount(request.getPageNum(), request.getPageSize());
		request.setAttribute("page", page);
		return "api/apiRole/list";
	}

	/** 前往接口角色管理编辑页 */
	@RequestMapping("/edit")
	public String edit(HttpRequestWrapper request) {
		Long id = request.getLong("id");
		if (id != null) {
			ApiRole apiRole = new ApiRoleQuery().id(id).readOnly().uniqueResult();
			List<ApiMenu> menus = new ApiMenuQuery().apiRoleId(id).readOnly().list();
			request.setAttribute("apiRole", apiRole);
			request.setAttribute("menus", menus);
		}
		List<ApiResource> apiResourceList = new ApiResourceQuery().orderBy("id", true).readOnly().list();
		ArrayNode apiResources = JsonUtils.createArrayNode();
		for (ApiResource apiResource : apiResourceList) {
			ObjectNode objectNode = apiResources.addObject();
			objectNode.put("id", apiResource.getId());
			objectNode.put("name", apiResource.getName() + "(" + apiResource.getUri() + ")");
		}
		request.setAttribute("apiResources", apiResources.toString());
		return "api/apiRole/edit";
	}

	/** 保存接口角色 */
	@Transactional
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void save(HttpRequestWrapper request, HttpResponseWrapper response) {
		Serializable[] apiResourceIds = request.getLongs("apiResourceIds");
		List<ApiResource> apiResourceList = null;
		if (ArrayUtils.isNotEmpty(apiResourceIds)) {
			apiResourceList = new ApiResourceQuery().id(apiResourceIds).readOnly().list();
		}
		Long id = request.getLong("id");
		ApiRole apiRole = null;
		if (id != null) {
			apiRole = new ApiRoleQuery().id(id).uniqueResult();
		} else {
			apiRole = new ApiRole();
		}
		String name = request.getTrimMust("name");
		ApiRole tempApiRole = new ApiRoleQuery().name(name).readOnly().uniqueResult();
		if (tempApiRole != null && !tempApiRole.equals(apiRole)) {
			response.outputJson(false, "保存失败，名称已被使用");
			return;
		}
		apiRole.setName(name);
		apiRole.getApiResources().clear();
		if (apiResourceList != null) {
			apiRole.getApiResources().addAll(apiResourceList);
		}
		apiRole.saveOrUpdate();
		List<ApiMenu> list = new ApiMenuQuery().apiRoleId(apiRole.getId()).list();
		for (ApiMenu apiMenu : list) {
			apiMenu.remove();
		}
		ArrayNode menus = JsonUtils.toArrayNode(request.getParameter("menus"));
		for (JsonNode jsonNode : menus) {
			ApiMenu apiMenu = new ApiMenu();
			JsonWrapper jsonWrapper = new JsonWrapper(jsonNode);
			apiMenu.setApiRoleId(apiRole.getId());
			apiMenu.setName(jsonWrapper.getStringMust("name"));
			apiMenu.setUrl(jsonWrapper.getString("url"));
			apiMenu.saveOrUpdate();
		}
		response.outputJson(true);
	}

	/** 删除接口角色 */
	@Transactional
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public void delete(HttpRequestWrapper request, HttpResponseWrapper response) {
		Serializable[] ids = request.getLongsMust("ids");
		List<ApiRole> list = new ApiRoleQuery().id(ids).list();
		for (ApiRole apiRole : list) {
			apiRole.remove();
		}
		response.outputJson(true);
	}
}