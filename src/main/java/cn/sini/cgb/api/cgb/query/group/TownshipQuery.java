package cn.sini.cgb.api.cgb.query.group;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.group.Township;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

/**
 * 镇区查询类
 * 
 * @author gaowei
 */
public class TownshipQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return Township.class;
	}

	/** 添加镇区编号查询条件 */
	public TownshipQuery code(String code) {
		if (StringUtils.isNotBlank(code)) {
			addCriterion(Restrictions.eq("code", code));
		}
		return this;
	}
}
