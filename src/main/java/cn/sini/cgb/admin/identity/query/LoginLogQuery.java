package cn.sini.cgb.admin.identity.query;

import cn.sini.cgb.admin.identity.entity.LoginLog;
import cn.sini.cgb.common.entity.AbstractEntity;
import cn.sini.cgb.common.query.AbstractQuery;

/**
 * 登录日志查询类
 * 
 * @author 杨海彬
 */
public class LoginLogQuery extends AbstractQuery {

	@Override
	protected Class<? extends AbstractEntity> queryEntity() {
		return LoginLog.class;
	}
}