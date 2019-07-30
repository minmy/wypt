package cn.sini.cgb.admin.identity.query.address;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.admin.identity.entity.address.TaAdministrativeDivision;
import cn.sini.cgb.admin.identity.entity.address.TaAdministrativeDivision.DivisionEnum;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

/**
 * 行政区划查询类
 * @author xuxiaoyu
 */
public class TaAdministrativeDivisionQuery extends AbstractLogicalRemoveQuery {

    @Override
    protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
        return TaAdministrativeDivision.class;
    }

    /**
     * 添加行政区划编码查询条件
     * @param code
     * @return
     */
    public TaAdministrativeDivisionQuery code(String code) {
        if (StringUtils.isNotBlank(code)) {
            addCriterion(Restrictions.eq("code", code));
        }
        return this;
    }

    /**
     * 添加行政区划名称查询条件
     * @param name
     * @return
     */
    public TaAdministrativeDivisionQuery nameLike(String name) {
        if (StringUtils.isNotBlank(name)) {
            addCriterion(Restrictions.ilike("name", name, MatchMode.ANYWHERE));
        }
        return this;
    }

    /**
     * 添加行政区划全称查询条件
     * @param fullName
     * @return
     */
    public TaAdministrativeDivisionQuery fullNameLike(String fullName) {
        if (StringUtils.isNotBlank(fullName)) {
            addCriterion(Restrictions.ilike("fullName", fullName, MatchMode.ANYWHERE));
        }
        return this;
    }

    /**
     * 添加行政区划类别查询条件
     * @param divisionEnum
     * @return
     */
    public TaAdministrativeDivisionQuery divisionType(DivisionEnum divisionEnum) {
        if (divisionEnum != null) {
            addCriterion(Restrictions.eq("divisionType", divisionEnum));
        }
        return this;
    }

    /**
     * 添加行政区划是否逻辑删除查询条件
     * @param flag
     * @return
     */
    /*public TaAdministrativeDivisionQuery remove(Boolean flag) {
        if (flag != null) {
            addCriterion(Restrictions.eq("remove", flag));
        }
        return this;
    }*/
}
