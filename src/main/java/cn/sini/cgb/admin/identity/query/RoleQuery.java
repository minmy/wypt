package cn.sini.cgb.admin.identity.query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.admin.identity.entity.Role;
import cn.sini.cgb.common.entity.AbstractEntity;
import cn.sini.cgb.common.query.AbstractQuery;

/**
 * 角色查询类
 * 
 * @author 杨海彬
 */
public class RoleQuery extends AbstractQuery {

	@Override
	protected Class<? extends AbstractEntity> queryEntity() {
		return Role.class;
	}

	/** 添加名称查询条件 */
	public RoleQuery name(String name) {
		if (StringUtils.isNotEmpty(name)) {
			addCriterion(Restrictions.eq("name", name));
		}
		return this;
	}

	/** 添加名称模糊查询条件 */
	public RoleQuery nameLike(String name) {
		if (StringUtils.isNotEmpty(name)) {
			addCriterion(Restrictions.ilike("name", name, MatchMode.ANYWHERE));
		}
		return this;
	}
}