<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<%@ include file="/WEB-INF/jsp/common/quoteHead.jsp"%>
<link type="text/css" rel="stylesheet" href="${skin}/css/style.css?5" />
<script type="text/javascript">
	$(document).ready(function() {
		$(".sublevel-tabToggle").click(function() {
			$(this).parent().siblings().find(".menu-sublevel-item").slideUp(400);
			$(this).next(".menu-sublevel-item").slideToggle(400);
			var clickButtonObj = $(this).find(".clickButton");
			$(".clickButton").not(clickButtonObj).removeClass("active");
			clickButtonObj.toggleClass("active");
		});
		$(".menu-item a").click(function() {
			$(".menu-item a").removeClass("active");
			$(this).addClass("active");
		});
	});
	function logout() {
		confirmAsk("确定退出登录吗？", function() {
			$.ajax({
				url : "${path}/doLogout.action?${_csrf.parameterName}=${_csrf.token}",
				type : "POST",
				success : function(result) {
					if (result.status) {
						location.href = "${path}/login.action";
					} else {
						alertError(result.message);
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					if (XMLHttpRequest.status == 403) {
						location.href = "${path}/login.action";
					} else {
						alertError("请求出错了，请重试！错误原因：" + errorThrown + "(" + XMLHttpRequest.status + ")");
					}
				}
			});
		});
	}
</script>
</head>
<body class="login_body">
	<div class="header">
		<div class="logo"><img src="${skin}/images/index_logo.png" /></div>
		<div class="header-user">
			<div class="user-info">
				<a>${loginUser.fullname}</a>
			</div>
			<div class="logout">
				<a onclick="logout()" style="cursor:pointer;">退出</a>
			</div>
		</div>
	</div>
	<div class="left-menu">
		<div class="menu-item">
			<a class="active" href="about:blank" target="main-frame"><i class="item-icon home-icon"></i>首页</a>
		</div>
		<sec:authorize access="hasRole('ROLE_APPLY_MANAGE')">
			<div class="menu-item">
				<span class="sublevel-tabToggle"><i class="item-icon setting-icon"></i>提现申请管理<i class="clickButton"></i></span>
				<div class="menu-sublevel-item">
					<sec:authorize access="hasRole('ROLE_APPLY_LIST_MANAGE')">
					<a href="${path}/apply/list.action" target="main-frame"><i class="sublevel-item-icon"></i>提现申请列表</a>
					</sec:authorize>
				</div>
			</div>
		</sec:authorize>
		<sec:authorize access="hasRole('ROLE_GROUP_ORDER_MANAGE')">
			<div class="menu-item">
				<span class="sublevel-tabToggle"><i class="item-icon setting-icon"></i>团单管理<i class="clickButton"></i></span>
				<div class="menu-sublevel-item">
					<sec:authorize access="hasRole('ROLE_GROUP_ORDER_LIST_MANAGE')">
					<a href="${path}/groupOrder/list.action" target="main-frame"><i class="sublevel-item-icon"></i>团单列表</a>
					</sec:authorize>
				</div>
			</div>
		</sec:authorize>
		<sec:authorize access="hasRole('ROLE_ORDER_MANAGE')">
			<div class="menu-item">
				<span class="sublevel-tabToggle"><i class="item-icon setting-icon"></i>订单管理<i class="clickButton"></i></span>
				<div class="menu-sublevel-item">
					<sec:authorize access="hasRole('ROLE_ORDER_LIST_MANAGE')">
						<a href="${path}/order/list.action" target="main-frame"><i class="sublevel-item-icon"></i>订单列表</a>
					</sec:authorize>
				</div>
			</div>
		</sec:authorize>
		<sec:authorize access="hasRole('ROLE_BUSINESS_MANAGE')">
			<div class="menu-item">
				<span class="sublevel-tabToggle"><i class="item-icon setting-icon"></i>商家管理<i class="clickButton"></i></span>
				<div class="menu-sublevel-item">
					<sec:authorize access="hasRole('ROLE_BUSINESS_LIST_MANAGE')">
					<a href="${path}/business/list.action" target="main-frame"><i class="sublevel-item-icon"></i>商家列表</a>
					</sec:authorize>
					<sec:authorize access="hasRole('ROLE_BUSINESS_TERMINAL_LIST_MANAGE')">
					<a href="${path}/businessTerminal/list.action" target="main-frame"><i class="sublevel-item-icon"></i>商家终端列表</a>
					</sec:authorize>
				</div>
			</div>
		</sec:authorize>
		<sec:authorize access="hasRole('ROLE_VERIFY_MANAGE')">
			<div class="menu-item">
				<span class="sublevel-tabToggle"><i class="item-icon setting-icon"></i>核销管理<i class="clickButton"></i></span>
				<div class="menu-sublevel-item">
					<sec:authorize access="hasRole('ROLE_VERIFY_ORDER_MANAGE')">
						<a href="${path}/verify/queryOrderPage.action" target="main-frame"><i class="sublevel-item-icon"></i>核销订单</a>
					</sec:authorize>
					<sec:authorize access="hasRole('ROLE_VERIFY_SHEETLIST_LIST_MANAGE')">
						<a href="${path}/verify/sheetList.action" target="main-frame"><i class="sublevel-item-icon"></i>核销单列表</a>
					</sec:authorize>
					<sec:authorize access="hasRole('ROLE_VERIFY_LOG_LIST_MANAGE')">
						<a href="${path}/verify/logList.action" target="main-frame"><i class="sublevel-item-icon"></i>核销日志</a>
					</sec:authorize>
				</div>
			</div>
		</sec:authorize>
		<sec:authorize access="hasRole('ROLE_SYSTEM_MANAGE')">
		<div class="menu-item">
			<span class="sublevel-tabToggle"><i class="item-icon setting-icon"></i>系统管理<i class="clickButton"></i></span>
			<div class="menu-sublevel-item">
				<sec:authorize access="hasRole('ROLE_USER_MANAGE')">
				<a href="${path}/user/list.action" target="main-frame"><i class="sublevel-item-icon"></i>用户管理</a>
				</sec:authorize>
				<sec:authorize access="hasRole('ROLE_ROLE_MANAGE')">
				<a href="${path}/role/list.action" target="main-frame"><i class="sublevel-item-icon"></i>角色管理</a>
				</sec:authorize>
			</div>
		</div>
		</sec:authorize>
		<sec:authorize access="hasRole('ROLE_API_MANAGE')">
		<div class="menu-item">
			<span class="sublevel-tabToggle"><i class="item-icon setting-icon"></i>接口管理<i class="clickButton"></i></span>
			<div class="menu-sublevel-item">
				<sec:authorize access="hasRole('ROLE_API_RESOURE_MANAGE')">
				<a href="${path}/apiResource/list.action" target="main-frame"><i class="sublevel-item-icon"></i>资源管理</a>
				</sec:authorize>
				<sec:authorize access="hasRole('ROLE_API_ROLE_MANAGE')">
				<a href="${path}/apiRole/list.action" target="main-frame"><i class="sublevel-item-icon"></i>接口角色管理</a>
				</sec:authorize>
				<sec:authorize access="hasRole('ROLE_WECHAT_USER_MANAGE')">
				<a href="${path}/user/wechatUserList.action" target="main-frame"><i class="sublevel-item-icon"></i>微信用户管理</a>
				</sec:authorize>
			</div>
		</div>
		</sec:authorize>
		<sec:authorize access="hasRole('ROLE_ADDRESS_LIBRARY_MANAGE')">
		<div class="menu-item">
			<span class="sublevel-tabToggle"><i class="item-icon setting-icon"></i>地址库<i class="clickButton"></i></span>
			<div class="menu-sublevel-item">
				<sec:authorize access="hasRole('ROLE_DIVISION_MANAGE')">
					<a href="${path}/address/division/toDivisionList.action" target="main-frame"><i class="sublevel-item-icon"></i>行政区划</a>
				</sec:authorize>
				<sec:authorize access="hasRole('ROLE_COMMUNITY_MANAGE')">
					<a href="${path}/address/community/toCommunityList.action" target="main-frame"><i class="sublevel-item-icon"></i>小区管理</a>
				</sec:authorize>
			</div>
		</div>
		</sec:authorize>
	</div>
	<div class="wrapper">
		<iframe src="about:blank" frameborder="0" width="100%" height="100%" name="main-frame"></iframe>
	</div>
</body>
</html>