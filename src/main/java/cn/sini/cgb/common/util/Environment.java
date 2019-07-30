package cn.sini.cgb.common.util;

import java.util.Properties;

import javax.servlet.ServletContext;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * 系统环境类，该类在Spring启动完毕后方可使用
 * 
 * @author 杨海彬
 */
@Component
public class Environment implements ApplicationListener<ContextRefreshedEvent> {

	private static boolean startFinish = false;
	private static boolean devMode = true;
	private static ApplicationContext applicationContext;
	private static SessionFactory sessionFactory;
	private static ServletContext servletContext;
	private static Properties properties;

	/** 系统是否启动完成 */
	public static boolean isStartFinish() {
		return startFinish;
	}

	/** 是否为开发模式 */
	public static boolean isDevMode() {
		return devMode;
	}

	/** 获取Spring的ApplicationContext对象 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/** 获取Spring的Bean对象 */
	public static <T> T getBean(Class<T> requiredType) {
		return applicationContext.getBean(requiredType);
	}

	/** 获取Spring的Bean对象 */
	public static <T> T getBean(String name, Class<T> requiredType) {
		return applicationContext.getBean(name, requiredType);
	}

	/** 获取Hibernate的SessionFactory对象 */
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/** 获取Hibernate当前线程的Session对象 */
	public static Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	/** 获取ServletContext对象 */
	public static ServletContext getServletContext() {
		return servletContext;
	}

	/** 获取Properties的属性值 */
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	/** Spring启动完成事件 */
	public void onApplicationEvent(ContextRefreshedEvent event) {
		applicationContext = event.getApplicationContext();
		if (applicationContext.getParent() == null) {
			sessionFactory = getBean("sessionFactory", SessionFactory.class);
			properties = getBean("systemProperties", Properties.class);
			servletContext = ((WebApplicationContext) applicationContext).getServletContext();
			servletContext.setAttribute("host", getProperty("host"));
			servletContext.setAttribute("path", getProperty("path"));
			servletContext.setAttribute("skin", getProperty("skin"));
			devMode = "true".equals(getProperty("dev.mode"));
			startFinish = true;
		}
	}
}