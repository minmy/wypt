package cn.sini.cgb.api.cgb.entity.verification;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Where;

import cn.sini.cgb.api.cgb.entity.group.GroupCommodity;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/**
 * 商家实体
 *
 * @author gaowei
 */
@Entity
@Table(name = Business.TABLE_NAME)
public class Business extends AbstractLogicalRemoveEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "T_BUSINESS";

    /**
     * 商家类型
     */
    public enum BusinessType {
        DAXIN("大信商家"),
        COMMON("普通商家");
        private String desc;

        BusinessType(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    /** ID */
    @Id
    @TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
    @Column(name = "ID")
    private Long id;

    /** 商户编号mchId */
    @Column(name = "MCH_ID", unique = true, nullable = false)
    private String mchId;

    /** 签名密钥mchKey */
    @Column(name = "MCH_KEY", nullable = false)
    private String mchKey;

    /** 商家名称 */
    @Column(name = "NAME", nullable = false)
    private String name;

    /** 商家类型 */
    @Enumerated(EnumType.STRING)
    @Column(name = "BUSINESS_TYPE", nullable = false)
    private BusinessType businessType;

    /** 商家终端集合 */
    @OneToMany(mappedBy = "business", fetch = FetchType.LAZY)
    private Set<BusinessTerminal> businessTerminals = new HashSet<BusinessTerminal>();

    /** 商品集合 */
    @OneToMany(mappedBy = "business", fetch = FetchType.LAZY)
    @Where(clause = "remove='false'")
    @OrderBy("sort asc")
    private Set<GroupCommodity> groupCommoditys = new HashSet<GroupCommodity>();

    /**
     * 获取ID
     *
     * @return idID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置ID
     *
     * @param id ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取商户编号mchId
     *
     * @return mchId商户编号mchId
     */
    public String getMchId() {
        return mchId;
    }

    /**
     * 设置商户编号mchId
     *
     * @param mchId 商户编号mchId
     */
    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    /**
     * 获取签名密钥mchKey
     *
     * @return mchKey签名密钥mchKey
     */
    public String getMchKey() {
        return mchKey;
    }

    /**
     * 设置签名密钥mchKey
     *
     * @param mchKey 签名密钥mchKey
     */
    public void setMchKey(String mchKey) {
        this.mchKey = mchKey;
    }

    /**
     * 获取商家名称
     *
     * @return name商家名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置商家名称
     *
     * @param name 商家名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取商家类型
     *
     * @return businessType商家类型
     */
    public BusinessType getBusinessType() {
        return businessType;
    }

    /**
     * 设置商家类型
     *
     * @param businessType 商家类型
     */
    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    /**
     * 获取商家终端集合
     *
     * @return businessTerminals商家终端集合
     */
    public Set<BusinessTerminal> getBusinessTerminals() {
        return businessTerminals;
    }

    /**
     * 设置商家终端集合
     *
     * @param businessTerminals 商家终端集合
     */
    public void setBusinessTerminals(Set<BusinessTerminal> businessTerminals) {
        this.businessTerminals = businessTerminals;
    }

    /**
     * 获取商品集合
     *
     * @return groupCommoditys商品集合
     */
    public Set<GroupCommodity> getGroupCommoditys() {
        return groupCommoditys;
    }

    /**
     * 设置商品集合
     *
     * @param groupCommoditys 商品集合
     */
    public void setGroupCommoditys(Set<GroupCommodity> groupCommoditys) {
        this.groupCommoditys = groupCommoditys;
    }
}
