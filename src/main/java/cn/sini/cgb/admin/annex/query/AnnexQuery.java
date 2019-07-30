package cn.sini.cgb.admin.annex.query;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.admin.annex.entity.Annex;
import cn.sini.cgb.admin.annex.entity.Annex.AnnexType;
import cn.sini.cgb.api.cgb.entity.group.GroupCommodity;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.api.cgb.entity.group.Order;
import cn.sini.cgb.common.entity.AbstractEntity;
import cn.sini.cgb.common.query.AbstractQuery;

/**
 * 附件查询类
 * 
 * @author 杨海彬
 */
public class AnnexQuery extends AbstractQuery {

	@Override
	protected Class<? extends AbstractEntity> queryEntity() {
		return Annex.class;
	}

	/** 添加团单图片查询条件 */
	public AnnexQuery groupOrder(GroupOrder groupOrder) {
		if (groupOrder != null) {
			addCriterion(Restrictions.eq("groupOrder", groupOrder));
		}
		return this;
	}

	/** 添加商品图片查询条件 */
	public AnnexQuery groupCommodity(GroupCommodity groupCommodity) {
		if (groupCommodity != null) {
			addCriterion(Restrictions.eq("groupCommodity", groupCommodity));
		}
		return this;
	}

	/** 添加附件类型查询条件 */
	public AnnexQuery annexType(AnnexType annexType) {
		if (annexType != null) {
			addCriterion(Restrictions.eq("annexType", annexType));
		}
		return this;
	}

	/** 添加附件名称查询条件 */
	public AnnexQuery fileName(String fileName) {
		if (StringUtils.isNotBlank(fileName)) {
			addCriterion(Restrictions.eq("fileName", fileName));
		}
		return this;
	}

	/** 添加附件名称查询条件 */
	public AnnexQuery createTime(Date createTime, Date endTime) {
		if (createTime != null) {
			addCriterion(Restrictions.between("createTime", createTime, endTime));
		}
		return this;
	}

	/** 添加订单查询条件 */
	public AnnexQuery order(Order order) {
		if (order != null) {
			addCriterion(Restrictions.eq("order", order));
		}
		return this;
	}
}