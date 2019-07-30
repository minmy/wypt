package cn.sini.cgb.common.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.sini.cgb.admin.identity.entity.User;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;

/**
 * 后置Http请求过滤器，该过滤器需在Hibernate和Spring过滤器之后
 * 
 * @author 杨海彬
 *
 */
public class HttpRequestAfterFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpRequestWrapper request = new HttpRequestWrapper((HttpServletRequest) servletRequest);
		HttpResponseWrapper response = new HttpResponseWrapper((HttpServletResponse) servletResponse);
		User loginUser = request.getLoginUser();
		if (loginUser != null) {
			request.setAttribute("loginUser", loginUser);
		}
		filterChain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}
}