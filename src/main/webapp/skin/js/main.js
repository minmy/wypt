var isHttps = location.protocol == "https";
var loadingIndex;
var layer;
layui.use("layer", function() {
	layer = layui.layer;
});
$(document).ready(function() {
	$("input, textarea").placeholder();
	$.ajaxSetup({
		cache : false,
		dataType : "json",
		beforeSend : function(XMLHttpRequest) {
			doLoading();
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alertError("请求出错了，请重试！错误原因：" + errorThrown + "(" + XMLHttpRequest.status + ")");
		},
		complete : function(XMLHttpRequest, textStatus) {
			finishLoading();
		}
	});
	$("select[defaultValue]").each(function() {
		var defaultValue = $(this).attr("defaultValue");
		if (defaultValue != "") {
			$(this).val(defaultValue);
		}
	});
});
var tiptypeRight = function(msg, o, cssctl) {
	if (o.type == 3) {
		if (o.obj.is(":hidden")) {
			tipRight(msg, $("input[errormsg='" + o.obj.attr("errormsg") + "']").not(":hidden").first()); // 修正Validform在IE8的一个错误
		} else {
			tipRight(msg, o.obj);
		}
	}
}
var tiptypeBottom = function(msg, o, cssctl) {
	if (o.type == 3) {
		tipBottom(msg, o.obj);
	}
}
var tiptypeAlert = function(msg, o, cssctl) {
	if (o.type == 3) {
		alertError(msg);
	}
}
function notEmpty(selector, message, trim) {
	var value;
	if (trim) {
		value = $.trim($(selector).val());
		$(selector).val(value);
	} else {
		value = $(selector).val();
	}
	if (value == undefined || value == null || value == "") {
		tipRight(message, selector);
		$(selector).focus();
		throw message;
	}
}
function matchRegex(selector, message, regex, trim) {
	var value;
	if (trim) {
		value = $.trim($(selector).val());
		$(selector).val(value);
	} else {
		value = $(selector).val();
	}
	if (!new RegExp(regex).test(value)) {
		tipRight(message, selector);
		$(selector).focus();
		throw message;
	}
}
function msgInfo(content, callbackMethod) {
	msgEx(content, {
		icon : 0,
		time : 3000,
		shade : 0.3,
		shadeClose : true
	}, callbackMethod);
}
function msgSuccess(content, callbackMethod) {
	msgEx(content, {
		icon : 1,
		time : 3000,
		shade : 0.3,
		shadeClose : true
	}, callbackMethod);
}
function msgError(content, callbackMethod) {
	msgEx(content, {
		icon : 2,
		time : 3000,
		shade : 0.3,
		shadeClose : true
	}, callbackMethod);
}
function msgEx(content, options, callbackMethod) {
	layer.msg(content, options, callbackMethod);
}
function alertInfo(content, callbackMethod) {
	alertEx(content, {
		title : "提示",
		icon : 0
	}, callbackMethod);
}
function alertSuccess(content, callbackMethod) {
	alertEx(content, {
		title : "提示",
		icon : 1
	}, callbackMethod);
}
function alertError(content, callbackMethod) {
	alertEx(content, {
		title : "提示",
		icon : 2
	}, callbackMethod);
}
function alertEx(content, options, callbackMethod) {
	layer.alert(content, options, function(index) {
		layer.close(index);
		if (callbackMethod != undefined) {
			callbackMethod();
		}
	});
}
function confirmAsk(content, okCallbackMethod, cancelCallbackMethod) {
	confirmEx(content, {
		title : "提示",
		icon : 3
	}, okCallbackMethod, cancelCallbackMethod);
}
function confirmEx(content, options, okCallbackMethod, cancelCallbackMethod) {
	layer.confirm(content, options, function(index) {
		layer.close(index);
		if (okCallbackMethod != undefined) {
			okCallbackMethod();
		}
	}, cancelCallbackMethod)
}
function tipTop(content, follow) {
	tipEx(content, follow, {
		tips : [ 1, '#FF0000' ]
	});
}
function tipRight(content, follow) {
	tipEx(content, follow, {
		tips : [ 2, '#FF0000' ]
	});
}
function tipBottom(content, follow) {
	tipEx(content, follow, {
		tips : [ 3, '#FF0000' ]
	});
}
function tipLeft(content, follow) {
	tipEx(content, follow, {
		tips : [ 4, '#FF0000' ]
	});
}
function tipEx(content, follow, options) {
	if ($(follow).is(":visible")) {
		layer.tips(content, follow, options);
	} else {
		alertInfo(content);
	}
}
function doLoading() {
	loadingIndex = layer.load();
}
function finishLoading() {
	layer.close(loadingIndex);
}