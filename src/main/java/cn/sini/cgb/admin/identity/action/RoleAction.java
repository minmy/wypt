package cn.sini.cgb.admin.identity.action;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.sini.cgb.admin.identity.entity.Authority;
import cn.sini.cgb.admin.identity.entity.Role;
import cn.sini.cgb.admin.identity.query.AuthorityQuery;
import cn.sini.cgb.admin.identity.query.RoleQuery;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.json.JsonUtils;
import cn.sini.cgb.common.query.Page;

/**
 * 角色Action
 * 
 * @author 杨海彬
 */
@Controller
@RequestMapping("/role")
public class RoleAction {

	/** 前往角色管理列表页 */
	@RequestMapping("/list")
	public String list(HttpRequestWrapper request) {
		Page<Role> page = new RoleQuery().nameLike(request.getTrim("name")).orderBy("id", true).readOnly().pageHasCount(request.getPageNum(), request.getPageSize());
		request.setAttribute("page", page);
		return "identity/role/list";
	}

	/** 前往角色管理编辑页 */
	@RequestMapping("/edit")
	public String edit(HttpRequestWrapper request) {
		Long id = request.getLong("id");
		if (id != null) {
			Role role = new RoleQuery().id(id).readOnly().uniqueResult();
			request.setAttribute("role", role);
		}
		List<Authority> authorityList = new AuthorityQuery().orderBy("sortWeight", true).orderBy("id", true).readOnly().list();
		ArrayNode authoritys = JsonUtils.createArrayNode();
		for (Authority authority : authorityList) {
			Authority superAuthority = authority.getSuperAuthority();
			ObjectNode objectNode = authoritys.addObject();
			objectNode.put("id", authority.getId());
			objectNode.put("pId", superAuthority == null ? 0 : superAuthority.getId());
			objectNode.put("name", authority.getName());
		}
		request.setAttribute("authoritys", authoritys.toString());
		return "identity/role/edit";
	}

	/** 保存角色 */
	@Transactional
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void save(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		Long id = request.getLong("id");
		String name = request.getTrimMust("name");
		List<Authority> authorityList = new AuthorityQuery().readOnly().list();
		Role role = null;
		if (id != null) {
			role = new RoleQuery().id(id).uniqueResult();
		} else {
			role = new Role();
		}
		Role tempRole = new RoleQuery().name(name).readOnly().uniqueResult();
		if (tempRole != null && !tempRole.equals(role)) {
			response.outputJson(false, "保存失败，名称已被使用");
			return;
		}
		role.getAuthoritys().removeAll(authorityList);
		Long[] authorityIds = request.getLongs("authorityIds");
		if (authorityIds != null) {
			for (Authority authority : authorityList) {
				if (ArrayUtils.contains(authorityIds, authority.getId())) {
					role.getAuthoritys().add(authority);
				}
			}
		}
		role.setName(name);
		role.setRemark(request.getTrim("remark"));
		role.saveOrUpdate();
		response.outputJson(true);
	}

	/** 删除角色 */
	@Transactional
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public void delete(HttpRequestWrapper request, HttpResponseWrapper response) {
		Serializable[] ids = request.getLongsMust("ids");
		List<Role> list = new RoleQuery().id(ids).list();
		for (Role entity : list) {
			entity.remove();
		}
		response.outputJson(true);
	}
}