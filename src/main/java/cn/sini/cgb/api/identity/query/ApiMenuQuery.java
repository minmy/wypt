package cn.sini.cgb.api.identity.query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.identity.entity.ApiMenu;
import cn.sini.cgb.common.entity.AbstractEntity;
import cn.sini.cgb.common.query.AbstractQuery;

/**
 * 接口菜单查询类
 * 
 * @author qi
 */
public class ApiMenuQuery extends AbstractQuery {

	@Override
	protected Class<? extends AbstractEntity> queryEntity() {
		return ApiMenu.class;
	}

	/** 添加角色id查询条件 */
	public ApiMenuQuery apiRoleId(Long apiRoleId) {
		if (apiRoleId != null) {
			addCriterion(Restrictions.eq("apiRoleId", apiRoleId));
		}
		return this;
	}

	/** 添加名称查询条件 */
	public ApiMenuQuery name(String name) {
		if (StringUtils.isNotEmpty(name)) {
			addCriterion(Restrictions.eq("name", name));
		}
		return this;
	}

	/** 添加名称模糊查询条件 */
	public ApiMenuQuery nameLike(String name) {
		if (StringUtils.isNotEmpty(name)) {
			addCriterion(Restrictions.ilike("name", name, MatchMode.ANYWHERE));
		}
		return this;
	}
}