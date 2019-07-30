package cn.sini.cgb.common.wxpay;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.LockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.sini.cgb.api.cgb.action.group.ShareTicketAction;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder.GroupType;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState.States;
import cn.sini.cgb.api.cgb.entity.group.Order;
import cn.sini.cgb.api.cgb.entity.group.Order.PayMethod;
import cn.sini.cgb.api.cgb.entity.group.Order.PayState;
import cn.sini.cgb.api.cgb.entity.group.OrderState;
import cn.sini.cgb.api.cgb.entity.group.OrderState.OrderStates;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.api.cgb.entity.integral.IntegralAccountBill;
import cn.sini.cgb.api.cgb.entity.integral.IntegralAccountBill.IntegralSourceTypeEnum;
import cn.sini.cgb.api.cgb.entity.integral.IntegralAccountBill.IntegralTypeEnum;
import cn.sini.cgb.api.cgb.entity.pay.AllBill.BillTypeEnum;
import cn.sini.cgb.api.cgb.entity.pay.PayBill;
import cn.sini.cgb.api.cgb.entity.pay.PayBill.PayStatusEnum;
import cn.sini.cgb.api.cgb.entity.pay.RefundBill;
import cn.sini.cgb.api.cgb.entity.pay.RefundBill.RefundStatusEnum;
import cn.sini.cgb.api.cgb.query.group.OrderQuery;
import cn.sini.cgb.api.cgb.query.group.OrderStateQuery;
import cn.sini.cgb.api.cgb.query.group.WeChatUserQuery;
import cn.sini.cgb.api.cgb.query.integral.IntegralAccountBillQuery;
import cn.sini.cgb.api.cgb.query.pay.PayBillQuery;
import cn.sini.cgb.api.cgb.query.pay.RefundBillQuery;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.util.DateTimeUtils;
import cn.sini.cgb.common.util.Environment;
import cn.sini.cgb.common.util.IntegralUtil;
import cn.sini.cgb.common.util.PayUtil;

/**
 * 订单支付通知
 * 
 * @author 黎嘉权
 */
@Controller
@RequestMapping("/payaction")
public class Notify {
	private static final Logger LOGGER = LoggerFactory.getLogger(Notify.class);
	
	/** 微信支付结果通知 */
	@Transactional
	@RequestMapping(value = "/notify", method=RequestMethod.POST)
	public void payNotify(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		WXPay wxpay = new WXPay(new PayConfig());
		IntegralUtil integralUtil = new IntegralUtil();
		ShareTicketAction shareTicketAction = new ShareTicketAction();
		PayUtil payUtil = new PayUtil();
		try{
			 // 读取回调内容
			LOGGER.info("【支付結果】微信回調请求。。。。。。。。");
	        InputStream inputStream;
	        StringBuffer sb = new StringBuffer();
	        inputStream = request.getInputStream();
	        String s;
	        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
	        while ((s = in.readLine()) != null) {
	            sb.append(s);
	        }
	        in.close();
	        inputStream.close();
	        
	        // 支付结果通知的xml格式数据
	        String notifyData = sb.toString(); 
	        //LOGGER.info("【支付回调】"+notifyData);
	        // 转换成map
	        Map<String, String> notifyMap = WXPayUtil.xmlToMap(notifyData);
	
	        //验证签名
	        if (wxpay.isPayResultNotifySignatureValid(notifyMap)) {        // 签名正确
	        	//LOGGER.info("【支付結果】签名正确，开始处理");
	        	String out_trade_no = notifyMap.get("out_trade_no");
	        	String openId = notifyMap.get("openid").toString();
	        	Order order = payUtil.getOrder(openId, out_trade_no, true);
	        	Long groupId = order.getGroupOrder().getId();
	            if(order != null) {
	                if("SUCCESS".equals(notifyMap.get("result_code"))) {    //交易成功
	                    //更新订单
	                	//LOGGER.info("【支付結果】更新订单");
	                	String payNo = notifyMap.get("transaction_id").toString();
	    				Date payTime = DateTimeUtils.parse(notifyMap.get("time_end").toString(), "yyyyMMddHHmmss");
	    				BigDecimal finalPayment = new BigDecimal(String.valueOf(Integer.valueOf(notifyMap.get("total_fee")) / 100.0));
	    				OrderState orderState = new OrderStateQuery().orderStates(OrderStates.DSH).uniqueResult();
	    				//判断团单状态
	    				GroupOrder groupOrder = payUtil.getGroup(groupId, true);
	    				//判断团单状态是否为：进行中，最正常的状态
	    		        if(groupOrder.getGroupOrderState().getStates() == States.JXZ ){
	    		        	//进行中，最正常的状态
	    		        	if(order.getOrderState().getOrderStates() == OrderState.OrderStates.DFK){
	    		        		order.setOrderState(orderState);
	    		        		order.setPayState(PayState.YZF);
	    		        	}
	    		        	//被定时器取消，这个支付需要退款
	    		        	if(order.getOrderState().getOrderStates() == OrderState.OrderStates.YQX){
	    		        		order.setPayState(PayState.DTK);
	    		        	}
	    		        }
	    		        //判断团单状态是否为：未成团 或者 判断团单状态是否为：已结束
	    		        if(groupOrder.getGroupOrderState().getStates() == States.WCT || groupOrder.getGroupOrderState().getStates() == States.YJS ){
	    		        	order.setPayState(PayState.DTK);
	    		        } 
	    		        
	    		        order.setFinalPayment(finalPayment);
	                	order.setPayMethod(PayMethod.微信);
	                	order.setPayNumber(payNo);
	                	order.setPayTime(payTime);
	                	order.saveOrUpdate();
	                	//写入支付流水表
	                	PayBill payBill = new PayBillQuery().orderNumber(out_trade_no).openId(openId).payStatus(PayStatusEnum.YZF).lockMode(LockMode.UPGRADE).uniqueResult();
		   	       		 if(payBill == null){
		   	       			 payBill = new PayBill();
		   	       		 }
	                	payBill.setOpenId(openId);
	                	payBill.setPayStatus(PayStatusEnum.YZF);
	                	payBill.setOrderNumber(out_trade_no);
	                	payBill.setTransactionId(payNo);
	                	payBill.setTotal_fee(finalPayment);
	                	payBill.setTime_end(payTime);
	                	payBill.setPayXml(notifyData);
	                	payBill.saveOrUpdate();
	                	//团长虚拟账号操作
	                    WeChatUser weChatUser = order.getGroupOrder().getWeChatUser();
	                    payUtil.virtualAccountAction(weChatUser,BillTypeEnum.SR,order,finalPayment);  
		                //分享获取积分方法    		         		
	                    integralUtil.operIntegral(groupOrder, openId, order , IntegralSourceTypeEnum.FX , IntegralTypeEnum.ZJ , 0L);
	                    //分享券
	                    shareTicketAction.getShareTicketByBuy(openId, out_trade_no);
	                    LOGGER.info("【支付結果】订单：" + notifyMap.get("out_trade_no") + "，微信支付結果成功");
	                	
	                } else {    //交易失败
	                	LOGGER.info("【支付結果】失败订单：" + notifyMap.get("out_trade_no") + "，微信支付失败，"+notifyMap.get("return_msg").toString());
	                }
	            }
	            
	        }
	        else {  // 签名错误，如果数据里没有sign字段，也认为是签名错误
	            LOGGER.info("【支付結果】签名错误");
	        }
		}
		catch(Exception e){
			LOGGER.error("【支付結果】，内部错误",e);
			throw e;
		}
		finally{
			//发送通知
			//给微信服务器返回 成功标示 否则会一直询问 咱们服务器 是否回调成功
	        PrintWriter writer = response.getWriter();
	        //封装 返回值
	        StringBuffer buffer = new StringBuffer();
	        buffer.append("<xml>");
	        buffer.append("<return_code><![CDATA[SUCCESS]]></return_code>");
	        buffer.append("<return_msg><![CDATA[OK]]></return_msg>");
	        buffer.append("</xml>");
	        //返回
	        writer.print(buffer.toString());
		}
        
	}
	/** 微信退款结果通知 */
	@Transactional
	@RequestMapping(value = "/refundnotify", method=RequestMethod.POST)
	public void refundNotify(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		LOGGER.info("【退款结果】微信回调请求。。。。。。。。");
		Map<String,String> map;
		IntegralUtil integralUtil = new IntegralUtil();
		ShareTicketAction shareTicketAction = new ShareTicketAction();
		String outTradeNo = "";
		String openId = "";
        InputStream inputStream;
        StringBuffer sb = new StringBuffer();
        inputStream = request.getInputStream();
        String s;
        String notityXml = "";
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		try {
			
	        while ((s = in.readLine()) != null) {
	            sb.append(s);
	        }
	        in.close();
	        inputStream.close();
	        notityXml = sb.toString();
            //解析成Map
            map = WXPayUtil.xmlToMap(notityXml);
            //LOGGER.info("【退款回调】获得报文" + map.toString());
            //判断 退款是否成功
            if("SUCCESS".equals(map.get("return_code"))){
            	//LOGGER.info("【退款回调】return_code:" + map.get("return_code"));
            	//获得 返回的商户订单号
                String passMap = AESUtil.decryptData(map.get("req_info"));
                //拿到解密信息
                map = WXPayUtil.xmlToMap(passMap);
                
                //拿到解密后的订单号
                outTradeNo = map.get("out_trade_no");
                String success_time = map.get("success_time");
                String refund_id = map.get("refund_id");
                String out_refund_no = map.get("out_refund_no");
                String transaction_id = map.get("transaction_id");
                BigDecimal settlement_refund_fee = new BigDecimal(String.valueOf(Integer.valueOf(map.get("settlement_refund_fee")) / 100.0));
                BigDecimal refund_fee = new BigDecimal(String.valueOf(Integer.valueOf(map.get("refund_fee")) / 100.0));
                String refundXML = map.toString();
                Order order = new OrderQuery().orderNumber(outTradeNo).lockMode(LockMode.UPGRADE).uniqueResult();
                openId = order.getWeChatUser().getOpenId();
                RefundBill refundBill = new RefundBillQuery().openId(openId).orderNumber(outTradeNo).refundStatus(RefundStatusEnum.YTK).lockMode(LockMode.UPGRADE).uniqueResult();
				 if(refundBill == null){
					 refundBill = new RefundBill();
				 }
                refundBill.setRealRefundTime(DateTimeUtils.parse(success_time, "yyyy-MM-dd HH:mm:ss"));
                refundBill.setOrderNumber(outTradeNo);
                refundBill.setTransactionId(transaction_id);
                refundBill.setOutRefundNo(out_refund_no);
                refundBill.setRefundNo(refund_id);
                refundBill.setRefundFee(refund_fee);
                refundBill.setSettlementRefundFee(settlement_refund_fee);
                if(map.get("refund_status").equals("SUCCESS")){
                	refundBill.setRefundStatus(RefundStatusEnum.YTK);
                	order.setPayState(PayState.YTK);
                }else{
                	refundBill.setRefundStatus(RefundStatusEnum.TKSB);
                	order.setPayState(PayState.TKSB);
                	LOGGER.info("【退款结果】订单失败，"+outTradeNo+"，状态："+map.get("refund_status"));
                }
                refundBill.setRefundXml(refundXML);
                refundBill.saveOrUpdate();
                order.saveOrUpdate();
                //减少积分
                integralUtil.operIntegral(order.getGroupOrder(),  openId,  order, IntegralSourceTypeEnum.TK , IntegralTypeEnum.JS , 0L);
                //减少分享卷
                shareTicketAction.refund(openId, outTradeNo);
                LOGGER.info("【退款结果】订单成功："+outTradeNo);
            }else {
                //获得 返回的商户订单号
                String passMap = AESUtil.decryptData(map.get("req_info"));
                //拿到解密信息
                map = WXPayUtil.xmlToMap(passMap);
                LOGGER.info("【退款结果】订单失败："+map.get("out_trade_no")+"："+map.get("return_code"));
            }
            
        } catch (Exception e) {
        	LOGGER.error("【退款结果】订单失败："+outTradeNo+"，内部错误",e);
        	throw e;
        }finally{
        	//给微信服务器返回 成功标示 否则会一直询问 咱们服务器 是否回调成功
            PrintWriter writer = response.getWriter();
            //封装 返回值
            StringBuffer buffer = new StringBuffer();
            buffer.append("<xml>");
            buffer.append("<return_code><![CDATA[SUCCESS]]></return_code>");
            buffer.append("<return_msg><![CDATA[OK]]></return_msg>");
            buffer.append("</xml>");
            //返回
            writer.print(buffer.toString());
        }
	}
}
