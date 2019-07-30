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
		location.href = "${path}/business/edit.action?id=" + id;
	}
	function remove(id) {
		confirmAsk("您确定要删除该记录吗？", function() {
			$.ajax({
				url : "${path}/business/remove.action?${_csrf.parameterName}=${_csrf.token}",
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
		<div class="list-title">商家列表</div>
		<div class="list-search">
			<form action="${path}/business/list.action" onsubmit="doLoading()">
				<input type="hidden" name="pageSize" value="${param.pageSize}" />
				商家名称 <input type="text" name="name" value="${param.name}" maxlength="20" />
				商户编号 <input type="text" name="mchId" value="${param.mchId}" maxlength="20" />
				商家类型 <select defaultValue="${param.businessType}" name="businessType">
					<option value="">全部</option>
					<c:forEach items="${businessType}" var="businessType">
						<option value="${businessType}">${businessType.desc}</option>
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
						<th width="10%">ID</th>
						<th width="20%">创建时间</th>
						<th width="20%">商户编号</th>
						<th width="20%">商家名称</th>
						<th width="20%">商家类型</th>
						<th width="10%">操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="business" items="${page.recordList}">
					<tr>
						<td class="center">${business.id}</td>
						<td class="center">${u:formatDate(business.createTime, 'yyyy-MM-dd HH:mm:ss')}</td>
						<td class="center">${business.mchId}</td>
						<td class="center">${business.name}</td>
						<td class="center">${business.businessType.desc}</td>
						<td class="center">
							<a onclick="toEdit('${business.id}')" style="cursor:pointer;">编辑</a>
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