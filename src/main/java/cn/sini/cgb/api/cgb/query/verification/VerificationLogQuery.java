package cn.sini.cgb.api.cgb.query.verification;

import cn.sini.cgb.common.util.CommonUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.verification.VerificationLog;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

/**
 * 核销日志查询类
 *
 * @author lijianxin
 */
public class VerificationLogQuery extends AbstractLogicalRemoveQuery {

    @Override
    protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
        return VerificationLog.class;
    }

    /**
     * 商户编号查询条件
     */
    public VerificationLogQuery mchId(String mchId) {
        if (StringUtils.isNotEmpty(mchId)) {
            addCriterion(Restrictions.eq("mchId", mchId));
        }
        return this;
    }

    /**
     * 商户编号模糊查询条件
     */
    public VerificationLogQuery mchIdLike(String mchId) {
        if (StringUtils.isNotEmpty(mchId)) {
            addCriterion(Restrictions.ilike("mchId", mchId, MatchMode.ANYWHERE));
        }
        return this;
    }

    /**
     * key模糊查询条件
     */
    public VerificationLogQuery keyLike(String key) {
        if (StringUtils.isNotEmpty(key)) {
            addCriterion(Restrictions.ilike("key", key, MatchMode.ANYWHERE));
        }
        return this;
    }

    /**
     * 状态码模糊查询条件
     */
    public VerificationLogQuery statusLike(Integer status) {
        if (null != status) {
            addCriterion(Restrictions.eq("status", status));
        }
        return this;
    }

    /**
     * 状态码模糊查询条件
     */
    public VerificationLogQuery apiType(VerificationLog.ApiType... apiType) {
        Object[] values = CommonUtils.removeEmptyElement(apiType);
        if (ArrayUtils.isNotEmpty(values)) {
            if (values.length == 1) {
                addCriterion(Restrictions.eq("apiType", values[0]));
            } else {
                addCriterion(Restrictions.in("apiType", values));
            }
        }
        return this;
    }
}
