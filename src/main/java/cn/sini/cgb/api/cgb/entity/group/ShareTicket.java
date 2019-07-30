package cn.sini.cgb.api.cgb.entity.group;

import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Set;

/**
 * 分享劵
 *
 * @author lijianxin
 */
@Entity
@Table(name = ShareTicket.TABLE_NAME)
public class ShareTicket extends AbstractLogicalRemoveEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "T_SHARE_TICKET";

    /**
     * ID
     */
    @Id
    @TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
    @Column(name = "ID")
    private Long id;

    /**
     * 拥有者
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_OWNER", nullable = false)
    private WeChatUser owner;

    /**
     * 团单
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_GROUP_ORDER", nullable = false)
    private GroupOrder groupOrder;

    /**
     * 明细
     */
    @OneToMany(mappedBy = "shareTicket", fetch = FetchType.LAZY)
    @Where(clause = "remove='false'")
    @OrderBy("createTime desc")
    private Set<ShareTicketDetail> shareTicketDetails;

    /**
     * 数量
     */
    @Column(name = "AMOUNT", nullable = false)
    private Integer amount = 0;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WeChatUser getOwner() {
        return owner;
    }

    public void setOwner(WeChatUser owner) {
        this.owner = owner;
    }

    public GroupOrder getGroupOrder() {
        return groupOrder;
    }

    public void setGroupOrder(GroupOrder groupOrder) {
        this.groupOrder = groupOrder;
    }

    public Set<ShareTicketDetail> getShareTicketDetails() {
        return shareTicketDetails;
    }

    public void setShareTicketDetails(Set<ShareTicketDetail> shareTicketDetails) {
        this.shareTicketDetails = shareTicketDetails;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}


