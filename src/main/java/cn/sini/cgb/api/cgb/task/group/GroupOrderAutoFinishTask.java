package cn.sini.cgb.api.cgb.task.group;

import java.util.Date;
import java.util.List;

import org.hibernate.LockMode;
import org.springframework.stereotype.Component;

import cn.sini.cgb.api.cgb.entity.group.GroupCommodity;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState.States;
import cn.sini.cgb.api.cgb.entity.group.Order;
import cn.sini.cgb.api.cgb.entity.group.Order.PayState;
import cn.sini.cgb.api.cgb.entity.group.OrderGoods;
import cn.sini.cgb.api.cgb.entity.group.OrderState.OrderStates;
import cn.sini.cgb.api.cgb.entity.pay.AllBill;
import cn.sini.cgb.api.cgb.entity.pay.AllBill.CashTypeEnum;
import cn.sini.cgb.api.cgb.query.group.GroupCommodityQuery;
import cn.sini.cgb.api.cgb.query.group.GroupOrderQuery;
import cn.sini.cgb.api.cgb.query.group.GroupOrderStateQuery;
import cn.sini.cgb.api.cgb.query.group.OrderQuery;
import cn.sini.cgb.api.cgb.query.group.OrderStateQuery;
import cn.sini.cgb.api.cgb.query.pay.AllBillQuery;
import cn.sini.cgb.common.task.AbstractTask;
import cn.sini.cgb.common.util.Environment;

/**
 * 团单自动结束定时任务（包括普通团和企业团）
 * 
 * @author 黎嘉权
 */
@Component
public class GroupOrderAutoFinishTask extends AbstractTask {

	@Override
	protected String taskName() {
		return "团单自动结束定时任务";
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
		GroupOrderState groupOrderState = new GroupOrderStateQuery().states(States.JXZ).uniqueResult();
		Date date = new Date();
		List<GroupOrder> groupOrderList = new GroupOrderQuery().groupOrderState(groupOrderState).endTimeLe(date)
				.lockMode(LockMode.UPGRADE).list();
		for (GroupOrder groupOrder : groupOrderList) {
			// 1.团单到达结束时间且无人付款，则团单未成团。
			Integer payOrders = groupOrder.getPayOrders();
			if (payOrders <= 0) {
				groupOrder.setGroupOrderState(new GroupOrderStateQuery().states(States.WCT).uniqueResult());
				groupOrder.setCancelTime(date);
				groupOrder.setCancelReason("拼团时间已结束，未成团");
				groupOrder.saveOrUpdate();
				// 取消allbill里面的支付记录
				List<AllBill> allBills = new AllBillQuery().groupId(groupOrder.getId().toString()).lockMode(LockMode.UPGRADE).list();
				for (AllBill allBill : allBills) {
					allBill.setFlag(CashTypeEnum.QX);
					allBill.saveOrUpdate();
				}
				// 取消掉未付款的订单
				for (Order order : groupOrder.getOrders()) {
					// 锁行
					order = new OrderQuery().id(order.getId()).lockMode(LockMode.UPGRADE).uniqueResult();
					OrderStates orderStates = order.getOrderState().getOrderStates();
					PayState payState = order.getPayState();
					if (orderStates == OrderStates.DFK) {
						if (payState == PayState.DZF || payState == PayState.ZFZ) {
							order.setOrderState(new OrderStateQuery().orderStates(OrderStates.YQX).uniqueResult());
							order.setCancelTime(date);
							order.setCancelReason("拼团时间已结束，未成团");
							// 恢复单数和库存
							if (!order.getIsRecovery()) {
								for (OrderGoods orderGoods : order.getOrderGoods()) {
									Integer amount = orderGoods.getAmount();
									// GroupCommodity groupCommodity =
									// orderGoods.getGroupCommodity();
									GroupCommodity groupCommodity = new GroupCommodityQuery()
											.id(orderGoods.getGroupCommodity().getId()).lockMode(LockMode.UPGRADE)
											.uniqueResult();
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
			}
		}
	}
}
