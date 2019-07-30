package cn.sini.cgb.api.identity.query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.admin.identity.entity.User;
import cn.sini.cgb.api.identity.entity.ApiAccessToken;
import cn.sini.cgb.common.entity.AbstractEntity;
import cn.sini.cgb.common.query.AbstractQuery;

/**
 * 接口访问令牌查询类
 * 
 * @author 杨海彬
 */
public class ApiAccessTokenQuery extends AbstractQuery {

	@Override
	protected Class<? extends AbstractEntity> queryEntity() {
		return ApiAccessToken.class;
	}

	/** 添加访问令牌查询条件 */
	public ApiAccessTokenQuery accessToken(String accessToken) {
		if (StringUtils.isNotEmpty(accessToken)) {
			addCriterion(Restrictions.eq("accessToken", accessToken));
		}
		return this;
	}

	/** 添加所属用户查询条件 */
	public ApiAccessTokenQuery user(User user) {
		if (user != null) {
			addCriterion(Restrictions.eq("user", user));
		}
		return this;
	}
}