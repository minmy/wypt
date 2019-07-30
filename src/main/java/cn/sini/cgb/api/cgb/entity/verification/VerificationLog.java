package cn.sini.cgb.api.cgb.entity.verification;

import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

import javax.persistence.*;

/**
 * 核销日志表
 *
 * @author lijainxin
 */
@Entity
@Table(name = VerificationLog.TABLE_NAME)
public class VerificationLog extends AbstractLogicalRemoveEntity {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "T_VERIFICATION_LOG";

    /**
     * 日志类型
     */
    public enum ApiType {

        QUERYORDER("查询订单"),
        WRITEOFF("核销"),
        CHECKWRITEOFF("验证核销"),
        CORRECT("冲正");

        private String desc;

        ApiType(String desc) {
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
     * 商户编号
     */
    @Column(name = "MCH_ID")
    private String mchId;

    /**
     * 请求参数
     */
    @Column(name = "KEY_")
    private String key;

    /**
     * 返回状态码
     */
    @Column(name = "STATUS", nullable = false)
    private Integer status;

    /**
     * 返回信息
     */
    @Column(name = "MESSAGE", nullable = false)
    private String message;

    /**
     * 类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "API_TYPE", nullable = false)
    private ApiType apiType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMchId() {
		return mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ApiType getApiType() {
		return apiType;
	}

	public void setApiType(ApiType apiType) {
		this.apiType = apiType;
	}

    
}
