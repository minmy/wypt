package cn.sini.cgb.api.cgb.query.group;

import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.api.cgb.entity.group.ShareTicket;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;
import org.hibernate.criterion.Restrictions;

import java.io.Serializable;

/**
 * 分享劵查询类
 *
 * @author lijianxin
 */
public class ShareTicketQuery extends AbstractLogicalRemoveQuery {
    @Override
    protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
        return ShareTicket.class;
    }

    public ShareTicketQuery id(Serializable... id) {
        super.id(id);
        return this;
    }

    /**
     * 添加所属团单查询条件
     */
    public ShareTicketQuery groupOrder(GroupOrder groupOrder) {
        if (groupOrder != null) {
            addCriterion(Restrictions.eq("groupOrder", groupOrder));
        }
        return this;
    }

    /**
     * 添加拥有者分享劵总表查询条件
     */
    public ShareTicketQuery owner(WeChatUser owner) {
        if (owner != null) {
            addCriterion(Restrictions.eq("owner", owner));
        }
        return this;
    }

    /**
     * 总数大于某数的条件查询
     *
     * @param discountNumber 某数
     */
    public ShareTicketQuery enableExchange(Integer discountNumber) {
        if (null != discountNumber) {
            addCriterion(Restrictions.ge("amount", discountNumber));
        }
        return this;
    }

    /**
     * 是否排序
     *
     * @param asc 顺序
     */
    public ShareTicketQuery orderByAmount(Boolean asc) {
        if (null != asc) {
            this.orderBy("amount", asc);
        }
        return this;
    }

    public ShareTicketQuery shareTicketDetailQuery(ShareTicketDetailQuery shareTicketDetailQuery) {
        if (null != shareTicketDetailQuery) {
            addCriteria("shareTicketDetails", shareTicketDetailQuery);
        }
        return this;
    }
}
