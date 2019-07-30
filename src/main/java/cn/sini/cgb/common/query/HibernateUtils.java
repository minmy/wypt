package cn.sini.cgb.common.query;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.springframework.util.ReflectionUtils;

import cn.sini.cgb.common.util.Environment;

/**
 * Hibernate工具类
 * 
 * @author 杨海彬
 */
public class HibernateUtils {

	/** 按照指定类型,重新从数据库加载该实体 */
	public static <T> T regetEntity(Class<T> type, Object entity) {
		Session session = null;
		Serializable id = null;
		if (entity instanceof HibernateProxy) {
			LazyInitializer lazyInitializer = ((HibernateProxy) entity).getHibernateLazyInitializer();
			session = (Session) lazyInitializer.getSession();
			id = lazyInitializer.getIdentifier();
		} else {
			Field field = ReflectionUtils.findField(type, getIdFieldName(type));
			field.setAccessible(true);
			id = (Serializable) ReflectionUtils.getField(field, entity);
		}
		if (session == null) {
			session = Environment.getSession();
			session.evict(entity);
		}
		return session.get(type, id);
	}

	/** 获取实体的主键字段名 */
	public static String getIdFieldName(Class<?> clazz) {
		return Environment.getSessionFactory().getClassMetadata(clazz).getIdentifierPropertyName();
	}
}