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
        核销列表
    </div>
    <div class="list-search">
        <form action="${path}/verify/sheetList.action" onsubmit="doLoading()">
            <input type="hidden" name="pageSize" value="${param.pageSize}"/>
            <input type="hidden" name="fromOther" value="${param.fromOther}"/>
            <c:if test="${param.fromOther}">
                <input type="hidden" name="orderNumber" value="${param.orderNumber}"/>
                <input type="hidden" name="mchId" value="${param.mchId}"/>
            </c:if>
            <c:if test="${!param.fromOther}">
                核销流水号 <input type="text" name="verificationCode" value="${param.verificationCode}"/>
                商户编号 <input type="text" name="mchId" value="${param.mchId}"/>
                终端号 <input type="text" name="posId" value="${param.posId}"/>
                订单编号 <input type="text" name="orderNumber" value="${param.orderNumber}"/>
                <br>
            </c:if>
            核销状态
            <select defaultValue="${param.verificationStatus}" name="verificationStatus">
                <option value="">全部</option>
                <c:forEach items="${verificationStatus}" var="vs">
                    <option value="${vs}">${vs.desc}</option>
                </c:forEach>
            </select>
            <button class="list-search-button" type="submit">搜索</button>
            <c:if test="${param.fromOther}">
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
                <th width="8%">核销流水号</th>
                <th width="13%">商户编号</th>
                <th width="10%">终端编号</th>
                <th width="13%">消费者</th>
                <th width="7%">订单编号</th>
                <th width="7%">商品编号</th>
                <th width="7%">核销人</th>
                <th width="12%">核销状态</th>
                <%--                <th width="8%">操作</th>--%>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="sheet" items="${page.recordList}">
                <tr>
                    <td class="center">${sheet.verificationCode}</td>
                    <td class="center">${sheet.mchId}</td>
                    <td class="center">${sheet.posId}</td>
                    <td class="center">${sheet.openId}</td>
                    <td class="center">${sheet.orderNumber}</td>
                    <td class="center">${sheet.orderGoodsNumber}</td>
                    <td class="center">${sheet.verificationer}</td>
                    <td class="center">${sheet.status.desc}</td>
                        <%--                    <td class="center">${u:formatDate(groupOrder.deliveryTime, 'yyyy-MM-dd HH:mm:ss')}</td>--%>
                        <%--                    <td class="center">--%>
                        <%--                        <a style="cursor:pointer;" onclick="moneyTransfer('${sheet.id}')">详情</a>--%>
                        <%--                    </td>--%>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <%@ include file="/WEB-INF/jsp/common/quotePageRefresh.jsp" %>
    </div>
</div>
</body>
</html>