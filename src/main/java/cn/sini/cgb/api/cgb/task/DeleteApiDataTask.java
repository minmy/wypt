package cn.sini.cgb.api.cgb.task;

import java.util.Date;

import org.hibernate.Session;
import org.springframework.stereotype.Component;

import cn.sini.cgb.api.identity.entity.ApiAccessToken;
import cn.sini.cgb.api.identity.entity.ApiRequestLimit;
import cn.sini.cgb.common.task.AbstractTask;
import cn.sini.cgb.common.util.DateTimeUtils;
import cn.sini.cgb.common.util.Environment;

/**
 * 删除接口数据任务
 * 
 * @author 杨海彬
 */
@Component
public class DeleteApiDataTask extends AbstractTask {

	@Override
	protected String taskName() {
		return "删除接口数据任务";
	}

	@Override
	protected boolean showStartEndLog() {
		return false;
	}

	@Override
	protected void execute() throws Exception {
		Date currentTime = new Date();
		Session session = Environment.getSession();
		session.createSQLQuery("delete t from " + ApiAccessToken.TABLE_NAME + " t where t.EXPIRE_TIME < :expire_time").setTimestamp("expire_time", currentTime).executeUpdate();
		session.createSQLQuery("delete t from " + ApiRequestLimit.TABLE_NAME + " t where t.CREATE_TIME < :create_time").setTimestamp("create_time", DateTimeUtils.addMinute(currentTime, -10)).executeUpdate();
	}
}