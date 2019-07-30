<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<%@ include file="/WEB-INF/jsp/common/quoteHead.jsp"%>
<link type="text/css" rel="stylesheet" href="${skin}/css/edit.css" />
<link type="text/css" rel="stylesheet" href="${skin}/js/ztree-3.5.38/css/zTreeStyle/zTreeStyle.css" />
<script type="text/javascript" src="${skin}/js/ztree-3.5.38/jquery.ztree.all.min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$("#submitForm").Validform({
			tipSweep : true,
			tiptype : tiptypeRight,
			beforeSubmit : function(form) {
				$.ajax({
					url : "${path}/businessTerminal/save.action?${_csrf.parameterName}=${_csrf.token}",
					type : "POST",
					data : $("#submitForm").serialize(),
					success : function(result) {
						if (result.status) {
							msgSuccess("保存成功", function() {
								returnUrl();
							});
						} else {
							alertError(result.message);
						}
					}
				});
				return false;
			}
		});
	});
	function returnUrl() {
		history.go(-1);
	}
</script>
</head>
<body class="login_body">
	<div class="edit-box">
		<div class="edit-title">${empty businessTerminal ? "新增" : "编辑"}商家终端</div>
		<form class="edit-form" id="submitForm">
			<input type="hidden" name="id" value="${businessTerminal.id}" />
			<table>
				<tr>
					<td class="right" width="10%"><span class="required">*</span>posId：</td>
					<td width="40%">
						<input type="text" name="posId" maxlength="50" value="${businessTerminal.posId}" datatype="/^\w{3,50}$/" placeholder="由3-20位的字母、数字、下划线组成" nullmsg="请输入商家posId" />
					</td>
					<td class="right" width="10%"><span class="required">*</span>商家类型：</td>
					<td width="40%">
						<select name="businessId" datatype="*" nullmsg="请选择商家">
							<option value=""></option>
							<c:forEach items="${businessList}" var="business">
								<option value="${business.id}" <c:if test="${business.id == businessTerminal.business.id}">selected="selected"</c:if>>${business.name}</option>
							</c:forEach>
						</select>
					</td>
				</tr>
			</table>
			<div class="edit-button">
				<button type="submit">保存</button>
				<button type="button" onclick="returnUrl()">返回</button>
			</div>
		</form>
	</div>
</body>
</html>