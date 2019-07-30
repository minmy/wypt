package cn.sini.cgb.common.el;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.jsp.JspFactory;

/**
 * EL特殊字符转义监听器
 * 
 * @author 杨海彬
 */
public class EscapeXmlELResolverListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		JspFactory.getDefaultFactory().getJspApplicationContext(sce.getServletContext()).addELResolver(new EscapeXmlELResolver());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
}