<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<c:if test="${not empty page && page.totalRecord > 0}">
<link type="text/css" rel="stylesheet" href="${skin}/js/layui-2.4.5/css/modules/laypage/default/laypage.css" />
<div id="paging" style="text-align:center;"></div>
<script type="text/javascript">
	layui.use("laypage", function() {
		layui.laypage.render({
			elem : "paging",
			count : parseInt("${page.totalRecord}"),
			limit : parseInt("${page.pageSize}"),
			limits : [ 10, 20, 30, 40, 50, 100 ],
			curr : parseInt("${page.pageNum}"),
			groups : 5,
			prev : "<",
			next: ">",
			first : 1,
			last : parseInt("${page.totalPage}"),
			layout : [ "count", "prev", "page", "next", "limit", "skip" ],
			theme : "#00F",
			jump : function(obj, first) {
				if (!first) {
					var newParams = new Array();
					var queryString = location.search;
					if (queryString != "") {
						var oldParams = queryString.substr(1).split("&");
						for (var i = 0; i < oldParams.length; i++) {
							var key = oldParams[i].split("=")[0];
							if (key != "pageNum" && key != "pageSize") {
								newParams.push(oldParams[i]);
							}
						}
					}
					newParams.push("pageNum=" + obj.curr);
					newParams.push("pageSize=" + obj.limit);
					queryString = "?" + newParams.join("&");
					doLoading();
					location.href = location.pathname + queryString;
				}
			}
		});
	});
</script>
</c:if>
<c:if test="${empty page || page.totalRecord < 1}">
	<div style="margin-bottom:15px;margin-top:15px;text-align:center;">暂无记录</div>
</c:if>