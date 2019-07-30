<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<%@ include file="/WEB-INF/jsp/common/quoteHead.jsp"%>
<link type="text/css" rel="stylesheet" href="${skin}/css/edit.css" />
<link type="text/css" rel="stylesheet" href="${skin}/js/ztree-3.5.38/css/zTreeStyle/zTreeStyle.css" />
<script type="text/javascript" src="${skin}/js/ztree-3.5.38/jquery.ztree.all.min.js"></script>
<script type="text/javascript">
	var zTreeObj;
	$(document).ready(function() {
		$("#submitForm").Validform({
			tipSweep : true,
			tiptype : tiptypeRight,
			beforeSubmit : function(form) {
				$("#authorityTree").siblings("input").remove();
				$.each(zTreeObj.getCheckedNodes(), function(i, result) {
					$("#authorityTree").after("<input type='hidden' name='authorityIds' value='" + result.id + "' />");
				});
				$.ajax({
					url : "${path}/role/save.action?${_csrf.parameterName}=${_csrf.token}",
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
		var setting = {
			view : {
				selectedMulti : false,
				dblClickExpand : false
			},
			check: {
				enable: true
			},
			data: {
				simpleData: {
	    			enable: true,
	    			idKey: "id",
	    			pIdKey: "pId",
	    			rootPId: 0
	    		}
			},
			callback : {
				onClick : function(event, treeId, treeNode) {
					zTreeObj.expandNode(treeNode, true);
				}
			}
		};
		zTreeObj = $.fn.zTree.init($("#authorityTree"), setting, <%=request.getAttribute("authoritys")%>);
		$("input[name='authorityIds']").each(function() {
			var node = zTreeObj.getNodeByParam("id", $(this).val());
			if (node != null) {
				zTreeObj.checkNode(node, true, false);
				if (node.isParent) {
					zTreeObj.expandNode(node, true);
				}
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
		<div class="edit-title">${empty role ? "新增" : "编辑"}角色</div>
		<form class="edit-form" id="submitForm">
			<input type="hidden" name="id" value="${role.id}" />
			<table>
				<tr>
					<td class="right" width="10%"><span class="required">*</span>名称：</td>
					<td width="40%"><input type="text" name="name" value="${role.name}" maxlength="20" datatype="*" nullmsg="请输入名称" /></td>
					<td class="right" width="10%">备注：</td>
					<td width="40%"><input type="text" name="remark" value="${role.remark}" maxlength="255" /></td>
				</tr>
				<tr>
					<td class="right">授予权限：</td>
					<td colspan="3">
						<ul id="authorityTree" class="ztree" style="border:1px solid #d3d9e3;height:400px;overflow:auto;margin:0"></ul>
						<c:forEach var="authority" items="${role.authoritys}">
							<input type="hidden" name="authorityIds" value="${authority.id}" />
						</c:forEach>
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