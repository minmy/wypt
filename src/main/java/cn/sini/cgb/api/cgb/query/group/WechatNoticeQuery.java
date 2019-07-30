package cn.sini.cgb.api.cgb.query.group;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.group.WechatNotice;
import cn.sini.cgb.api.cgb.entity.group.WechatNotice.NoticeType;
import cn.sini.cgb.common.entity.AbstractEntity;
import cn.sini.cgb.common.query.AbstractQuery;

public class WechatNoticeQuery extends AbstractQuery{

	@Override
	protected Class<? extends AbstractEntity> queryEntity() {
		return WechatNotice.class;
	}
	/** 添加所属订单商品查询条件 */
	public WechatNoticeQuery groupId(Long groupId) {
		if (groupId != null) {
			addCriterion(Restrictions.eq("groupId", groupId));
		}
		return this;
	}
	public WechatNoticeQuery openId(String openId) {
		if (StringUtils.isNotEmpty(openId)) {
			addCriterion(Restrictions.eq("openId", openId));
		}
		return this;
	}
	public WechatNoticeQuery noticeType(NoticeType noticeType) {
		addCriterion(Restrictions.eq("noticeType", noticeType));
		return this;
	}
	/** 添加小于等于创建时间查询条件 */
	public WechatNoticeQuery createTimeLe(Date createTime) {
		if (createTime != null) {
			addCriterion(Restrictions.le("createTime", createTime));
		}
		return this;
	}
}
