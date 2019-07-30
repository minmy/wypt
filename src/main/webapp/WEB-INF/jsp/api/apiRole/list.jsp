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
		location.href = "${path}/apiRole/edit.action?id=" + id;
	}
	function doDelete() {
		if ($("input[name='ids']:checked").length == 0) {
			alertInfo("请选择要删除的记录");
			return;
		}
		confirmAsk("您确定要删除选中的记录吗？", function() {
			$.ajax({
				url : "${path}/apiRole/delete.action?${_csrf.parameterName}=${_csrf.token}",
				type : "POST",
				data : $("input[name='ids']:checked").serialize(),
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
		<div class="list-title">接口角色管理</div>
		<div class="list-search">
			<form action="${path}/apiRole/list.action" onsubmit="doLoading()">
				<input type="hidden" name="pageSize" value="${param.pageSize}" />
				名称 <input type="text" name="name" value="${param.name}" maxlength="20" />
				<button class="list-search-button" type="submit">搜索</button>
			</form>
		</div>
		<div class="list-message">
			<div class="list-button">
				<span class="add" onclick="toEdit('')"><i></i>新增</span>
				<span class="del" onclick="doDelete()"><i></i>删除</span>
			</div>
			<table>
				<thead>
					<tr>
						<th width="5%"><input type="checkbox" id="selectAll" /></th>
						<th width="15%">名称</th>
						<th width="70%">可访问资源</th>
						<th width="10%">操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="apiRole" items="${page.recordList}">
					<tr>
						<td class="center"><input type="checkbox" name="ids" value="${apiRole.id}" /></td>
						<td class="center">${apiRole.name}</td>
						<td>
						<c:forEach var="apiResource" items="${apiRole.apiResources}">
							【${apiResource.name}】
						</c:forEach>
						</td>
						<td class="center"><a onclick="toEdit('${apiRole.id}')" style="cursor:pointer;">编辑</a></td>
					</tr>
					</c:forEach>
				</tbody>
			</table>
			<%@ include file="/WEB-INF/jsp/common/quotePageRefresh.jsp"%>
		</div>
	</div>
</body>
</html>