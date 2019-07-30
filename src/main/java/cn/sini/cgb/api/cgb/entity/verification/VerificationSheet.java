package cn.sini.cgb.api.cgb.entity.verification;

import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

import javax.persistence.*;

/**
 * 核销单
 *
 * @author lijainxin
 */
@Entity
@Table(name = VerificationSheet.TABLE_NAME)
public class VerificationSheet extends AbstractLogicalRemoveEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "T_VERIFICATION_SHEET";

    /**
     * 核销状态
     */
    public enum VerificationStatus {

        CONSUMED("已核销"),
        UNCONSUMED("未核销");

        private String desc;

        VerificationStatus(String desc) {
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
     * 核销流水号
     */
    @Column(name = "VERIFICATION_CODE", unique = true, nullable = false)
    private String verificationCode;

    /**
     * 商户编号
     */
    @Column(name = "MCH_ID", nullable = false)
    private String mchId;

    /**
     * pos终端机的编号
     */
    @Column(name = "POS_ID", nullable = false)
    private String posId;

    /**
     * 消费者OpenId
     */
    @Column(name = "OPEN_ID", nullable = false)
    private String openId;

    /**
     * 订单编号
     */
    @Column(name = "ORDER_NUMBER", nullable = false)
    private String orderNumber;

    /**
     * 订单商品id
     */
    @Column(name = "ORDER_GOODS_NUMBER", nullable = false)
    private String orderGoodsNumber;

    /**
     * 核销人
     */
    @Column(name = "VERIFICATIONER", nullable = false)
    private String verificationer;

    /**
     * 核销状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "ORDER_STATES", nullable = false)
    private VerificationStatus status;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getPosId() {
        return posId;
    }

    public void setPosId(String posId) {
        this.posId = posId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public VerificationStatus getStatus() {
        return status;
    }

    public void setStatus(VerificationStatus status) {
        this.status = status;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getOrderGoodsNumber() {
        return orderGoodsNumber;
    }

    public void setOrderGoodsNumber(String orderGoodsNumber) {
        this.orderGoodsNumber = orderGoodsNumber;
    }

    public String getVerificationer() {
        return verificationer;
    }

    public void setVerificationer(String verificationer) {
        this.verificationer = verificationer;
    }
}
