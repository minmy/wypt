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
					url : "${path}/business/save.action?${_csrf.parameterName}=${_csrf.token}",
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
		<div class="edit-title">${empty business ? "新增" : "编辑"}商家</div>
		<form class="edit-form" id="submitForm">
			<input type="hidden" name="id" value="${business.id}" />
			<table>
				<tr>
					<td class="right" width="10%"><span class="required">*</span>商家名称：</td>
					<td width="40%">
						<input type="text" name="name" maxlength="20" value="${business.name}" datatype="*" placeholder="请输入商家名称" nullmsg="请输入商家名称" />
					</td>
					<td class="right" width="10%"><span class="required">*</span>商家类型：</td>
					<td width="40%">
						<select name="businessType" datatype="*" nullmsg="请选择商家类型">
							<option value=""></option>
							<c:forEach items="${businessType}" var="businessType">
								<option value="${businessType}" <c:if test="${business.businessType == businessType}">selected="selected"</c:if>>${businessType.desc}</option>
							</c:forEach>
						</select>
					</td>
				</tr>
				<tr>
					<td class="right"><span class="required">*</span>mchId：</td>
					<td><input id="password" type="text" name="mchId" value="${business.mchId}" placeholder="请输入商户编号mchId" datatype="*" nullmsg="请输入商户编号mchId" /></td>
					<td class="right"><c:if test="${empty business}"><span class="required">*</span></c:if>mchKey：</td>
					<td><input id="againPwd" type="password" name="mchKey" ${empty business ? '' : 'ignore="ignore"'} placeholder="${empty business ? '请输入签名密钥mchKey' : '签名密钥mchKey, 不修改请留空'}" datatype="*" nullmsg="请输入签名密钥mchKey"></td>
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