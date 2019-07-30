package cn.sini.cgb.admin.annex.entity;

import java.io.File;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.Session;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import cn.sini.cgb.api.cgb.entity.group.GroupCommodity;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.api.cgb.entity.group.Order;
import cn.sini.cgb.api.cgb.entity.group.Share;
import cn.sini.cgb.common.entity.AbstractEntity;
import cn.sini.cgb.common.util.Environment;

/**
 * 附件实体
 * 
 * @author 杨海彬
 */
@Entity
@Table(name = Annex.TABLE_NAME)
public class Annex extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_ANNEX";

	/** 所属类型 */
	public enum AnnexType {
		/** 团单图 */
		GROUP_ORDER_PIC("团单图", "jpg,png,jpeg"),
		/** 商品图 */
		GROUP_COMMODITY_PIC("商品图", "jpg,png,jpeg"),
		/** 订单提货图 */
		ORDER_PICK_PIC("订单提货图", "jpg,png,jpeg"),
		/** 其他图片 */
		OTHER_PIC("其他图", "jpg,png,jpeg"),
		/** 配送单 */
		GROUP_ORDER_PSD_EXCEL("配送单", "xls,xlsx"),
		/** 二维码 */
		QR_CODE("二维码", "jpg,png,jpeg"),
		/** 分享图 */
		SHARE_PIC("分享图", "jpg,png,jpeg"),
		/** 提货二维码 */
		VERIFICATION_QR_CODE("提货二维码", "jpg,png,jpeg");

		private String desc;
		private String allowSuffix;

		private AnnexType(String desc, String allowSuffix) {
			this.desc = desc;
			this.allowSuffix = allowSuffix;
		}

		/** 获取枚举的真实值 */
		public String getName() {
			return this.name();
		}

		/** 获取枚举的描述值 */
		public String getDesc() {
			return this.desc;
		}

		/** 获取允许的后缀值 */
		public String getAllowSuffix() {
			return this.allowSuffix;
		}
	}

	/** ID */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "ID")
	private String id;

	/** 所属类型 */
	@Enumerated(EnumType.STRING)
	@Column(name = "ANNEX_TYPE")
	private AnnexType annexType;

	/** 文件名 */
	@Column(name = "FILE_NAME", nullable = false)
	private String fileName;

	/** 文件相对路径 */
	@Column(name = "FILE_PATH", nullable = false)
	private String filePath;

	/** 所属团单图片 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_GROUP_ORDER")
	private GroupOrder groupOrder;

	/** 所属商品图片 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_GROUP_COMMODITY")
	private GroupCommodity groupCommodity;

	/** 所属订单提货图片 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_ORDER")
	private Order order;

	/** 排序ID */
	@Column(name = "SORT_")
	private Long sort;

	/** 分享图片 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_SHARE")
	private Share share;

	/**
	 * 获取分享图片
	 * 
	 * @return share分享图片
	 */
	public Share getShare() {
		return share;
	}

	/**
	 * 设置分享图片
	 * 
	 * @param share 分享图片
	 */
	public void setShare(Share share) {
		this.share = share;
	}

	/**
	 * 获取所属订单提货图片
	 * 
	 * @return order所属订单提货图片
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * 设置所属订单提货图片
	 * 
	 * @param order 所属订单提货图片
	 */
	public void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * 获取ID
	 * 
	 * @return id ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置ID
	 * 
	 * @param id ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取所属类型
	 * 
	 * @return annexType所属类型
	 */
	public AnnexType getAnnexType() {
		return annexType;
	}

	/**
	 * 设置所属类型
	 * 
	 * @param annexType 所属类型
	 */
	public void setAnnexType(AnnexType annexType) {
		this.annexType = annexType;
	}

	/**
	 * 获取文件名
	 * 
	 * @return fileName 文件名
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 设置文件名
	 * 
	 * @param fileName 文件名
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 获取文件相对路径
	 * 
	 * @return filePath 文件相对路径
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * 设置文件相对路径
	 * 
	 * @param filePath 文件相对路径
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/** 获取文件扩展名 */
	public String getFileExtension() {
		return "." + StringUtils.getFilenameExtension(this.fileName);
	}

	/**
	 * 获取所属团单图片
	 * 
	 * @return groupOrder所属团单图片
	 */
	public GroupOrder getGroupOrder() {
		return groupOrder;
	}

	/**
	 * 设置所属团单图片
	 * 
	 * @param groupOrder 所属团单图片
	 */
	public void setGroupOrder(GroupOrder groupOrder) {
		this.groupOrder = groupOrder;
	}

	/**
	 * 获取所属商品图片
	 * 
	 * @return groupCommodity所属商品图片
	 */
	public GroupCommodity getGroupCommodity() {
		return groupCommodity;
	}

	/**
	 * 设置所属商品图片
	 * 
	 * @param groupCommodity 所属商品图片
	 */
	public void setGroupCommodity(GroupCommodity groupCommodity) {
		this.groupCommodity = groupCommodity;
	}

	/**
	 * 获取排序ID
	 * 
	 * @return sort排序ID
	 */
	public Long getSort() {
		return sort;
	}

	/**
	 * 设置排序ID
	 * 
	 * @param sort 排序ID
	 */
	public void setSort(Long sort) {
		this.sort = sort;
	}

	@Override
	public void remove(Session session) {
		super.remove(session);
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
			@Override
			public void afterCompletion(int status) {
				if (TransactionSynchronization.STATUS_COMMITTED == status) {
					new File(Environment.getProperty("annex.path") + filePath).delete();
				}
			}
		});
	}
}