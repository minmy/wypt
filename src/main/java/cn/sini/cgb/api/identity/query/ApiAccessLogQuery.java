package cn.sini.cgb.api.identity.query;

import cn.sini.cgb.api.identity.entity.ApiAccessLog;
import cn.sini.cgb.common.entity.AbstractEntity;
import cn.sini.cgb.common.query.AbstractQuery;

/**
 * 接口访问日志查询类
 * 
 * @author 杨海彬
 */
public class ApiAccessLogQuery extends AbstractQuery {

	@Override
	protected Class<? extends AbstractEntity> queryEntity() {
		return ApiAccessLog.class;
	}
}