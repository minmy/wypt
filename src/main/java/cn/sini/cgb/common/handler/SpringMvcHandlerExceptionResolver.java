package cn.sini.cgb.common.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import cn.sini.cgb.common.http.HttpRequestWrapper;

/**
 * Spring MVC自定义处理异常解析器
 * 
 * @author 杨海彬
 */
public class SpringMvcHandlerExceptionResolver implements HandlerExceptionResolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringMvcHandlerExceptionResolver.class);

	@Override
	public ModelAndView resolveException(HttpServletRequest req, HttpServletResponse response, Object handler, Exception ex) {
		HttpRequestWrapper request = new HttpRequestWrapper(req);
		LOGGER.error(request.toString(), ex);
		return null;
	}
}