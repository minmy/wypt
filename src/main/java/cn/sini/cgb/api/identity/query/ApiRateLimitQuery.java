package cn.sini.cgb.api.identity.query;

import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.admin.identity.entity.User;
import cn.sini.cgb.api.identity.entity.ApiRateLimit;
import cn.sini.cgb.api.identity.entity.ApiResource;
import cn.sini.cgb.common.entity.AbstractEntity;
import cn.sini.cgb.common.query.AbstractQuery;

/**
 * 接口访问资源频率限制查询类
 * 
 * @author 杨海彬
 */
public class ApiRateLimitQuery extends AbstractQuery {

	@Override
	protected Class<? extends AbstractEntity> queryEntity() {
		return ApiRateLimit.class;
	}

	/** 添加对应的用户查询条件 */
	public ApiRateLimitQuery user(User user) {
		if (user != null) {
			addCriterion(Restrictions.eq("user", user));
		}
		return this;
	}

	/** 添加接口对应的资源查询条件 */
	public ApiRateLimitQuery apiResource(ApiResource apiResource) {
		if (apiResource != null) {
			addCriterion(Restrictions.eq("apiResource", apiResource));
		}
		return this;
	}
}