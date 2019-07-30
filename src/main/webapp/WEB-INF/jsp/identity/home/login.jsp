<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<%@ include file="/WEB-INF/jsp/common/quoteHead.jsp"%>
<link type="text/css" rel="stylesheet" href="${skin}/css/login.css" />
<script type="text/javascript" src="${skin}/js/jquery.cookie-1.4.1.min.js"></script>
<script type="text/javascript">
	if (top.location != this.location) {
		top.location.replace(this.location.href);
	}
	$(document).ready(function() {
		var username = $.cookie("login_username");
		if (username != undefined && username.length > 0) {
			$("#username").val(username);
		}
		if ($("#username").val() != "") {
			$("#password").focus();
		} else {
			$("#username").focus();
		}
		var validformObj = $("#submitForm").Validform({
			btnSubmit : "#loginBtn",
			tipSweep : true,
			tiptype : tiptypeRight,
			beforeSubmit : function(form) {
				$.ajax({
					url : "${path}/doLogin.action",
					type : "POST",
					data : $("#submitForm").serialize(),
					success : function(result) {
						if (result.status) {
							$.cookie("login_username", $("#username").val(), {expires : 365, secure : isHttps});
							location.href = "${path}/index.action";
						} else {
							alertError(result.message);
						}
					}
				});
				return false;
			}
		});
	});
	$(document).keyup(function(event) {
		if (event.keyCode == 13) {
			$("#loginBtn").click();
		}
	});
</script>
</head>
<body class="login_body">
	<div class="login_wrap">
		<div class="login_title"><img src="${skin}/images/login_logo.png" /></div>
		<form id="submitForm">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<div class="login_box">
				<div class="title">用户登录</div>
				<div class="login_input user_name">
					<input type="text" id="username" name="username" maxlength="20" placeholder="帐号" datatype="*" nullmsg="请输入帐号" />
				</div>
				<div class="login_input pass_word">
					<input type="password" id="password" name="password" placeholder="密码" datatype="*" nullmsg="请输入密码" />
				</div>
				<div class="login_btn">
					<a id="loginBtn" style="cursor:pointer;">登 录</a>
				</div>
			</div>
		</form>
		<div class="copy_right">版权所有 Copyright 2019 All right reserved</div>
	</div>
</body>
</html>