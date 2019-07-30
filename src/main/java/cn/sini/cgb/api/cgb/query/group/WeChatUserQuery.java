package cn.sini.cgb.api.cgb.query.group;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.group.Community;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

public class WeChatUserQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return WeChatUser.class;
	}

	/** 添加openId查询条件 */
	public WeChatUserQuery openId(String openId) {
		if (StringUtils.isNotEmpty(openId)) {
			addCriterion(Restrictions.eq("openId", openId));
		}
		return this;
	}

	/** 添加联系人(昵称)模糊查询条件 */
	public WeChatUserQuery contactsLike(String contacts) {
		if (StringUtils.isNotEmpty(contacts)) {
			addCriterion(Restrictions.like("contacts", contacts, MatchMode.ANYWHERE));
		}
		return this;
	}

	/** 添加微信昵称模糊查询条件 */
	public WeChatUserQuery nameLike(String name) {
		if (StringUtils.isNotEmpty(name)) {
			addCriterion(Restrictions.like("name", name, MatchMode.ANYWHERE));
		}
		return this;
	}

	/** 添加里长查询条件 */
	public WeChatUserQuery brigadier(Boolean brigadier) {
		if (brigadier != null) {
			addCriterion(Restrictions.eq("brigadier", brigadier));
		}
		return this;
	}
	
	/** 添加小区查询条件 */
	public WeChatUserQuery community(Community community) {
		if (community != null) {
			addCriterion(Restrictions.eq("community", community));
		}
		return this;
	}
}
