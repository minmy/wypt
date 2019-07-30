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
				$("#communityTree").siblings("input").remove();
				$.each(zTreeObj.getCheckedNodes(), function(i, result) {
					$("#communityTree").after("<input type='hidden' name='communityIds' value='" + result.id + "' />");
				});
				$.ajax({
					url : "${path}/user/wechatUserSave.action?${_csrf.parameterName}=${_csrf.token}",
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
		zTreeObj = $.fn.zTree.init($("#communityTree"), setting, <%=request.getAttribute("communitys")%>);
		$("input[name='communityIds']").each(function() {
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
		<div class="edit-title">编辑微信用户</div>
		<form class="edit-form" id="submitForm">
			<input type="hidden" name="id" value="${weChatUser.id}" />
			<table>
				<tr>
					<td class="right" width="10%">openId：</td>
					<td width="40%">${weChatUser.openId}</td>
					<td class="right" width="10%">微信昵称：</td>
					<td width="40%">${weChatUser.name}</td>
				</tr>
				<tr>
					<td class="right">联系人：</td>
					<td>${weChatUser.contacts}</td>
					<td class="right">联系电话：</td>
					<td>${weChatUser.phone}</td>
				</tr>
				<tr>
					<td class="right">授予接口角色：</td>
					<td colspan="3">
						<c:forEach var="apiRole" items="${apiRoleList}">
							<c:set var="checked" value="" />
							<c:forEach var="tempApiRole" items="${weChatUser.apiRoles}">
								<c:if test="${apiRole eq tempApiRole}">
									<c:set var="checked" value="checked" />
								</c:if>
							</c:forEach>
							<label style="cursor:pointer;"><input type="checkbox" name="apiRoleIds" value="${apiRole.id}" ${checked} /> ${apiRole.name}</label>
						</c:forEach>
					</td>
				</tr>
				<tr>
					<td class="right">可管理的小区：</td>
					<td colspan="3">
						<ul id="communityTree" class="ztree" style="border:1px solid #d3d9e3;height:400px;overflow:auto;margin:0"></ul>
						<c:forEach var="community" items="${weChatUser.communities}">
							<input type="hidden" name="communityIds" value="${community.id}" />
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