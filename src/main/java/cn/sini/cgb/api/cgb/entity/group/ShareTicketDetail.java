package cn.sini.cgb.api.cgb.entity.group;

import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

import javax.persistence.*;

/**
 * 分享劵明细
 *
 * @author lijianxin
 */
@Entity
@Table(name = ShareTicketDetail.TABLE_NAME)
public class ShareTicketDetail extends AbstractLogicalRemoveEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "T_SHARE_TICKET_DETAIL";

    /**
     * 获取途径（不要管单词什么意思，需求改了后，不想动代码，意思看desc）
     */
    public enum GetWay {
        /**
         * 分享
         */
        SHARE("分享"),
        /**
         * 分享-受邀
         */
        CLICK("分享-受邀"),
        /**
         * 购买-受邀
         */
        BUY("购买-受邀"),
        /**
         * 购买
         */
        FEEDBACK("购买");

        public String desc;

        GetWay(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 变动项目
     */
    public enum ChangeType {
        /**
         * 获取
         */
        GAIN("获取"),

        /**
         * 兑换
         */
        EXCHANGE("兑换"),

        /**
         * 退款
         */
        REFUND("退款");

        public String desc;

        ChangeType(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * ID
     */
    @Id
    @TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
    @Column(name = "ID")
    private Long id;

    /**
     * 分享劵
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_SHARE_TICKET", nullable = false)
    private ShareTicket shareTicket;

    /**
     * 与这条记录相关的人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_RELATEDUSER")
    private WeChatUser relatedUser;

    /**
     * 分享单
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_SHARE")
    private Share share;

    /**
     * 数量
     */
    @Column(name = "AMOUNT", nullable = false)
    private Integer amount = 0;

    /**
     * 获得途径
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "GET_WAY")
    private GetWay getWay;

    /**
     * 变动类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "CHANGE_TYPE", nullable = false)
    private ChangeType changeType;

    /**
     * 变动原因
     */
    @Column(name = "CAUSE_CHANGE")
    private String causeChange;

    /**
     * 订单编号
     */
    @Column(name = "ORDER_NUMBER")
    private String orderNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ShareTicket getShareTicket() {
        return shareTicket;
    }

    public void setShareTicket(ShareTicket shareTicket) {
        this.shareTicket = shareTicket;
    }

    public WeChatUser getRelatedUser() {
        return relatedUser;
    }

    public void setRelatedUser(WeChatUser relatedUser) {
        this.relatedUser = relatedUser;
    }

    public Share getShare() {
        return share;
    }

    public void setShare(Share share) {
        this.share = share;
    }

    public GetWay getGetWay() {
        return getWay;
    }

    public void setGetWay(GetWay getWay) {
        this.getWay = getWay;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public String getCauseChange() {
        return causeChange;
    }

    public void setCauseChange(String causeChange) {
        this.causeChange = causeChange;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
}


