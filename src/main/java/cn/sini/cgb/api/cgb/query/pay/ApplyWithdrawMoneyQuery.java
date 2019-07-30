package cn.sini.cgb.api.cgb.query.pay;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.pay.ApplyWithdrawMoney;
import cn.sini.cgb.api.cgb.entity.pay.ApplyWithdrawMoney.ExamineState;
import cn.sini.cgb.api.cgb.entity.pay.ApplyWithdrawMoney.HandleState;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

/**
 * 虚拟账户流水表查询实体
 *
 * @author gaowei
 */
public class ApplyWithdrawMoneyQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return ApplyWithdrawMoney.class;
	}

	/** 添加提现申请编号查询条件 */
	public ApplyWithdrawMoneyQuery applyNumber(String applyNumber) {
		if (StringUtils.isNotEmpty(applyNumber)) {
			addCriterion(Restrictions.like("applyNumber", applyNumber, MatchMode.ANYWHERE));
		}
		return this;
	}

	/** 添加openId查询条件 */
	public ApplyWithdrawMoneyQuery openId(String openId) {
		if (StringUtils.isNotEmpty(openId)) {
			addCriterion(Restrictions.eq("openId", openId));
		}
		return this;
	}

	/** 添加处理状态 查询条件 */
	public ApplyWithdrawMoneyQuery handleState(HandleState handleState) {
		if (handleState != null) {
			addCriterion(Restrictions.eq("handleState", handleState));
		}
		return this;
	}

	/** 添加审核状态 查询条件 */
	public ApplyWithdrawMoneyQuery examineState(ExamineState examineState) {
		if (examineState != null) {
			addCriterion(Restrictions.eq("examineState", examineState));
		}
		return this;
	}

	/** 添加申请人姓名查询条件 */
	public ApplyWithdrawMoneyQuery applyName(String applyName) {
		if (StringUtils.isNotEmpty(applyName)) {
			addCriterion(Restrictions.like("applyName", applyName, MatchMode.ANYWHERE));
		}
		return this;
	}

	/** 添加申请人电话查询条件 */
	public ApplyWithdrawMoneyQuery applyPhone(String applyPhone) {
		if (StringUtils.isNotEmpty(applyPhone)) {
			addCriterion(Restrictions.eq("applyPhone", applyPhone));
		}
		return this;
	}

	/** 添加申请人微信查询条件 */
	public ApplyWithdrawMoneyQuery applyWeChat(String applyWeChat) {
		if (StringUtils.isNotEmpty(applyWeChat)) {
			addCriterion(Restrictions.like("applyWeChat", applyWeChat, MatchMode.ANYWHERE));
		}
		return this;
	}

	/** 添加大于等于创建时间查询条件 */
	public ApplyWithdrawMoneyQuery createTimeGe(Date createTime) {
		if (createTime != null) {
			addCriterion(Restrictions.ge("createTime", createTime));
		}
		return this;
	}

	/** 添加小于等于创建时间查询条件 */
	public ApplyWithdrawMoneyQuery createTimeLe(Date createTime) {
		if (createTime != null) {
			addCriterion(Restrictions.le("createTime", createTime));
		}
		return this;
	}

	/** 是否发现邮件查询条件 */
	public ApplyWithdrawMoneyQuery sendEmail(Boolean sendEmail) {
		if (sendEmail != null) {
			addCriterion(Restrictions.eq("sendEmail", sendEmail));
		}
		return this;
	}
}
