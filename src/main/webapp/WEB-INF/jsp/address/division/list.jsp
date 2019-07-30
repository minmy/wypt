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
		location.href = "${path}/address/division/toEditDivision.action?id=" + id;
	}
	function doDelete() {
		if ($("input[name='ids']:checked").length == 0) {
			alertInfo("请选择要删除的记录");
			return;
		}
		confirmAsk("您确定要删除选中的记录吗？", function() {
			$.ajax({
				url : "${path}/address/division/deleteDivision.action?${_csrf.parameterName}=${_csrf.token}",
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
	/*function unLock(id) {
		confirmAsk("是否解锁该用户？", function() {
			$.ajax({
				url : "${path}/user/unLock.action?${_csrf.parameterName}=${_csrf.token}&id=" + id,
				type : "POST",
				success : function(result) {
					if (result.status) {
						msgSuccess("解锁成功", function() {
							location.reload();
						});
					} else {
						alertError(result.message);
					}
				}
			});
		});
	}*/
</script>
</head>
<body class="login_body">
	<div class="list-box">
		<div class="list-title">行政区划</div>
		<div class="list-search">
			<form action="${path}/address/division/toDivisionList.action" onsubmit="doLoading()">
				<input type="hidden" name="pageSize" value="${param.pageSize}" />
				区划全称 <input type="text" name="fullName" value="${param.fullName}" maxlength="30" />
				区划代码 <input type="text" name="code" value="${param.code}" maxlength="30" />
				区划类型 <select defaultValue="${param.divisionType}" name="divisionType">
							<option value="">全部</option>
							<c:forEach items="${divisionTypes}" var="type">
								<option value="${type}">${type.name}</option>
							</c:forEach>
						</select>
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
						<th width="15%">区划名称</th>
						<th width="20%">区划全称</th>
						<th width="15%">上级区划</th>
						<th width="15%">区划代码</th>
						<th width="10%">拼音</th>
						<th width="10%">区划类别</th>
						<th width="10%">操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="division" items="${page.recordList}">
					<tr>
						<td class="center"><input type="checkbox" name="ids" value="${division.id}" /></td>
						<td class="center">${division.name}</td>
						<td class="center">${division.fullName}</td>
						<td class="center">
							<c:if test="${division.divisionType.name() == 'province'}">
								${empty division.parentDivision ? '中国' : division.parentDivision.fullName}
							</c:if>
							<c:if test="${division.divisionType.name() == 'city'}">
								${division.parentDivision.fullName}
							</c:if>
							<c:if test="${division.divisionType.name() == 'area_or_county'}">
								${division.parentDivision.parentDivision.fullName}-${division.parentDivision.fullName}
							</c:if>
							<c:if test="${division.divisionType.name() == 'town_or_street'}">
								${division.parentDivision.parentDivision.parentDivision.fullName}-
								${division.parentDivision.parentDivision.fullName}-
								${division.parentDivision.fullName}
							</c:if>
						</td>
						<td class="center">${division.code}</td>
						<td class="center">${division.pinyin}</td>
						<td class="center">${division.divisionType.name}</td>
						<td class="center">
							<a onclick="toEdit('${division.id}')" style="cursor:pointer;">编辑</a>
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