package cn.sini.cgb.api.identity.query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.identity.entity.ApiRole;
import cn.sini.cgb.common.entity.AbstractEntity;
import cn.sini.cgb.common.query.AbstractQuery;

/**
 * 接口角色查询类
 * 
 * @author 杨海彬
 */
public class ApiRoleQuery extends AbstractQuery {

	@Override
	protected Class<? extends AbstractEntity> queryEntity() {
		return ApiRole.class;
	}

	/** 添加名称查询条件 */
	public ApiRoleQuery name(String name) {
		if (StringUtils.isNotEmpty(name)) {
			addCriterion(Restrictions.eq("name", name));
		}
		return this;
	}

	/** 添加名称模糊查询条件 */
	public ApiRoleQuery nameLike(String name) {
		if (StringUtils.isNotEmpty(name)) {
			addCriterion(Restrictions.ilike("name", name, MatchMode.ANYWHERE));
		}
		return this;
	}
}