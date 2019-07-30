package cn.sini.cgb.admin.identity.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.sini.cgb.common.http.HttpRequestWrapper;

/**
 * 首页Action
 * 
 * @author 杨海彬
 */
@Controller
public class HomeAction {

	/** 前往管理端登录页 */
	@RequestMapping("/login")
	public String login(HttpRequestWrapper request) {
		return request.getLoginUser() == null ? "identity/home/login" : "redirect:/index.action";
	}

	/** 前往管理端首页 */
	@RequestMapping("/index")
	public String index(HttpRequestWrapper request) {
		return "identity/home/index";
	}
}