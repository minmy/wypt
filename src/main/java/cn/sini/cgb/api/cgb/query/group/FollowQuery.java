package cn.sini.cgb.api.cgb.query.group;

import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.group.Follow;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

public class FollowQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return Follow.class;
	}

	/** 添加用户查询条件 */
	public FollowQuery weChatUser(WeChatUser weChatUser) {
		if (weChatUser != null) {
			addCriterion(Restrictions.eq("weChatUser", weChatUser));
		}
		return this;
	}

	/** 添加被关注用户查询条件 */
	public FollowQuery followUser(WeChatUser followUser) {
		if (followUser != null) {
			addCriterion(Restrictions.eq("followUser", followUser));
		}
		return this;
	}
}
