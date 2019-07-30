<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<%@ include file="/WEB-INF/jsp/common/quoteHead.jsp"%>
<link type="text/css" rel="stylesheet" href="${skin}/css/list.css" />
<script type="text/javascript">
	function toEdit(id) {
		location.href = "${path}/user/wechatUserEdit.action?id=" + id;
	}
</script>
</head>
<body class="login_body">
	<div class="list-box">
		<div class="list-title">微信用户</div>
		<div class="list-search">
			<form action="${path}/user/wechatUserList.action" onsubmit="doLoading()">
				<input type="hidden" name="pageSize" value="${param.pageSize}" />
				openId <input type="text" name="openId" value="${param.openId}" maxlength="100" />
				微信昵称 <input type="text" name="name" value="${param.name}" maxlength="100" />
				联系人 <input type="text" name="contacts" value="${param.contacts}" maxlength="100" />
				是否里长 <select name="brigadier" defaultValue="${param.brigadier}">
					<option value=""></option>
					<option value="true">是</option>
					<option value="false">否</option>
				</select>
				所属小区 <select name="communityId" defaultValue="${param.communityId}">
					<option value=""></option>
					<c:forEach items="${communities}" var="community">
						<option value="${community.id}">${community.name}</option>
					</c:forEach>
				</select>
				<button class="list-search-button" type="submit">搜索</button>
			</form>
		</div>
		<div class="list-message">
			<table>
				<thead>
					<tr>
						<th width="5%">openId</th>
						<th width="10%">头像</th>
						<th width="20%">微信昵称</th>
						<th width="15%">联系人</th>
						<th width="15%">所属小区</th>
						<th width="10%">是否里长</th>
						<th width="13%">创建时间</th>
						<th width="7%">操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="wechatUser" items="${page.recordList}">
					<tr>
						<td class="center">${wechatUser.openId}</td>
						<td class="center"><img src="${wechatUser.headImgUrl}" height="30" /></td>
						<td class="center">${wechatUser.name}</td>
						<td class="center">${wechatUser.contacts}</td>
						<td class="center">${wechatUser.community.name}</td>
						<td class="center">${wechatUser.brigadier ? "是" : "否"}</td>
						<td class="center">${u:formatDate(wechatUser.createTime, 'yyyy-MM-dd HH:mm:ss')}</td>
						<td class="center">
							<a onclick="toEdit('${wechatUser.id}')" style="cursor:pointer;">编辑</a>
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