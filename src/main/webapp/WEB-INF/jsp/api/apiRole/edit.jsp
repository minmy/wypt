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
				$("#apiResourceTree").siblings("input").remove();
				$.each(zTreeObj.getCheckedNodes(), function(i, result) {
					$("#apiResourceTree").after("<input type='hidden' name='apiResourceIds' value='" + result.id + "' />");
				});
				var menus = new Array();
				$.each($(".menuName"), function(i, obj) {
					if ($(obj).val() != "") {
						var menu = {};
						menu.id = $(obj).parent().parent().find(".menuId").val();
						menu.name = $(obj).val();
						menu.url = $(obj).parent().parent().find(".menuUrl").val();
						menus.push(menu);
					}
				});
				$.ajax({
					url : "${path}/apiRole/save.action?${_csrf.parameterName}=${_csrf.token}",
					type : "POST",
					data : $("#submitForm").serialize() + "&menus=" + JSON.stringify(menus),
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
		zTreeObj = $.fn.zTree.init($("#apiResourceTree"), setting, <%=request.getAttribute("apiResources")%>);
		$("input[name='apiResourceIds']").each(function() {
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
	function addMenu() {
		var html = '<tr>';
		html += '<input type="hidden" class="menuId" value="" />';
		html += '<td class="right" width="10%">菜单名称：</td>';
		html += '<td width="40%"><input type="text" class="menuName" value="" maxlength="20" /></td>';
		html += '<td class="right" width="10%">url:</td>';
		html += '<td width="40%"><input type="text" class="menuUrl" value="" maxlength="250" /></td>';
		html += '</tr>';
		$("#tb tbody").append(html);
	}
</script>
</head>
<body class="login_body">
	<div class="edit-box">
		<div class="edit-title">${empty apiRole ? "新增" : "编辑"}接口角色</div>
		<form class="edit-form" id="submitForm">
			<input type="hidden" name="id" value="${apiRole.id}" />
			<table id="tb">
				<tr>
					<td class="right" width="10%"><span class="required">*</span>名称：</td>
					<td colspan="3"><input type="text" name="name" value="${apiRole.name}" maxlength="20" datatype="*" nullmsg="请输入名称" /></td>
				</tr>
				<tr>
					<td class="right">可访问资源：</td>
					<td colspan="3">
						<ul id="apiResourceTree" class="ztree" style="border:1px solid #d3d9e3;height:400px;overflow:auto;margin:0"></ul>
						<c:forEach var="apiResource" items="${apiRole.apiResources}">
							<input type="hidden" name="apiResourceIds" value="${apiResource.id}" />
						</c:forEach>
					</td>
				</tr>
				<c:forEach items="${menus}" var="menu">
					<tr>
						<input type="hidden" class="menuId" value="${menu.id}" />
						<td class="right" width="10%">菜单名称：</td>
						<td width="40%"><input type="text" class="menuName" value="${menu.name}" maxlength="20" /></td>
						<td class="right" width="10%">url:</td>
						<td width="40%"><input type="text" class="menuUrl" value="${menu.url}" maxlength="250" /></td>
					</tr>
				</c:forEach>
			</table>
			<div class="edit-button">
				<button type="button" onclick="addMenu()">添加菜单</button>
				<button type="submit">保存</button>
				<button type="button" onclick="returnUrl()">返回</button>
			</div>
		</form>
	</div>
</body>
</html>