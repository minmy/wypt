package cn.sini.cgb.api.cgb.query.group;

import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.group.WayOfDelivery;
import cn.sini.cgb.api.cgb.entity.group.WayOfDelivery.DeliveryType;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

/**
 * 企业团收货方式查询类
 * 
 * @author gaowei
 */
public class WayOfDeliveryQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return WayOfDelivery.class;
	}

	/** 添加送货类型查询条件 */
	public WayOfDeliveryQuery deliveryType(DeliveryType deliveryType) {
		if (deliveryType != null) {
			addCriterion(Restrictions.eq("deliveryType", deliveryType));
		}
		return this;
	}
}
