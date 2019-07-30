package cn.sini.cgb.api.cgb.query.integral;

import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.integral.UpgradeCommodities;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

/**
 * 商品升级关系查询类
 * 
 * @author gaowei
 */
public class UpgradeCommoditiesQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return UpgradeCommodities.class;
	}

	/** 添加升级后的商品ID的查询条件 */
	public UpgradeCommoditiesQuery newCommodityId(Long newCommodityId) {
		if (newCommodityId != null) {
			addCriterion(Restrictions.eq("newCommodityId", newCommodityId));
		}
		return this;
	}

	/** 添加升级前的商品ID的查询条件 */
	public UpgradeCommoditiesQuery oldCommodityId(Long oldCommodityId) {
		if (oldCommodityId != null) {
			addCriterion(Restrictions.eq("oldCommodityId", oldCommodityId));
		}
		return this;
	}
}
