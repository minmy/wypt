<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<%@ include file="/WEB-INF/jsp/common/quoteHead.jsp"%>
<link type="text/css" rel="stylesheet" href="${skin}/css/edit.css" />
<link type="text/css" rel="stylesheet" href="${skin}/css/list.css" />
<head>
</head>
<body class="login_body">
<div class="edit-box"><div class="edit-title">订单编号：${order.orderNumber} ，订单总价：￥${order.total}</div>
	<c:forEach var="orderGood" items="${orderGoods}">
		<form class="edit-form" id="submitForm">
			<table>
				<tr>
					<td class="right" width="10%">商品名称</td>
					<td width="40%">${orderGood.groupCommodity.name}</td>
					<td class="right" width="10%">商品售价</td>
					<td width="40%">￥${orderGood.groupCommodity.price}</td>
				</tr>
				<tr>
					<td class="right" width="10%">购买数量</td>
					<td width="40%">${orderGood.amount}</td>
					<td class="right" width="10%">合计</td>
					<td width="40%">￥${orderGood.total}</td>
				</tr>
			</table>
			
		</form>
	</c:forEach>
</div>

	<div class="list-box">
		<div class="list-title">支付记录</span></div>
		<div class="list-message">
			<table>
				<thead>
					<th width="12%">支付时间</th>
					<th width="12%">支付流水</th>
					<th width="12%">支付金额</th>
					<th width="12%">支付状态</th>
				</thead>
				<tbody>
					<c:forEach var="payBill" items="${payBills}">
					<tr>
						<td class="center">${payBill.createTime}</td>
						<td class="center">${payBill.transactionId}</td>
						<td class="center">${payBill.total_fee}</td>
						<c:choose>
						    <c:when test="${payBill.payStatus == 'YZF'}">
						       	<td class="center" style="color: blue;font-weight:bold">${payBill.payStatus.desc}</td>
						    </c:when>
						    <c:otherwise>
						    	<td class="center">${payBill.payStatus.desc}</td>
						    </c:otherwise>
						</c:choose>
					</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	
	<div class="list-box">
		<div class="list-title">退款记录</span></div>
		<div class="list-message">
			<table>
				<thead>
					<th width="12%">退款时间</th>
					<th width="12%">退款流水</th>
					<th width="12%">退款金额</th>
					<th width="12%">退款状态</th>
				</thead>
				<tbody>
					<c:forEach var="refundBill" items="${refundBills}">
					<tr>
						<td class="center">${refundBill.createTime}</td>
						<td class="center">${refundBill.transactionId}</td>
						<td class="center">${refundBill.refundFee}</td>
						<c:choose>
						    <c:when test="${refundBill.refundStatus == 'YTK'}">
						       	<td class="center" style="color: red;font-weight:bold">${refundBill.refundStatus.desc}</td>
						    </c:when>
						    <c:otherwise>
						    	<td class="center">${refundBill.refundStatus.desc}</td>
						    </c:otherwise>
						</c:choose>
					</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>

</body>
</html>