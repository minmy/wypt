package cn.sini.cgb.api.cgb.task.group;

import java.util.Date;
import java.util.List;

import org.hibernate.LockMode;
import org.springframework.stereotype.Component;

import cn.sini.cgb.api.cgb.entity.group.GroupCommodity;
import cn.sini.cgb.api.cgb.entity.group.Order;
import cn.sini.cgb.api.cgb.entity.group.Order.PayState;
import cn.sini.cgb.api.cgb.entity.group.OrderGoods;
import cn.sini.cgb.api.cgb.entity.group.OrderState;
import cn.sini.cgb.api.cgb.entity.group.OrderState.OrderStates;
import cn.sini.cgb.api.cgb.query.group.GroupCommodityQuery;
import cn.sini.cgb.api.cgb.query.group.OrderQuery;
import cn.sini.cgb.api.cgb.query.group.OrderStateQuery;
import cn.sini.cgb.common.task.AbstractTask;
import cn.sini.cgb.common.util.DateTimeUtils;
import cn.sini.cgb.common.util.Environment;

/**
 * 待付款订单自动取消定时任务
 * 
 * @author gaowei
 */
@Component
public class OrderCancelTask extends AbstractTask {

	@Override
	protected String taskName() {
		return "待付款订单自动取消定时任务";
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
		// 把待付款且待支付的订单变更为已取消状态，锁行
		OrderState orderState = new OrderStateQuery().orderStates(OrderStates.DFK).uniqueResult();
		Date date = new Date();
		Date time = DateTimeUtils.addMinute(date, -10);
		List<Order> orderList = new OrderQuery().orderState(orderState).payState(PayState.DZF).createTimeLe(time).lockMode(LockMode.UPGRADE).list();
		for (Order order : orderList) {
			// 取消订单
			order.setOrderState(new OrderStateQuery().orderStates(OrderStates.YQX).uniqueResult());
			order.setCancelTime(date);
			order.setCancelReason("订单有效支付时间已过");
			// 恢复单数和库存
			if (!order.getIsRecovery()) {
				for (OrderGoods orderGoods : order.getOrderGoods()) {
					Integer amount = orderGoods.getAmount();
					//GroupCommodity groupCommodity = orderGoods.getGroupCommodity();
					GroupCommodity groupCommodity = new GroupCommodityQuery().id(orderGoods.getGroupCommodity().getId()).lockMode(LockMode.UPGRADE).uniqueResult();
					Integer remnantInventory = groupCommodity.getRemnantInventory() + amount;
					groupCommodity.setRemnantInventory(remnantInventory);
					groupCommodity.saveOrUpdate();

				}
				order.setIsRecovery(true);
			}
			order.saveOrUpdate();
		}
	}
}
