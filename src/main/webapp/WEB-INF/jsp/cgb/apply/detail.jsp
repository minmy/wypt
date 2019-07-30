<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<%@ include file="/WEB-INF/jsp/common/quoteHead.jsp"%>
<link type="text/css" rel="stylesheet" href="${skin}/css/edit.css" />
<link type="text/css" rel="stylesheet" href="${skin}/css/list.css" />
<head>
<script type="text/javascript">
	function audit(id, applyNumber,state) {
		confirmAsk("确定操作吗？该操作不可逆转，请再次确认！", function() {
			$.ajax({
				url : "${path}/apply/audit.action?${_csrf.parameterName}=${_csrf.token}",
				type : "POST",
				data : {id : id, applyNumber : applyNumber,state : state},
				success : function(result) {
					if (result.status) {
						msgSuccess("操作成功", function() {
							location.href = "${path}/apply/list.action";
						});
					} else {
						alertError(result.message);
					}
				}
			});
		});
	}

	function returnUrl() {
		history.go(-1);
	}

	function orderDetail(orderNumber) {
		location.href = "${path}/apply/orderDetail.action?orderNumber="+orderNumber;
	}
</script>
</head>
<body class="login_body">
<div class="edit-box">
	<div class="edit-title">提现编号：${applyNumber} <span style="color: blue;font-weight:bold">(${handleState.desc})</span></div>
	<form class="edit-form" id="submitForm">
	<table>
				<tr>
					<td class="right" width="10%">提现用户</td>
					<td width="40%">${applyRealName}</td>
					<td class="right" width="10%">手续费</td>
					<td width="40%" style="color: red;font-weight:bold">${serviceCharge}% &nbsp; ￥${applyMoneyAmount - actualAmount}</td>
				</tr>
				<tr>
					<td class="right" width="10%">申请时间</td>
					<td width="40%">${createTime}</td>
					<td class="right" width="10%">申请人电话</td>
					<td width="40%">${applyPhone}</td>
				</tr>
				<tr>
					<td class="right" width="10%">申请总额</td>
					<td width="40%" style="color: blue;font-weight:bold">￥${applyMoneyAmount}</td>
					<td class="right" width="10%">提现总额</td>
					<td width="40%" style="color: blue;font-weight:bold">￥${actualAmount}</td>
				</tr>
				<tr>
					<td class="right" width="10%">Appid</td>
					<td width="40%">wx53b0d174ab62b643</td>
					<td class="right" width="10%">Openid</td>
					<td width="40%">${openId}</td>
				</tr>
	</table>
	<c:if test="${examineState == 'DSH' || examineState == 'SHZ' }">


	<div class="edit-box">
					<div class="edit-button">
						<button type="button" onclick="audit('${id}', '${applyNumber}','1')">通过</button>
						<button type="button" onclick="audit('${id}', '${applyNumber}','0')">退回</button>
						<button type="button" onclick="returnUrl()">返回</button>
					</div>
		</div>
		</c:if>
</div>

<c:forEach var="groupOrder" items="${groupOrders}">
	<div class="list-box">
		<div class="list-title">拼团主题：${groupOrder.theme} ,参团总金额：<span style="color: blue;font-weight:bold">￥${groupOrder.groupPrice}</span></div>

		<div class="list-message">

			<table>
				<thead>
					<th width="12%">订单编号</th>
					<th width="12%">拼团用户</th>
					<th width="12%">订单总价</th>
					<th width="12%">支付状态</th>
					<th width="12%">订单状态</th>
					<th width="5%">详情</th>
				</thead>
				<tbody>
					<c:forEach var="order" items="${groupOrder.orders}">
					<tr>
						<td class="center">${order.orderNumber}</td>
						<td class="center"><img heigth="30px" width="30px" alt="${order.weChatUser.name}" src="${order.weChatUser.headImgUrl}">${order.weChatUser.name} </td>
						<c:choose>
						    <c:when test="${order.payState == 'YZF'}">
						       	<td class="center" style="color: blue;font-weight:bold">￥${order.total}</td>
						       	<td class="center" style="color: blue;font-weight:bold">${order.payState.desc}</td>
						    </c:when>
						    <c:when test="${order.payState == 'YTK'}">
						     	<td class="center" style="color: red;">￥${order.total}</td>
						       	<td class="center" style="color: red;">${order.payState.desc}</td>
						    </c:when>
						    <c:otherwise>
						    	<td class="center" style="color: red;">￥${order.total}</td>
						        <td class="center">${order.payState.desc}</td>
						    </c:otherwise>
						</c:choose>
						<c:choose>
						    <c:when test="${order.orderState.orderStates == 'YWC'}">
						       	<td class="center" style="color: blue;font-weight:bold">${order.orderState.orderStates.desc}</td>
						    </c:when>
						    <c:when test="${order.orderState.orderStates == 'YQX'}">
						       	<td class="center" style="color: red;">${order.orderState.orderStates.desc}</td>
						    </c:when>
						    <c:otherwise>
						        <td class="center">${order.payState.desc}</td>
						    </c:otherwise>
						</c:choose>
						<td class="center">
							<a onclick="orderDetail('${order.orderNumber}')" style="cursor:pointer;">详情</a>
						</td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</c:forEach>

	</form>

</body>
</html>