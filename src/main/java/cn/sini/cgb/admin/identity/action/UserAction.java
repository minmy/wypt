package cn.sini.cgb.admin.identity.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.sini.cgb.admin.identity.entity.Role;
import cn.sini.cgb.admin.identity.entity.User;
import cn.sini.cgb.admin.identity.entity.User.UserType;
import cn.sini.cgb.admin.identity.query.RoleQuery;
import cn.sini.cgb.admin.identity.query.UserQuery;
import cn.sini.cgb.api.cgb.entity.group.Community;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.api.cgb.query.group.CommunityQuery;
import cn.sini.cgb.api.cgb.query.group.WeChatUserQuery;
import cn.sini.cgb.api.identity.entity.ApiRole;
import cn.sini.cgb.api.identity.query.ApiRoleQuery;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.json.JsonUtils;
import cn.sini.cgb.common.query.Page;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 用户Action
 * 
 * @author 杨海彬
 */
@Controller
@RequestMapping("/user")
public class UserAction {

	/** 前往用户列表页 */
	@RequestMapping("/list")
	public String list(HttpRequestWrapper request) {
		Page<User> page = new UserQuery().userType(UserType.ADMIN).usernameLike(request.getTrim("username")).fullnameLike(request.getTrim("fullname")).orderBy("id", false).readOnly()
				.pageHasCount(request.getPageNum(), request.getPageSize());
		request.setAttribute("page", page);
		return "identity/user/list";
	}

	/** 前往用户编辑页 */
	@RequestMapping("/edit")
	public String edit(HttpRequestWrapper request) {
		Long id = request.getLong("id");
		if (id != null) {
			User user = new UserQuery().userType(UserType.ADMIN).id(id).readOnly().uniqueResult();
			request.setAttribute("user", user);
		}
		request.setAttribute("roleList", new RoleQuery().orderBy("id", true).readOnly().list());
		request.setAttribute("apiRoleList", new ApiRoleQuery().orderBy("id", true).readOnly().list());
		return "identity/user/edit";
	}

	/** 保存用户 */
	@Transactional
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void save(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		Long id = request.getLong("id");
		String password = null;
		User user = null;
		if (id != null) {
			password = request.getString("password", 6, 20);
			user = new UserQuery().userType(UserType.ADMIN).id(id).uniqueResult();
		} else {
			String username = request.getTrimMust("username", "^\\w{3,20}$");
			password = request.getStringMust("password", 6, 20);
			if (new UserQuery().username(username).readOnly().count() > 0) {
				response.outputJson(false, "保存失败，帐号已被使用");
				return;
			}
			user = new User();
			user.setUserType(UserType.ADMIN);
			user.setUsername(username);
		}
		if (StringUtils.isNotEmpty(password)) {
			user.setPassword(password);
			user.encryptPassword();
		}
		user.setFullname(request.getTrimMust("fullname", 20));
		user.setDisable(BooleanUtils.isTrue(request.getBoolean("disable")));
		// 保存授予的角色
		user.getRoles().clear();
		Serializable[] roleIds = request.getLongs("roleIds");
		if (roleIds != null) {
			List<Role> roleList = new RoleQuery().id(roleIds).readOnly().list();
			user.getRoles().addAll(roleList);
		}
		user.saveOrUpdate();
		response.outputJson(true);
	}

	/** 删除后台用户 */
	@Transactional
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public void delete(HttpRequestWrapper request, HttpResponseWrapper response) {
		Serializable[] ids = request.getLongsMust("ids");
		List<User> list = new UserQuery().userType(UserType.ADMIN).id(ids).list();
		for (User entity : list) {
			entity.remove();
		}
		response.outputJson(true);
	}

	/** 解锁用户 */
	@Transactional
	@RequestMapping(value = "/unLock", method = RequestMethod.POST)
	public void unLock(HttpRequestWrapper request, HttpResponseWrapper response) {
		User user = new UserQuery().userType(UserType.ADMIN).id(request.getLongMust("id")).uniqueResult();
		user.setLoginErrorCount(0);
		user.saveOrUpdate();
		response.outputJson(true);
	}

	/** 前往微信用户管理列表页 */
	@RequestMapping("/wechatUserList")
	public String wechatUser(HttpRequestWrapper request) {
		Long communityId = request.getLong("communityId");
		Community community = null;
		if (communityId != null) {
			community = new CommunityQuery().id(communityId).readOnly().uniqueResult();
		}
		Page<WeChatUser> page = new WeChatUserQuery().openId(request.getString("openId")).community(community).brigadier(request.getBoolean("brigadier")).contactsLike(request.getString("contacts")).nameLike(request.getString("name"))
				.orderBy("createTime", true).readOnly().pageHasCount(request.getPageNum(), request.getPageSize());
		List<Community> communities = new CommunityQuery().readOnly().list();
		request.setAttribute("page", page);
		request.setAttribute("communities", communities);
		return "identity/user/wechatUserList";
	}

	/** 前往微信用户编辑页 */
	@RequestMapping("/wechatUserEdit")
	public String wechatUserEdit(HttpRequestWrapper request) {
		Long id = request.getLong("id");
		if (id != null) {
			WeChatUser weChatUser = new WeChatUserQuery().id(request.getLongMust("id")).uniqueResult();
			request.setAttribute("weChatUser", weChatUser);
		}
		request.setAttribute("apiRoleList", new ApiRoleQuery().orderBy("id", true).readOnly().list());
		List<Community> communityList = new CommunityQuery().orderBy("id", true).readOnly().list();
		ArrayNode communitys = JsonUtils.createArrayNode();
		for (Community community : communityList) {
			ObjectNode objectNode = communitys.addObject();
			objectNode.put("id", community.getId());
			objectNode.put("name", community.getName());
		}
		request.setAttribute("communitys", communitys.toString());
		return "identity/user/wechatUserEdit";
	}

	/** 保存微信角色 */
	@Transactional
	@RequestMapping(value = "/wechatUserSave", method = RequestMethod.POST)
	public void wechatUserSave(HttpRequestWrapper request, HttpResponseWrapper response) {
		WeChatUser weChatUser = new WeChatUserQuery().id(request.getLongMust("id")).uniqueResult();
		for (Community community : weChatUser.getCommunities()) {
			community.setWeChatUser(null);
			community.saveOrUpdate();
		}
		Serializable[] apiRoleIds = request.getLongs("apiRoleIds");
		List<ApiRole> apiRoleList = new ArrayList<ApiRole>();
		if (apiRoleIds != null) {
			apiRoleList = new ApiRoleQuery().id(apiRoleIds).readOnly().list();
			ApiRole apiRole = new ApiRoleQuery().name("里长角色").readOnly().uniqueResult();
			if (apiRoleList.contains(apiRole)) {
				Serializable[] communityIds = request.getLongs("communityIds");
				List<Community> communityList = null;
				if (ArrayUtils.isNotEmpty(communityIds)) {
					communityList = new CommunityQuery().id(communityIds).list();
					for (Community community : communityList) {
						if (community.getWeChatUser() != null) {
							response.outputJson(false, "小区【" + community.getName() + "】已存在里长");
							return;
						}
						weChatUser.setBrigadier(true);
						community.setWeChatUser(weChatUser);
						community.saveOrUpdate();
					}
				}
			} else {
				weChatUser.setBrigadier(false);
			}
			apiRole = new ApiRoleQuery().name("企业角色").readOnly().uniqueResult();
			if (apiRoleList.contains(apiRole)) {
				weChatUser.setIsBusiness(true);
			} else {
				weChatUser.setIsBusiness(false);
			}
		}
		weChatUser.getApiRoles().clear();
		weChatUser.getApiRoles().addAll(apiRoleList);
		weChatUser.saveOrUpdate();
		response.outputJson(true);
	}
}