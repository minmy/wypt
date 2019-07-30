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
					url : "${path}/apiResource/save.action?${_csrf.parameterName}=${_csrf.token}",
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
		<div class="edit-title">${empty apiResource ? "新增" : "编辑"}接口资源</div>
		<form class="edit-form" id="submitForm">
			<input type="hidden" name="id" value="${apiResource.id}" />
			<table>
				<tr>
					<td class="right" width="10%"><span class="required">*</span>名称：</td>
					<td width="40%"><input type="text" name="name" value="${apiResource.name}" maxlength="20" datatype="*" nullmsg="请输入名称" /></td>
					<td class="right" width="10%"><span class="required">*</span>URI：</td>
					<td width="40%"><input type="text" name="uri" value="${apiResource.uri}" maxlength="50" placeholder="以/api/开头，最长50位字符组成" datatype="*" nullmsg="请输入URI" /></td>
				</tr>
				<tr>
					<td class="right">启用频率限制：</td>
					<td><input type="checkbox" id="enableRateLimit" name="enableRateLimit" value="true" ${apiResource.enableRateLimit ? 'checked' : ''} /><label for="enableRateLimit">是</label></td>
					<td class="right"><span class="required">*</span>限制周期：</td>
					<td>
						<select name="limitPeriod" defaultValue="${apiResource.limitPeriod}" datatype="*" nullmsg="请输入选择限制周期">
							<option value="">请选择</option>
							<c:forEach var="limitPeriod" items="${limitPeriods}">
							<option value="${limitPeriod}">${limitPeriod.desc}</option>
							</c:forEach>
						</select>
					</td>
				</tr>
				<tr>
					<td class="right"><span class="required">*</span>限制次数：</td>
					<td><input type="text" name="limitCount" value="${apiResource.limitCount}" maxlength="10" placeholder="由1-10位的数字组成" datatype="n1-10" nullmsg="请输入限制次数"  errormsg="限制次数不正确，由1-10位的数字" /></td>
					<td class="right"></td>
					<td></td>
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