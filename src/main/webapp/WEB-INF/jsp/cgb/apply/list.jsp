<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<%@ include file="/WEB-INF/jsp/common/quoteHead.jsp"%>
<link type="text/css" rel="stylesheet" href="${skin}/css/list.css" />
<script type="text/javascript">
	$(document).ready(function() {
		$("#selectAll").click(function() {
			$("input[name='ids']").prop("checked", $(this).is(":checked"));
		});
		$("input[name='ids']").click(function () {
			$("#selectAll").prop("checked", $("input[name='ids']:checked").length == $("input[name='ids']").length);
		});
	});
	
	function moneyTransfer(id) {
		location.href = "${path}/apply/detail.action?id=" + id;
		
	}
</script>
</head>
<body class="login_body">
	<div class="list-box">
		<div class="list-title">提现申请列表</div>
		<div class="list-search">
			<form action="${path}/apply/list.action" onsubmit="doLoading()">
				<input type="hidden" name="pageSize" value="${param.pageSize}" />
				申请编号 <input type="text" name="applyNumber" value="${param.applyNumber}" />
				申请人姓名 <input type="text" name="applyName" value="${param.applyName}" maxlength="20" />
				申请人电话 <input type="text" name="applyPhone" value="${param.applyPhone}" maxlength="11" />
				申请人微信 <input type="text" name="applyWeChat" value="${param.applyWeChat}" />
				<br>
				提现状态 <select defaultValue="${param.handleState}" name="handleState">
					<option value="">全部</option>
					<c:forEach items="${handleStates}" var="handleState">
						<option value="${handleState}">${handleState.desc}</option>
					</c:forEach>
				</select>
				审核状态 <select defaultValue="${param.examineState}" name="examineState">
					<option value="">全部</option>
					<c:forEach items="${examineStates}" var="examineState">
						<option value="${examineState}">${examineState.desc}</option>
					</c:forEach>
				</select>
				<button class="list-search-button" type="submit">搜索</button>
			</form>
		</div>
		<div class="list-message">
			<div class="list-button">
			</div>
			<table>
				<thead>
					<tr>
						<th width="12%">申请编号</th>
						<th width="12%">申请时间</th>
						<th width="8%">申请人姓名</th>
						<th width="10%">申请人电话</th>
						<th width="10%">申请人微信</th>
						<th width="8%">申请金额</th>
						<th width="8%">实际金额</th>
						<th width="5%">提现状态</th>
						<th width="5%">审核状态</th>
						<th width="5%">提现结果</th>
						<th width="5%">操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="apply" items="${page.recordList}">
					<tr>
						<td class="center">${apply.applyNumber}</td>
						<td class="center">${apply.createTime}</td>
						<td class="center">${apply.weChatUser.applyRealName}</td>
						<td class="center">${apply.weChatUser.applyPhone}</td>
						<td class="center">${apply.weChatUser.applyWeChat}</td>
						<td class="center">￥${apply.applyMoneyAmount}</td>
						<td class="center">￥${apply.actualAmount}</td>
						<c:choose>
						    <c:when test="${apply.handleState == 'DTX'}">
						       	<td class="center" style="color: blue;font-weight:bold">${apply.handleState.desc}</td>
						    </c:when>
						    <c:when test="${apply.handleState == 'YTX'}">
						       	<td class="center" style="color: green;font-weight:bold">${apply.handleState.desc}</td>
						    </c:when>
						    <c:when test="${apply.handleState == 'TXSB'}">
						       	<td class="center" style="color: red;font-weight:bold">${apply.handleState.desc}</td>
						    </c:when>
						    <c:otherwise>
						        <td class="center">${apply.handleState.desc}</td>
						    </c:otherwise>
						</c:choose>
						
						<c:choose>
						<c:when test="${apply.examineState == 'YTG'}">
						       	<td class="center" style="color: green;font-weight:bold">${apply.examineState.desc}</td>
						    </c:when>
						    <c:when test="${apply.examineState == 'DSH'}">
						       	<td class="center" style="color: blue;font-weight:bold">${apply.examineState.desc}</td>
						    </c:when>
						     <c:when test="${apply.examineState == 'BTG'}">
						       	<td class="center" style="color: red;font-weight:bold">${apply.examineState.desc}</td>
						    </c:when>
						    <c:otherwise>
						        <td class="center">${apply.examineState.desc}</td>
						    </c:otherwise>
						</c:choose>
						
							<td class="center">${apply.transResults}</td>
						
						<td class="center">
							<a onclick="moneyTransfer('${apply.id}')" style="cursor:pointer;">查看审核</a>
						</td> 
					</tr>
					</c:forEach>
				</tbody>
			</table>
			<%@ include file="/WEB-INF/jsp/common/quotePageRefresh.jsp"%>
		</div>
	</div>
</body>
</html>