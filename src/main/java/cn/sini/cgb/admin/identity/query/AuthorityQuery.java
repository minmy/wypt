package cn.sini.cgb.admin.identity.query;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.admin.identity.entity.Authority;
import cn.sini.cgb.common.entity.AbstractEntity;
import cn.sini.cgb.common.query.AbstractQuery;
import cn.sini.cgb.common.util.CommonUtils;

/**
 * 权限查询类
 * 
 * @author 杨海彬
 */
public class AuthorityQuery extends AbstractQuery {

	@Override
	protected Class<? extends AbstractEntity> queryEntity() {
		return Authority.class;
	}

	/** 添加权限查询条件 */
	public AuthorityQuery authority(String... authority) {
		Object[] values = CommonUtils.removeEmptyElement(authority);
		if (ArrayUtils.isNotEmpty(values)) {
			if (values.length == 1) {
				addCriterion(Restrictions.eq("authority", values[0]));
			} else {
				addCriterion(Restrictions.in("authority", values));
			}
		}
		return this;
	}

	/** 添加权限模糊查询条件 */
	public AuthorityQuery authorityLike(String authority) {
		if (StringUtils.isNotEmpty(authority)) {
			addCriterion(Restrictions.ilike("authority", authority, MatchMode.ANYWHERE));
		}
		return this;
	}

	/** 添加名称模糊查询条件 */
	public AuthorityQuery nameLike(String name) {
		if (StringUtils.isNotEmpty(name)) {
			addCriterion(Restrictions.ilike("name", name, MatchMode.ANYWHERE));
		}
		return this;
	}

	/** 添加排序权重最大值投影规则 */
	public AuthorityQuery maxSortWeight() {
		addProjection(Projections.max("sortWeight"));
		return this;
	}
}