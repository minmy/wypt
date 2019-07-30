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
	function toEdit(id) {
		location.href = "${path}/businessTerminal/edit.action?id=" + id;
	}
	function remove(id) {
		confirmAsk("您确定要删除该记录吗？", function() {
			$.ajax({
				url : "${path}/businessTerminal/remove.action?${_csrf.parameterName}=${_csrf.token}",
				type : "POST",
				data : {id : id},
				success : function(result) {
					if (result.status) {
						msgSuccess("删除成功", function() {
							location.reload();
						});
					} else {
						alertError(result.message);
					}
				}
			});
		});
	}
</script>
</head>
<body class="login_body">
	<div class="list-box">
		<div class="list-title">商家终端列表</div>
		<div class="list-search">
			<form action="${path}/businessTerminal/list.action" onsubmit="doLoading()">
				<input type="hidden" name="pageSize" value="${param.pageSize}" />
				终端ID <input type="text" name="posId" value="${param.posId}" maxlength="20" />
				所属商家 <select defaultValue="${param.businessId}" name="businessId">
					<option value="">全部</option>
					<c:forEach items="${businessList}" var="business">
						<option value="${business.id}">${business.name}</option>
					</c:forEach>
				</select>
				<button class="list-search-button" type="submit">搜索</button>
			</form>
		</div>
		<div class="list-message">
			<div class="list-button">
				<span class="add" onclick="toEdit('')"><i></i>新增</span>
				<!-- <span class="del" onclick="doDelete()"><i></i>删除</span> -->
			</div>
			<table>
				<thead>
					<tr>
						<th width="15%">ID</th>
						<th width="20%">创建时间</th>
						<th width="25%">终端ID</th>
						<th width="20%">所属商家</th>
						<th width="20%">操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="businessTerminal" items="${page.recordList}">
					<tr>
						<td class="center">${businessTerminal.id}</td>
						<td class="center">${u:formatDate(businessTerminal.createTime, 'yyyy-MM-dd HH:mm:ss')}</td>
						<td class="center">${businessTerminal.posId}</td>
						<td class="center">${businessTerminal.business.name}</td>
						<td class="center">
							<a onclick="toEdit('${businessTerminal.id}')" style="cursor:pointer;">编辑</a>
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