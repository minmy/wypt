package cn.sini.cgb.api.cgb.task.pay;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.LockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState.States;
import cn.sini.cgb.api.cgb.entity.pay.AllBill;
import cn.sini.cgb.api.cgb.entity.pay.AllBill.BillTypeEnum;
import cn.sini.cgb.api.cgb.entity.pay.AllBill.CashTypeEnum;
import cn.sini.cgb.api.cgb.entity.pay.VirtualAccount;
import cn.sini.cgb.api.cgb.entity.pay.VirtualAccountBill;
import cn.sini.cgb.api.cgb.entity.pay.VirtualAccountBill.BillType;
import cn.sini.cgb.api.cgb.query.group.GroupOrderQuery;
import cn.sini.cgb.api.cgb.query.group.GroupOrderStateQuery;
import cn.sini.cgb.api.cgb.query.pay.AllBillQuery;
import cn.sini.cgb.api.cgb.query.pay.VirtualAccountQuery;
import cn.sini.cgb.common.task.AbstractTask;
import cn.sini.cgb.common.util.DateTimeUtils;
import cn.sini.cgb.common.util.Environment;

@Component
public class CashWithdrawalTask extends AbstractTask{
	private static final Logger LOGGER = LoggerFactory.getLogger(CashWithdrawalTask.class);

	@Override
	protected String taskName() {
		return "发货提现T+N任务";
	}

	@Override
	protected void execute() throws Exception {
		if (!"true".equals(Environment.getProperty("job.enable"))) {
			return;
		}
		String groupId = "";
		String openId = "";
		
		Date date = new Date();	
		
		//将符合提现的团单下的所有账，转移到可提现
		GroupOrderState groupOrderState = new GroupOrderStateQuery().states(States.YJS).uniqueResult();
		List<GroupOrder> groupOrders = new GroupOrderQuery().groupOrderState(groupOrderState).isFinish(false).cashWithdrawalTimeLe(date).lockMode(LockMode.UPGRADE).list();
		for (GroupOrder groupOrder : groupOrders) {
			BigDecimal total_fee = new BigDecimal("0");
			String changeNumber = "BG"+DateTimeUtils.format(new Date(), "yyyyMMddHHmmsss") + (int) ((Math.random() * 9 + 1) * 1000);
			groupId = groupOrder.getId().toString();
			openId = groupOrder.getWeChatUser().getOpenId();
			List<AllBill> allBills = new AllBillQuery().groupId(groupId).flag(CashTypeEnum.DTX).list();
			for (AllBill allBill : allBills) {
				allBill.setFlag(CashTypeEnum.KTX);
				allBill.setChangeNumber(changeNumber);
				if(allBill.getBillType() == BillTypeEnum.SR){
					total_fee = total_fee.add(allBill.getTotal_fee());
				}else{
					total_fee = total_fee.subtract(allBill.getTotal_fee());
				}
				allBill.saveOrUpdate();
			}
			//开始转移
			VirtualAccount virtualAccount = new VirtualAccountQuery().openId(openId).lockMode(LockMode.UPGRADE).uniqueResult();
			if (virtualAccount == null) {
				LOGGER.info("【发货提现】找不到用户的虚拟账户："+openId);
			}else{
				BigDecimal beforeAlreadyAvailable = virtualAccount.getAlreadyAvailable();
				BigDecimal beforeGrossIncome = virtualAccount.getGrossIncome();
				BigDecimal beforeNoWithdrawn = virtualAccount.getNoWithdrawn();
				BigDecimal beforeWithdrawableCash = virtualAccount.getWithdrawableCash();
				BigDecimal noWithdrawn = virtualAccount.getNoWithdrawn();//不可提减少
				BigDecimal withdrawableCash = virtualAccount.getWithdrawableCash();//可提增加
				virtualAccount.setNoWithdrawn(noWithdrawn.subtract(total_fee));
				virtualAccount.setWithdrawableCash(withdrawableCash.add(total_fee));
				virtualAccount.saveOrUpdate();
				//写入虚拟账户留水
				VirtualAccountBill virtualAccountBill = new VirtualAccountBill();
				virtualAccountBill.setAmountMoney(total_fee);
				virtualAccountBill.setBeforeAlreadyAvailable(beforeAlreadyAvailable);
				virtualAccountBill.setBeforeGrossIncome(beforeGrossIncome);
				virtualAccountBill.setBeforeNoWithdrawn(beforeNoWithdrawn);
				virtualAccountBill.setBeforeWithdrawableCash(beforeWithdrawableCash);
				virtualAccountBill.setBillType(BillType.BG);
				virtualAccountBill.setOpenId(openId);
				virtualAccountBill.setChangeNumber(changeNumber);
				virtualAccountBill.saveOrUpdate();
				groupOrder.setIsFinish(true);
				groupOrder.saveOrUpdate();
				LOGGER.info("【发货提现】虚拟账户操作提现："+openId+","+noWithdrawn+","+withdrawableCash);
			}
		}
	}

}
