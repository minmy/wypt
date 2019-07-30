package cn.sini.cgb.api.cgb.task.enterprise;

import java.util.Date;
import java.util.List;

import org.hibernate.LockMode;
import org.springframework.stereotype.Component;

import cn.sini.cgb.api.cgb.entity.group.GroupOrder.GroupType;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState.States;
import cn.sini.cgb.api.cgb.entity.group.Order;
import cn.sini.cgb.api.cgb.entity.group.Order.PayState;
import cn.sini.cgb.api.cgb.entity.group.OrderState.OrderStates;
import cn.sini.cgb.api.cgb.query.group.GroupOrderQuery;
import cn.sini.cgb.api.cgb.query.group.GroupOrderStateQuery;
import cn.sini.cgb.api.cgb.query.group.OrderQuery;
import cn.sini.cgb.api.cgb.query.group.OrderStateQuery;
import cn.sini.cgb.common.task.AbstractTask;
import cn.sini.cgb.common.util.Environment;

/**
 * 企业订单自动结束任务
 * 
 * @author gaowei
 */
@Component
public class EnterpriseOrderAutoFinishTask extends AbstractTask {

	@Override
	protected String taskName() {
		return "企业订单自动结束任务";
	}

	@Override
	protected boolean showStartEndLog() {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void execute() throws Exception {
		if (!"true".equals(Environment.getProperty("job.enable"))) {
			return;
		}
		// 查询进行中状态的团单
		GroupOrderStateQuery groupOrderStateQuery = new GroupOrderStateQuery().states(States.JXZ);
		// 查询类型为企业团的团单，且当前时间大于团单自提结束时间的团单
		GroupOrderQuery groupOrderQuery = new GroupOrderQuery().groupType(GroupType.QYT).groupOrderStateQuery(groupOrderStateQuery).selfExtractingEndTimeLt(new Date());
		// 待收货(待发货)的订单状态
		OrderStateQuery orderStateQuery = new OrderStateQuery().orderStates(OrderStates.DSH);
		List<Order> orderList = new OrderQuery().groupOrderQuery(groupOrderQuery).orderStateQuery(orderStateQuery).payState(PayState.YZF).lockMode(LockMode.UPGRADE).list();
		for (Order order : orderList) {
			order.setOrderState(new OrderStateQuery().orderStates(OrderStates.YWC).uniqueResult());
			order.setReceivingTime(new Date());
			order.saveOrUpdate();
		}
	}
}
