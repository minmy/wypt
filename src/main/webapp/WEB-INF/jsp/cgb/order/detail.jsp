<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
    <%@ include file="/WEB-INF/jsp/common/quoteHead.jsp" %>
    <link type="text/css" rel="stylesheet" href="${skin}/css/edit.css"/>
    <link type="text/css" rel="stylesheet" href="${skin}/css/list.css"/>
    <head>
        <script type="text/javascript">
            function returnUrl() {
                history.go(-1);
            }
            function showImg(id) {
                layer.open({
                    type: 1,
                    title: false,
                    closeBtn: 0,
                    area: [$('#' + id).width()+'px', $('#' + id).height()+'px'],
                    skin: 'layui-layer-nobg', //没有背景色
                    shadeClose: true,
                    content: $('#' + id),
                });
            }
        </script>
    </head>
<body class="login_body">
<div class="edit-box">
    <div class="edit-title">订单详情</div>
    <form class="edit-form" id="submitForm">
        <table>
            <tr>
                <td class="right" width="20%">订单号：</td>
                <td width="40%">${order.orderNumber}</td>
                <td class="right" width="10%">手机号：</td>
                <td width="40%">${order.phone}</td>
            </tr>
            <tr>
                <td class="right" width="20%">联系人：</td>
                <td width="40%">${order.contacts}</td>
                <td class="right" width="10%">详细地址：</td>
                <td width="40%">${order.address}</td>
            </tr>
            <tr>
                <td class="right" width="20%">备注：</td>
                <td width="40%">${order.remarks}</td>
                <td class="right" width="10%">订单状态：</td>
                <td width="40%">${order.orderState.orderStates.desc}</td>
            </tr>
            <tr>
                <td class="right" width="20%">总价：</td>
                <td width="40%">${order.total}</td>
                <td class="right" width="10%">运费：</td>
                <td width="40%">${order.freight}</td>
            </tr>
            <tr>
                <td class="right" width="20%">优惠金额：</td>
                <td width="40%">${order.discount}</td>
                <td class="right" width="10%">实际付款：</td>
                <td width="40%">${order.finalPayment}</td>
            </tr>
            <tr>
                <td class="right" width="20%">所属用户：</td>
                <td width="40%">${order.weChatUser.name}</td>
                <td class="right" width="10%">支付方式：</td>
                <td width="40%">${order.payMethod}</td>
            </tr>
            <tr>
                <td class="right" width="20%">支付流水号：</td>
                <td width="40%">${order.payNumber}</td>
                <td class="right" width="10%">完成支付时间：</td>
                <td width="40%">${u:formatDate(order.payTime, 'yyyy-MM-dd HH:mm:ss')}</td>
            </tr>
            <tr>
                <td class="right" width="20%">支付状态：</td>
                <td width="40%">${order.payState.desc}</td>
                <td class="right" width="10%">收货时间：</td>
                <td width="40%">${u:formatDate(order.receivingTime, 'yyyy-MM-dd HH:mm:ss')}</td>
            </tr>
            <tr>
                <td class="right" width="20%">取消时间：</td>
                <td width="40%">${u:formatDate(order.cancelTime, 'yyyy-MM-dd HH:mm:ss')}</td>
                <td class="right" width="10%">取消原因：</td>
                <td width="40%">${order.cancelReason}</td>
            </tr>
            <tr>
                <td class="right" width="20%">配送日期：</td>
                <td width="40%">${order.deliveryTime}</td>
                <td class="right" width="10%">关闭时间：</td>
                <td width="40%">${u:formatDate(order.closingTime, 'yyyy-MM-dd HH:mm:ss')}</td>
            </tr>
            <tr>
                <td class="right" width="20%">退款时间：</td>
                <td width="40%">${u:formatDate(order.refundTime, 'yyyy-MM-dd HH:mm:ss')}</td>
                <td class="right" width="10%">所属团单：</td>
                <td width="40%">${order.groupOrder.theme}</td>
            </tr>
            <tr>
                <td class="right" width="20%">退款流水：</td>
                <td width="40%">${order.refundNo}</td>
                <td class="right" width="10%">是否恢复库存：</td>
                <td width="40%">${order.isRecovery ? "是":"否"}</td>
            </tr>
            <tr>
                <td class="right" width="20%">prepayId：</td>
                <td width="40%">${order.prepayId}</td>
                <td class="right" width="10%">统一支付时间：</td>
                <td width="40%">${u:formatDate(order.uinionPaytime, 'yyyy-MM-dd HH:mm:ss')}</td>
            </tr>
            <tr>
                <td class="right" width="20%">积分流水号：</td>
                <td width="40%">${order.integralNumber}</td>
                <td class="right" width="10%">分享人：</td>
                <td width="40%">${order.originOpenId}</td>
            </tr>
            <tr>
                <td class="right" width="20%">订单用户的提货照：</td>
                <td width="40%" colspan="3">
                    <c:forEach var="annex" items="${order.annexs}">
                        <img src="${skin}${annex.filePath}"
                             onclick="showImg('${annex.id}')"
                             height="150px"/>
                        <img src="${skin}${annex.filePath}"
                             id="${annex.id}"
                             style="display: none"/>
                    </c:forEach>
                </td>
            </tr>
        </table>
        <div class="edit-box">
            <div class="edit-button">
                <button type="button" onclick="returnUrl()">返回</button>
            </div>
        </div>
    </form>
</div>
</body>
</html>