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

        function moneyTransfer(id) {
            location.href = "${path}/order/orderDetail.action?id=" + id;

        }

        function returnUrl() {
            history.go(-1);
        }
    </script>
</head>
<body class="login_body">
<div class="list-box">
    <div class="list-title">
        <c:if test="${param.fromGroup}">该团单所有</c:if>订单列表
    </div>
    <div class="list-search">
        <form action="${path}/order/list.action" onsubmit="doLoading()">
            <input type="hidden" name="pageSize" value="${param.pageSize}"/>
            <input type="hidden" name="groupOrderId" value="${param.groupOrderId}"/>
            订单编号 <input type="text" name="orderNumber" value="${param.orderNumber}"/>
            <%--				申请人姓名 <input type="text" name="applyName" value="${param.applyName}" maxlength="20" />--%>
            <%--				申请人电话 <input type="text" name="applyPhone" value="${param.applyPhone}" maxlength="11" />--%>
            <%--				申请人微信 <input type="text" name="applyWeChat" value="${param.applyWeChat}" />--%>
            <%--				<br>--%>
            支付状态 <select defaultValue="${param.payState}" name="payState">
            <option value="">全部</option>
            <c:forEach items="${payStates}" var="payState">
                <option value="${payState}">${payState.desc}</option>
            </c:forEach>
        </select>
            订单状态 <select defaultValue="${param.orderState}" name="orderState">
            <option value="">全部</option>
            <c:forEach items="${orderStates}" var="orderState">
                <option value="${orderState}">${orderState.desc}</option>
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
        </div>
        <table>
            <thead>
            <tr>
                <th width="8%">订单号</th>
                <th width="8%">订单随机码</th>
                <th width="13%">所属团单</th>
                <th width="10%">联系人</th>
<%--                <th width="13%">所属用户</th>--%>
                <th width="7%">实际付款</th>
                <th width="7%">支付流水号</th>
                <th width="7%">支付状态</th>
                <th width="12%">订单状态</th>
                <th width="7%">商品详情</th>
                <th width="5%">核销记录</th>
                <th width="8%">操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="order" items="${page.recordList}">
                <tr>
                    <td class="center">${order.orderNumber}</td>
                    <td class="center">${order.randomNumber}</td>
                    <td class="center">${order.groupOrder.theme}</td>
                    <td class="center">${order.contacts}</td>
<%--                    <td class="center">${order.weChatUser.name}</td>--%>
                    <td class="center">${order.finalPayment}</td>
                    <td class="center">${order.payNumber}</td>
                    <td class="center">${order.payState.desc}</td>
                    <td class="center">${order.orderState.orderStates.desc}</td>
                    <td class="center">
                        <a style="cursor:pointer;"
                           href="${path}/order/commodityDetail.action?id=${order.id}">查看</a>
                    </td>
                    <td class="center">
                        <a style="cursor:pointer;"
                           href="${path}/verify/sheetList.action?orderNumber=${order.orderNumber}&fromOther=true">查看</a>
                    </td>
                    <td class="center">
                        <a style="cursor:pointer;" onclick="moneyTransfer('${order.id}')">详情</a>
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