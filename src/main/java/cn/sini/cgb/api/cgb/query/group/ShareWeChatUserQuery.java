package cn.sini.cgb.api.cgb.query.group;

import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.group.Share;
import cn.sini.cgb.api.cgb.entity.group.ShareWeChatUser;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

public class ShareWeChatUserQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return ShareWeChatUser.class;
	}

	/** 添加用户查询条件 */
	public ShareWeChatUserQuery weChatUser(WeChatUser weChatUser) {
		if (weChatUser != null) {
			addCriterion(Restrictions.eq("weChatUser", weChatUser));
		}
		return this;
	}

	/** 添加所属团单查询条件 */
	public ShareWeChatUserQuery share(Share share) {
		if (share != null) {
			addCriterion(Restrictions.eq("share", share));
		}
		return this;
	}
}
