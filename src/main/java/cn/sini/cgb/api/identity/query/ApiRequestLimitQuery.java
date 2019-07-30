package cn.sini.cgb.api.identity.query;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.admin.identity.entity.User;
import cn.sini.cgb.api.identity.entity.ApiRequestLimit;
import cn.sini.cgb.common.entity.AbstractEntity;
import cn.sini.cgb.common.query.AbstractQuery;

/**
 * 接口请求限制查询类
 * 
 * @author 杨海彬
 */
public class ApiRequestLimitQuery extends AbstractQuery {

	@Override
	protected Class<? extends AbstractEntity> queryEntity() {
		return ApiRequestLimit.class;
	}

	/** 添加请求标识查询条件 */
	public ApiRequestLimitQuery requestId(String requestId) {
		if (StringUtils.isNotEmpty(requestId)) {
			addCriterion(Restrictions.eq("requestId", requestId));
		}
		return this;
	}

	/** 添加创建时间区间查询条件 */
	public ApiRequestLimitQuery createTimeBetween(Date startTime, Date endTime) {
		if (startTime != null && endTime != null) {
			addCriterion(Restrictions.between("createTime", startTime, endTime));
		} else if (startTime != null) {
			addCriterion(Restrictions.ge("createTime", startTime));
		} else if (endTime != null) {
			addCriterion(Restrictions.le("createTime", endTime));
		}
		return this;
	}

	/** 添加所属用户查询条件 */
	public ApiRequestLimitQuery user(User user) {
		if (user != null) {
			addCriterion(Restrictions.eq("user", user));
		}
		return this;
	}
}