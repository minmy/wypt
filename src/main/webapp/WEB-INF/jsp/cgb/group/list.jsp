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

        function remove(id) {
            confirmAsk("您确定要删除该记录吗？", function () {
                $.ajax({
                    url: "${path}/groupOrder/remove.action?${_csrf.parameterName}=${_csrf.token}",
                    type: "POST",
                    data: {id: id},
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

        function isTop(id, isTop) {
            confirmAsk("您确定要置顶该拼团吗？", function () {
                $.ajax({
                    url: "${path}/groupOrder/isTop.action?${_csrf.parameterName}=${_csrf.token}",
                    type: "POST",
                    data: {id: id, isTop: isTop},
                    success: function (result) {
                        if (result.status) {
                            msgSuccess("操作成功", function () {
                                location.reload();
                            });
                        } else {
                            alertError(result.message);
                        }
                    }
                });
            });
        }

        function details(id) {
            location.href = "${path}/groupOrder/details.action?id=" + id;
        }
    </script>
</head>
<body class="login_body">
<div class="list-box">
    <div class="list-title">团单列表</div>
    <div class="list-search">
        <form action="${path}/groupOrder/list.action" onsubmit="doLoading()">
          <input type="hidden" name="pageSize" value="${param.pageSize}"/>
            团单编号 <input type="text" name="id" value="${param.id}"/>
            团长昵称 <input type="text" name="contacts" value="${param.contacts}" maxlength="20"/>
            所属小区 <input type="text" name="community" value="${param.community}" maxlength="20"/>
            团单状态 <select defaultValue="${param.groupOrderStates}" name="groupOrderStates">
            <option value="">全部</option>
            <c:forEach items="${groupOrderStates}" var="groupOrderState">
                <option value="${groupOrderState}">${groupOrderState.desc}</option>
            </c:forEach>
        </select>
            团单类型 <select defaultValue="${param.groupType}" name="groupType">
            <option value="">全部</option>
            <c:forEach items="${groupTypes}" var="groupType">
                <option value="${groupType}">${groupType.desc}</option>
            </c:forEach>
        </select>
                置顶 <select defaultValue="${param.isTop}" name="isTop">
            <option value="">全部</option>
            <option value="true">是</option>
            <option value="false">否</option>
        </select>
            <button class="list-search-button" type="submit">搜索</button>
        </form>
    </div>
    <div class="list-message">
        <div class="list-button">
        </div>
        <table>
            <thead>
            <tr>
                <th width="8%">团单编号</th>
                <th width="15%">团单主题</th>
                <th width="10%">团长昵称</th>
                <th width="12%">所属小区</th>
                <th width="7%">团单状态</th>
                <th width="13%">创建时间</th>
                <th width="7%">审核状态</th>
                <th width="7%">团单类型</th>
                <th width="13%">发货时间</th>
                <th width="8%">操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="groupOrder" items="${page.recordList}">
                <tr>
                    <td class="center">${groupOrder.id}</td>
                    <td class="center">${groupOrder.theme}</td>
                    <td class="center">${groupOrder.weChatUser.contacts}</td>
                    <td class="center">${groupOrder.community.name}</td>
                    <td class="center">${groupOrder.groupOrderState.desc}</td>
                    <td class="center">${u:formatDate(groupOrder.createTime, 'yyyy-MM-dd HH:mm:ss')}</td>
                    <td class="center">${groupOrder.reviewStates.desc}</td>
                    <td class="center">${groupOrder.groupType.desc}</td>
                    <td class="center">${u:formatDate(groupOrder.deliveryTime, 'yyyy-MM-dd HH:mm:ss')}</td>
                    <td class="center">
                            <%-- <c:if test="${groupOrder.groupOrderState.desc == '待发布' || groupOrder.groupOrderState.desc == '进行中'}">
                                <a onclick="remove('${groupOrder.id}')" style="cursor:pointer;">删除</a>
                            </c:if> --%>
                        <c:if test="${groupOrder.groupOrderState.desc == '进行中'}">
                            <c:choose>
                                <c:when test="${groupOrder.isTop == false}">
                                    <a style="cursor:pointer;" onclick="isTop('${groupOrder.id}', true)">置顶</a>
                                </c:when>
                                <c:otherwise>
                                    <a style="cursor:pointer;" onclick="isTop('${groupOrder.id}', false)">取消置顶</a>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                        <a style="cursor:pointer;" onclick="details('${groupOrder.id}')">详情</a>
                        <c:if test="${groupOrder.orders.size() != 0}">
                            <a style="cursor:pointer;" href="${path}/order/list.action?groupOrderId=${groupOrder.id}&fromGroup=true">订单</a>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <%@ include file="/WEB-INF/jsp/common/quotePageRefresh.jsp" %>
    </div>
</div>
</body>
</html>