<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
    <%@ include file="/WEB-INF/jsp/common/quoteHead.jsp" %>
    <link type="text/css" rel="stylesheet" href="${skin}/css/list.css"/>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#selectAll").click(function () {
                $("input[name='ids']").prop("checked", $(this).is(":checked"));
            });
            $("input[name='ids']").click(function () {
                $("#selectAll").prop("checked", $("input[name='ids']:checked").length == $("input[name='ids']").length);
            });
        });

        function doDelete() {
            if ($("input[name='ids']:checked").length == 0) {
                alertInfo("请选择要删除的记录");
                return;
            }
            console.log($("input[name='ids']:checked").serialize())
            confirmAsk("您确定要删除选中的记录吗？", function () {
                $.ajax({
                    url: "${path}/verify/deleteLog.action?${_csrf.parameterName}=${_csrf.token}",
                    type: "POST",
                    data: $("input[name='ids']:checked").serialize(),
                    success: function (result) {
                        if (result.status) {
                            msgSuccess("删除成功", function () {
                                location.reload();
                            });
                        } else {
                            alertError(result.message);
                        }
                    }
                });
            });
        }
        function showKey(key) {
            alertEx(key, {
                title : "请求参数",
                area : ['800px' , '200px']
            });
        }
    </script>
</head>
<body class="login_body">
<div class="list-box">
    <div class="list-title">
        核销日志
    </div>
    <div class="list-search">
        <form action="${path}/verify/logList.action" onsubmit="doLoading()">
            <input type="hidden" name="pageSize" value="${param.pageSize}"/>
            商户编号<input type="text" name="mchId" value="${param.mchId}"/>
            key参数 <input type="text" name="key" value="${param.key}"/>
            状态码 <input type="text" name="status" value="${param.status}"/>
            类型
            <select defaultValue="${param.apiType}" name="apiType">
                <option value="">全部</option>
                <c:forEach items="${apiTypes}" var="apiType">
                    <option value="${apiType}">${apiType.desc}</option>
                </c:forEach>
            </select>
            <button class="list-search-button" type="submit">搜索</button>
            <c:if test="${param.fromGroup}">
                <button class="list-search-button" type="button" onclick="returnUrl()">返回</button>
            </c:if>
        </form>
    </div>
    <div class="list-message">
        <div class="list-button">
            <span class="del" onclick="doDelete()"><i></i>删除</span>
        </div>
        <table>
            <thead>
            <tr>
                <th width="5%"><input type="checkbox" id="selectAll"/></th>
                <th width="13%">商户编号</th>
                <th width="13%">状态码</th>
                <th width="7%">返回信息</th>
                <th width="7%">类型</th>
                <th width="10%">请求参数</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="log" items="${page.recordList}">
                <tr>
                    <td class="center"><input type="checkbox" name="ids" value="${log.id}"/></td>
                    <td class="center">${log.mchId}</td>
                    <td class="center">${log.status}</td>
                    <td class="center">${log.message}</td>
                    <td class="center">${log.apiType.desc}</td>
                    <td class="center"><a href="javascript:;" onclick="showKey('${log.key}')">查看</a></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <%@ include file="/WEB-INF/jsp/common/quotePageRefresh.jsp" %>
    </div>
</div>
</body>
</html>