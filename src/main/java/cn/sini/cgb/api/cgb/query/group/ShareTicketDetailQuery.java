package cn.sini.cgb.api.cgb.query.group;

import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.api.cgb.entity.group.ShareTicket;
import cn.sini.cgb.api.cgb.entity.group.ShareTicketDetail;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;
import org.hibernate.criterion.Restrictions;

/**
 * 分享劵明细查询类
 *
 * @author lijianxin
 */
public class ShareTicketDetailQuery extends AbstractLogicalRemoveQuery {
    @Override
    protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
        return ShareTicketDetail.class;
    }

    /**
     * 分享者
     */
    public ShareTicketDetailQuery sharer(WeChatUser sharer) {
        if (sharer != null) {
            addCriterion(Restrictions.eq("sharer", sharer));
        }
        return this;
    }

    /**
     * 被分享者
     */
    public ShareTicketDetailQuery relatedUser(WeChatUser relatedUser) {
        if (relatedUser != null) {
            addCriterion(Restrictions.eq("relatedUser", relatedUser));
        }
        return this;
    }

    /**
     * 获得途径
     */
    public ShareTicketDetailQuery getWay(ShareTicketDetail.GetWay getWay) {
        if (getWay != null) {
            addCriterion(Restrictions.eq("getWay", getWay));
        }
        return this;
    }

    /**
     * 变动方法
     */
    public ShareTicketDetailQuery changeType(ShareTicketDetail.ChangeType changeType) {
        if (changeType != null) {
            addCriterion(Restrictions.eq("changeType", changeType));
        }
        return this;
    }

    /**
     * 添加分享劵总表查询条件
     */
    public ShareTicketDetailQuery shareTicket(ShareTicket shareTicket) {
        if (shareTicket != null) {
            addCriterion(Restrictions.eq("shareTicket", shareTicket));
        }
        return this;
    }

    /**
     * 添加分享劵总表查询条件
     */
    public ShareTicketDetailQuery shareQuery(ShareQuery shareQuery) {
        if (shareQuery != null) {
            addCriterion(Restrictions.eq("share", shareQuery));
        }
        return this;
    }

    public ShareTicketDetailQuery orderNumber(String orderNumber) {
        if (orderNumber != null) {
            addCriterion(Restrictions.eq("orderNumber", orderNumber));
        }
        return this;
    }
}
