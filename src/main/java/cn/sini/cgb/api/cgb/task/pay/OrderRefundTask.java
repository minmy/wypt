package cn.sini.cgb.api.cgb.task.pay;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.stereotype.Component;

import cn.sini.cgb.api.cgb.entity.group.GroupCommodity;
import cn.sini.cgb.api.cgb.entity.group.Order;
import cn.sini.cgb.api.cgb.entity.group.Order.PayState;
import cn.sini.cgb.api.cgb.entity.group.OrderGoods;
import cn.sini.cgb.api.cgb.entity.group.OrderState.OrderStates;
import cn.sini.cgb.api.cgb.query.group.GroupCommodityQuery;
import cn.sini.cgb.api.cgb.query.group.OrderQuery;
import cn.sini.cgb.common.task.AbstractTask;
import cn.sini.cgb.common.util.Environment;

/**
 * 订单退款定时任务
 * 
 * @author gaowei
 */
@Component
public class OrderRefundTask extends AbstractTask {

	@Override
	protected String taskName() {
		// 团长取消拼团，将已付款的用户订单原路退回
		return "恢复库存定时任务";
	}

	@Override
	protected boolean showStartEndLog() {
		return true;
	}

	@Override
	protected void execute() throws Exception {
		if (!"true".equals(Environment.getProperty("job.enable"))) {
			return;
		}
		List<Order> orders = new OrderQuery().payState(PayState.DTK).lockMode(LockMode.UPGRADE).list();
		for (Order order : orders) {
			OrderStates orderStates = order.getOrderState().getOrderStates();
			PayState payState = order.getPayState();
			if (orderStates == OrderStates.YQX && payState == PayState.DTK) {
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
			}
		}
	}

}
