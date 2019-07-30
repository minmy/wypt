package cn.sini.cgb.api.cgb.query.group;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.admin.identity.entity.address.TaAdministrativeDivision;
import cn.sini.cgb.admin.identity.query.address.TaAdministrativeDivisionQuery;
import cn.sini.cgb.api.cgb.entity.group.Community;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

/**
 * 小区查询类
 * 
 * @author gaowei
 */
public class CommunityQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return Community.class;
	}

	/** 添加小区名称查询条件 */
	public CommunityQuery name(String name) {
		if (StringUtils.isNotBlank(name)) {
			addCriterion(Restrictions.eq("name", name));
		}
		return this;
	}

	/** 添加小区名称模糊查询条件 */
	public CommunityQuery nameLike(String name) {
		if (StringUtils.isNotBlank(name)) {
			addCriterion(Restrictions.like("name", name, MatchMode.ANYWHERE));
		}
		return this;
	}

	/** 添加镇区查询条件(一期) */
	public CommunityQuery townshipQuery(TownshipQuery townshipQuery) {
		if (townshipQuery != null) {
			addCriteria("township", townshipQuery);
		}
		return this;
	}

	/** 添加镇区查询条件(二期) */
	public CommunityQuery taAdministrativeDivisionQuery(TaAdministrativeDivisionQuery taAdministrativeDivisionQuery) {
		if (taAdministrativeDivisionQuery != null) {
			addCriteria("town", taAdministrativeDivisionQuery);
		}
		return this;
	}

	/** 添加镇区查询条件(二期) */
	public CommunityQuery taAdministrativeDivision(TaAdministrativeDivision taAdministrativeDivision) {
		if (taAdministrativeDivision != null) {
			addCriterion(Restrictions.eq("town", taAdministrativeDivision));
		}
		return this;
	}
}
