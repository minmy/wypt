package cn.sini.cgb.api.cgb.action.pay;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.api.cgb.entity.group.Order;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.api.cgb.entity.pay.AllBill;
import cn.sini.cgb.api.cgb.entity.pay.AllBill.BillTypeEnum;
import cn.sini.cgb.api.cgb.entity.pay.AllBill.CashTypeEnum;
import cn.sini.cgb.api.cgb.entity.pay.VirtualAccount;
import cn.sini.cgb.api.cgb.query.group.GroupOrderQuery;
import cn.sini.cgb.api.cgb.query.group.OrderQuery;
import cn.sini.cgb.api.cgb.query.group.WeChatUserQuery;
import cn.sini.cgb.api.cgb.query.pay.AllBillQuery;
import cn.sini.cgb.api.cgb.query.pay.VirtualAccountQuery;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.json.JsonUtils;

/**
 * 虚拟账户相关action
 * 
 * @author 黎嘉权
 */
@Controller
@RequestMapping("/api/vaccount")
public class VirtualAccountAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(VirtualAccountAction.class);
	
	/*
	 * 获取个人虚拟账户数据
	 */
	@RequestMapping(value = "/home", method = RequestMethod.POST)
	public void home(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		ObjectNode objectNode = JsonUtils.createObjectNode();
		String openId = request.getString("openId");
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		VirtualAccount virtualAccount = new VirtualAccountQuery().openId(openId).uniqueResult();
		if(virtualAccount==null){
			response.outputJson(70001, "找不到用户");
		}else{
			objectNode.put("grossIncome", virtualAccount.getGrossIncome());
			objectNode.put("noWithdrawn", virtualAccount.getNoWithdrawn());
			objectNode.put("withdrawableCash", virtualAccount.getWithdrawableCash());
			objectNode.put("alreadyAvailable", virtualAccount.getAlreadyAvailable());
			response.outputJson(0, objectNode);
		}
	}
	
	/*
	 * 获取可提现和待提现的订单
	 */
	@RequestMapping(value = "/details", method = RequestMethod.POST)
	public void withdrawableCash(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		ObjectNode node = null;
		ObjectNode objectNode = null;
		ArrayNode arrayNode = null;
		ArrayNode arrayGroup = JsonUtils.createArrayNode();
		String groupOrderName = "";
		String groupid = "";
		BigDecimal group_fee = null;
		BigDecimal all_fee = new BigDecimal("0");
		String total_fee = "";
		String orderNumner = "";
		String name = "";
		String billType = "";
		String openId = request.getString("openId");
		String type = request.getString("type");
		List<AllBill> allbillList;
		if (StringUtils.isEmpty(openId)) {
			response.outputJson(-1, "缺少openId参数");
			return;
		}
		if (StringUtils.isEmpty(type)) {
			response.outputJson(-1, "缺少type参数");
			return;
		}
		WeChatUser weChatUser = new WeChatUserQuery().openId(openId).uniqueResult();
		VirtualAccount virtualAccount = new VirtualAccountQuery().openId(openId).uniqueResult();
		if(weChatUser == null){
			response.outputJson(70001, "找不到用户");
		}else {
			if(StringUtils.isNotEmpty(openId) && type.equals("DTX")){
				allbillList = new AllBillQuery().weChatUser(weChatUser).flag(CashTypeEnum.DTX).list();
				all_fee = virtualAccount.getWithdrawableCash();
			}else if(StringUtils.isNotEmpty(openId) && type.equals("KTX")){
				allbillList = new AllBillQuery().weChatUser(weChatUser).flag(CashTypeEnum.KTX).list();
			}else if(StringUtils.isNotEmpty(openId) && type.equals("YTX")){
				allbillList = new AllBillQuery().weChatUser(weChatUser).flag(CashTypeEnum.YTX).list();
			}else {
				allbillList = new AllBillQuery().weChatUser(weChatUser).flag(CashTypeEnum.ZSR).list();
			}
			Set<AllBill> allBills = new HashSet<AllBill>();
			allBills.addAll(allbillList);
			Map<String, List<AllBill>> groupMap = allBills.stream().collect(Collectors.groupingBy(AllBill::getGroupId));
			for (Map.Entry<String, List<AllBill>> entry : groupMap.entrySet()) { 
				group_fee = new BigDecimal("0");
				arrayNode = JsonUtils.createArrayNode();
				node = JsonUtils.createObjectNode();
				groupid = entry.getKey();
				GroupOrder groupOrder = new GroupOrderQuery().id(Long.parseLong(groupid)).uniqueResult();
				groupOrderName = groupOrder.getTheme();
				node.put("groupid", groupid);
				node.put("groupOrderName", groupOrderName);
				for (AllBill allBill : entry.getValue()) {
					objectNode = JsonUtils.createObjectNode();
					orderNumner = allBill.getOrderNumber();
					Order order = new OrderQuery().orderNumber(orderNumner).uniqueResult();
					name = order.getWeChatUser().getName();
					total_fee = order.getFinalPayment().toString();
					billType = allBill.getBillType().toString();
					if(allBill.getBillType() == BillTypeEnum.SR){
						group_fee = group_fee.add(order.getFinalPayment());
					}else{
						group_fee = group_fee.subtract(order.getFinalPayment());
					}
					objectNode.put("orderNumner", orderNumner);
					objectNode.put("name", name);
					objectNode.put("total_fee", total_fee);
					objectNode.put("billType", billType);
					arrayNode.add(objectNode);
				}
				node.put("all_fee", all_fee);
				node.put("group_fee", group_fee);
				arrayGroup.add(node);
				node.set("orderList", arrayNode);
				
			}
			response.outputJson(0, arrayGroup);
		}
	}
}
