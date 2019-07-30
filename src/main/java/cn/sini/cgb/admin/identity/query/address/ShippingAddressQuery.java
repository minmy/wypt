package cn.sini.cgb.admin.identity.query.address;

import cn.sini.cgb.admin.identity.entity.address.ShippingAddress;
import cn.sini.cgb.api.cgb.query.group.CommunityQuery;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

/**
 * 配送地址查询类
 * @author  xuxiaoyu
 *
 */
public class ShippingAddressQuery extends AbstractLogicalRemoveQuery {

    @Override
    protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
        return ShippingAddress.class;
    }

    /**
     * 添加所属小区查询条件
     * @param communityQuery
     * @return
     */
    public ShippingAddressQuery communityQuery(CommunityQuery communityQuery) {
        if (communityQuery != null) {
            addCriteria("community", communityQuery);
        }
        return this;
    }
}
