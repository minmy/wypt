<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
    <%@ include file="/WEB-INF/jsp/common/quoteHead.jsp" %>
    <link type="text/css" rel="stylesheet" href="${skin}/css/edit.css"/>
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
            location.href = "${path}/order/orderDetail.action?id=" + id;
        }

        function returnUrl() {
            history.go(-1);
        }
    </script>
</head>
<body class="login_body">
<div class="list-box">
    <div class="list-title">订单列表</div>
    <div class="list-search">
    </div>
    <div class="list-message">
        <div class="list-button">
        </div>
        <table>
            <thead>
            <tr>
                <th width="8%">订单号</th>
                <th width="13%">所属团单</th>
                <th width="10%">联系人</th>
                <th width="13%">所属用户</th>
                <th width="7%">实际付款</th>
                <th width="7%">支付流水号</th>
                <th width="7%">支付状态</th>
                <th width="12%">订单状态</th>
                <th width="8%">操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="orderGood" items="${page.recordList}">
                <tr>
                    <td class="center">${orderGood.order.orderNumber}</td>
                    <td class="center">${orderGood.order.groupOrder.theme}</td>
                    <td class="center">${orderGood.order.contacts}</td>
                    <td class="center">${orderGood.order.weChatUser.name}</td>
                    <td class="center">${orderGood.order.finalPayment}</td>
                    <td class="center">${orderGood.order.payNumber}</td>
                    <td class="center">${orderGood.order.payState.desc}</td>
                    <td class="center">${orderGood.order.orderState.orderStates.desc}</td>
                        <%--                    <td class="center">${u:formatDate(groupOrder.deliveryTime, 'yyyy-MM-dd HH:mm:ss')}</td>--%>
                    <td class="center">
                        <a style="cursor:pointer;" onclick="details('${orderGood.order.id}')">详情</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <%@ include file="/WEB-INF/jsp/common/quotePageRefresh.jsp" %>
    </div>
    <div class="edit-form">
        <div class="edit-button">
            <button type="button" onclick="returnUrl()">返回</button>
        </div>
    </div>
</div>
</body>
</html>