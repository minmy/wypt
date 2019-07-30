package cn.sini.cgb.api.cgb.query.group;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.api.cgb.entity.group.Share;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

public class ShareQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return Share.class;
	}

	/** 添加用户查询条件 */
	public ShareQuery weChatUser(WeChatUser weChatUser) {
		if (weChatUser != null) {
			addCriterion(Restrictions.eq("weChatUser", weChatUser));
		}
		return this;
	}

	/** 添加所属团单查询条件 */
	public ShareQuery groupOrder(GroupOrder groupOrder) {
		if (groupOrder != null) {
			addCriterion(Restrictions.eq("groupOrder", groupOrder));
		}
		return this;
	}

	/** 添加拼团链接查询条件 */
	public ShareQuery groupOrderQuery(GroupOrderQuery groupOrderQuery) {
		if (groupOrderQuery != null) {
			addCriteria("groupOrder", groupOrderQuery);
		}
		return this;
	}

	/** 添加拼团链接查询条件 */
	public ShareQuery randomNumber(String randomNumber) {
		if (StringUtils.isNotBlank(randomNumber)) {
			addCriterion(Restrictions.eq("randomNumber", randomNumber));
		}
		return this;
	}
}
