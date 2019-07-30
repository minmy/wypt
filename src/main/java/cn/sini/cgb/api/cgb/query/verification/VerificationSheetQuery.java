package cn.sini.cgb.api.cgb.query.verification;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.verification.VerificationSheet;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;
import cn.sini.cgb.common.util.CommonUtils;

/**
 * 核销单查询类
 *
 * @author lijianxin
 */
public class VerificationSheetQuery extends AbstractLogicalRemoveQuery {

    @Override
    protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
        return VerificationSheet.class;
    }

    /**
     * 核销单流水号查询条件
     */
    public VerificationSheetQuery verificationCode(String verificationCode) {
        if (StringUtils.isNotEmpty(verificationCode)) {
            addCriterion(Restrictions.eq("verificationCode", verificationCode));
        }
        return this;
    }

    /**
     * 核销单流水号查询条件
     */
    public VerificationSheetQuery status(VerificationSheet.VerificationStatus... status) {
        Object[] values = CommonUtils.removeEmptyElement(status);
        if (ArrayUtils.isNotEmpty(values)) {
            if (values.length == 1) {
                addCriterion(Restrictions.eq("status", values[0]));
            } else {
                addCriterion(Restrictions.in("status", values));
            }
        }
        return this;
    }

    /**
     * 订单编号查询条件
     */
    public VerificationSheetQuery orderNumber(String orderNumber) {
        if (StringUtils.isNotEmpty(orderNumber)) {
            addCriterion(Restrictions.eq("orderNumber", orderNumber));
        }
        return this;
    }

    /**
     * 订单编号查询条件
     */
    public VerificationSheetQuery mchId(String mchId) {
        if (StringUtils.isNotEmpty(mchId)) {
            addCriterion(Restrictions.eq("mchId", mchId));
        }
        return this;
    }

    /**
     * 订单编号查询条件
     */
    public VerificationSheetQuery orderGoodsNumber(String orderGoodsNumber) {
        if (StringUtils.isNotEmpty(orderGoodsNumber)) {
            addCriterion(Restrictions.eq("orderGoodsNumber", orderGoodsNumber));
        }
        return this;
    }

    /**
     * 订单编号查询条件
     */
    public VerificationSheetQuery openId(String openId) {
        if (StringUtils.isNotEmpty(openId)) {
            addCriterion(Restrictions.eq("openId", openId));
        }
        return this;
    }

    /**
     * 订单编号查询条件
     */
    public VerificationSheetQuery verificationCodeLike(String verificationCode) {
        if (StringUtils.isNotEmpty(verificationCode)) {
            addCriterion(Restrictions.ilike("verificationCode", verificationCode, MatchMode.ANYWHERE));
        }
        return this;
    }

    /**
     * 订单编号查询条件
     */
    public VerificationSheetQuery mchIdLike(String mchId) {
        if (StringUtils.isNotEmpty(mchId)) {
            addCriterion(Restrictions.ilike("mchId", mchId, MatchMode.ANYWHERE));
        }
        return this;
    }
    /**
     * 订单编号查询条件
     */
    public VerificationSheetQuery posIdLike(String posId) {
        if (StringUtils.isNotEmpty(posId)) {
            addCriterion(Restrictions.ilike("posId", posId, MatchMode.ANYWHERE));
        }
        return this;
    }
    /**
     * 订单编号查询条件
     */
    public VerificationSheetQuery orderNumberLike(String orderNumber) {
        if (StringUtils.isNotEmpty(orderNumber)) {
            addCriterion(Restrictions.ilike("orderNumber", orderNumber, MatchMode.ANYWHERE));
        }
        return this;
    }
}
