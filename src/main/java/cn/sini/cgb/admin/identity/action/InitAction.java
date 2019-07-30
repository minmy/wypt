package cn.sini.cgb.admin.identity.action;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.sini.cgb.admin.identity.entity.Authority;
import cn.sini.cgb.admin.identity.entity.Role;
import cn.sini.cgb.admin.identity.entity.User;
import cn.sini.cgb.admin.identity.entity.User.UserType;
import cn.sini.cgb.admin.identity.query.AuthorityQuery;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.util.Environment;

/**
 * 系统初始化Action
 * 
 * @author 杨海彬
 */
@Controller
@RequestMapping("/init")
public class InitAction {

	/** 初始化入口 */
	@Transactional
	@RequestMapping("/index")
	public void index(HttpRequestWrapper request, HttpResponseWrapper response) {

		if (new AuthorityQuery().count() == 0) {
			createAuthority();
			createRoleAndUser();
		}
		response.outputJson(true);
	}

	/** 创建权限 */
	private void createAuthority() {
		Authority authority = new Authority();
		authority.setAuthority("ROLE_VISIT_MANAGE");
		authority.setName("访问管理端");
		authority.setSortWeight(10L);
		authority.saveOrUpdate();

		authority = new Authority();
		authority.setAuthority("ROLE_SYSTEM_MANAGE");
		authority.setName("系统管理");
		authority.setSortWeight(20L);
		authority.saveOrUpdate();

		Authority subAuthority = new Authority();
		subAuthority.setAuthority("ROLE_USER_MANAGE");
		subAuthority.setName("用户管理");
		subAuthority.setSortWeight(10L);
		subAuthority.setSuperAuthority(authority);
		subAuthority.saveOrUpdate();

		subAuthority = new Authority();
		subAuthority.setAuthority("ROLE_ROLE_MANAGE");
		subAuthority.setName("角色管理");
		subAuthority.setSortWeight(20L);
		subAuthority.setSuperAuthority(authority);
		subAuthority.saveOrUpdate();

		authority = new Authority();
		authority.setAuthority("ROLE_API_MANAGE");
		authority.setName("接口管理");
		authority.setSortWeight(30L);
		authority.saveOrUpdate();

		subAuthority = new Authority();
		subAuthority.setAuthority("ROLE_API_RESOURE_MANAGE");
		subAuthority.setName("资源管理");
		subAuthority.setSortWeight(10L);
		subAuthority.setSuperAuthority(authority);
		subAuthority.saveOrUpdate();

		subAuthority = new Authority();
		subAuthority.setAuthority("ROLE_API_ROLE_MANAGE");
		subAuthority.setName("角色管理");
		subAuthority.setSortWeight(20L);
		subAuthority.setSuperAuthority(authority);
		subAuthority.saveOrUpdate();

		authority = new Authority();
		authority.setAuthority("ROLE_APPLY_MANAGE");
		authority.setName("提现申请管理");
		authority.setSortWeight(40L);
		authority.saveOrUpdate();

		subAuthority = new Authority();
		subAuthority.setAuthority("ROLE_APPLY_LIST_MANAGE");
		subAuthority.setName("提现申请列表");
		subAuthority.setSortWeight(10L);
		subAuthority.setSuperAuthority(authority);
		subAuthority.saveOrUpdate();

		authority = new Authority();
		authority.setAuthority("ROLE_GROUP_ORDER_MANAGE");
		authority.setName("团单管理");
		authority.setSortWeight(50L);
		authority.saveOrUpdate();

		subAuthority = new Authority();
		subAuthority.setAuthority("ROLE_GROUP_ORDER_LIST_MANAGE");
		subAuthority.setName("团单列表");
		subAuthority.setSortWeight(10L);
		subAuthority.setSuperAuthority(authority);
		subAuthority.saveOrUpdate();

		Environment.getSession().flush();
	}

	/** 创建角色和用户 */
	private void createRoleAndUser() {
		List<Authority> authorityList = new AuthorityQuery().authority("ROLE_VISIT_MANAGE", "ROLE_SYSTEM_MANAGE", "ROLE_USER_MANAGE", "ROLE_ROLE_MANAGE", "ROLE_API_MANAGE", "ROLE_API_RESOURE_MANAGE", "ROLE_API_ROLE_MANAGE").readOnly()
				.list();
		Role role = new Role();
		role.setName("系统管理员");
		role.getAuthoritys().addAll(authorityList);
		role.saveOrUpdate();
		Environment.getSession().flush();

		User user = new User();
		user.setUserType(UserType.ADMIN);
		user.setUsername("admin");
		user.setPassword("111111");
		user.encryptPassword();
		user.setFullname("系统管理员");
		user.getRoles().add(role);
		user.saveOrUpdate();
	}
}