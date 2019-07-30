package cn.sini.cgb.api.identity.query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.identity.entity.ApiResource;
import cn.sini.cgb.common.entity.AbstractEntity;
import cn.sini.cgb.common.query.AbstractQuery;

/**
 * 接口资源查询类
 * 
 * @author 杨海彬
 */
public class ApiResourceQuery extends AbstractQuery {

	@Override
	protected Class<? extends AbstractEntity> queryEntity() {
		return ApiResource.class;
	}

	/** 添加资源地址查询条件 */
	public ApiResourceQuery uri(String uri) {
		if (StringUtils.isNotEmpty(uri)) {
			addCriterion(Restrictions.eq("uri", uri));
		}
		return this;
	}

	/** 添加资源地址模糊查询条件 */
	public ApiResourceQuery uriLike(String uri) {
		if (StringUtils.isNotEmpty(uri)) {
			addCriterion(Restrictions.ilike("uri", uri, MatchMode.ANYWHERE));
		}
		return this;
	}

	/** 添加资源名称查询条件 */
	public ApiResourceQuery name(String name) {
		if (StringUtils.isNotEmpty(name)) {
			addCriterion(Restrictions.eq("name", name));
		}
		return this;
	}

	/** 添加资源名称模糊查询条件 */
	public ApiResourceQuery nameLike(String name) {
		if (StringUtils.isNotEmpty(name)) {
			addCriterion(Restrictions.ilike("name", name, MatchMode.ANYWHERE));
		}
		return this;
	}
}