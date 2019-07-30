<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<%@ include file="/WEB-INF/jsp/common/quoteHead.jsp"%>
<link type="text/css" rel="stylesheet" href="${skin}/css/edit.css" />
<script type="text/javascript">
	$(document).ready(function() {
		$("#submitForm").Validform({
			tipSweep : true,
			tiptype : tiptypeRight,
			beforeSubmit : function(form) {
				$.ajax({
					url : "${path}/address/shipping/saveShipping.action?${_csrf.parameterName}=${_csrf.token}",
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
		<div class="edit-title">${empty community ? "新增" : "编辑"}配送地址</div>
		<form class="edit-form" id="submitForm">
			<input type="hidden" name="communityId" value="${communityId}" />
			<input type="hidden" name="id" value="${shipping.id}" />
			<table>
				<tr>
					<td class="right" width="10%"><span class="required">*</span>详细地址：</td>
					<td width="40%">
						<%--<input type="text" name="detailedAddress" value="${shipping.detailedAddress}" maxlength="100" datatype="*" nullmsg="请输入小区名称" />--%>
                        <textarea name="detailedAddress" rows="5" cols="120" maxlength="100" datatype="*" nullmsg="请输入小区名称">${shipping.detailedAddress}</textarea>
					</td>
                    <td class="right"></td>
                    <td></td>
				</tr>
                <tr>
                    <td class="right">是否设为默认：</td>
                    <td><label style="cursor:pointer;"><input type="checkbox" name="defaultFlag" value="true" ${shipping.defaultFlag ? 'checked' : ''} /> 是</label></td>
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