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
					url : "${path}/user/save.action?${_csrf.parameterName}=${_csrf.token}",
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
		<div class="edit-title">${empty user ? "新增" : "编辑"}用户</div>
		<form class="edit-form" id="submitForm">
			<input type="hidden" name="id" value="${user.id}" />
			<table>
				<tr>
					<td class="right" width="10%"><c:if test="${empty user}"><span class="required">*</span></c:if>帐号：</td>
					<td width="40%">
						<c:if test="${empty user}">
							<input type="text" name="username" maxlength="20" placeholder="由3-20位的字母、数字、下划线组成" datatype="/^\w{3,20}$/" nullmsg="请输入帐号" errormsg="帐号格式不正确，由3-20位的字母、数字、下划线组成" />
						</c:if>
						<c:if test="${not empty user}">${user.username}</c:if>
					</td>
					<td class="right" width="10%"><span class="required">*</span>姓名：</td>
					<td width="40%"><input type="text" name="fullname" value="${user.fullname}" maxlength="20" datatype="*" nullmsg="请输入姓名" /></td>
				</tr>
				<tr>
					<td class="right"><c:if test="${empty user}"><span class="required">*</span></c:if>密码：</td>
					<td><input id="password" type="password" name="password" placeholder="由6-20位字符组成${empty user ? '' : '，不修改密码请留空'}" datatype="*6-20" ${empty user ? '' : 'ignore="ignore"'} nullmsg="请输入密码" errormsg="密码格式不正确，由6-20位字符组成" /></td>
					<td class="right"><c:if test="${empty user}"><span class="required">*</span></c:if>确认密码：</td>
					<td><input id="againPwd" type="password" placeholder="请再次输入密码" datatype="*0-20" nullmsg="请输入确认密码" recheck="password" errormsg="两次输入的密码不一致" /></td>
				</tr>
				<tr>
					<td class="right">授予角色：</td>
					<td>
						<c:forEach var="role" items="${roleList}">
							<c:set var="checked" value="" />
							<c:forEach var="tempRole" items="${user.roles}">
								<c:if test="${role eq tempRole}">
									<c:set var="checked" value="checked" />
								</c:if>
							</c:forEach>
							<label style="cursor:pointer;"><input type="checkbox" name="roleIds" value="${role.id}" ${checked} /> ${role.name}</label>
						</c:forEach>
					</td>
					<td class="right">禁用帐号：</td>
					<td><label style="cursor:pointer;"><input type="checkbox" name="disable" value="true" ${user.disable ? 'checked' : ''} /> 是</label></td>
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