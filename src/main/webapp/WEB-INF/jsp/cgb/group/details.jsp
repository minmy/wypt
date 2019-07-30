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
    <div class="edit-title">团单详情</div>
    <form class="edit-form" id="submitForm">
        <table>
            <tr>
                <td class="right" width="20%">团单编号：</td>
                <td width="40%">${groupOrder.id}</td>
                <td class="right" width="10%">团单主题：</td>
                <td width="40%">${groupOrder.theme}</td>
            </tr>
            <tr>
                <td class="right" width="20%">创建时间：</td>
                <td width="40%">${groupOrder.createTime}</td>
                <td class="right" width="10%">主题介绍：</td>
                <td width="40%">${groupOrder.themeIntroduce}</td>
            </tr>
            <tr>
                <td class="right" width="20%">拼团须知：</td>
                <td width="40%">${groupOrder.notice}</td>
                <td class="right" width="10%">热度：</td>
                <td width="40%">${groupOrder.heatDegree}</td>
            </tr>
            <tr>
                <td class="right" width="20%">开始时间：</td>
                <td width="40%">${groupOrder.beginTime}</td>
                <td class="right" width="10%">结束时间：</td>
                <td width="40%">${groupOrder.endTime}</td>
            </tr>
            <tr>
                <td class="right" width="20%">审核状态：</td>
                <td width="40%">${groupOrder.reviewStates}</td>
                <td class="right" width="10%">审核时间：</td>
                <td width="40%">${groupOrder.reviewTime}</td>
            </tr>
            <tr>
                <td class="right" width="20%">审核意见：</td>
                <td width="40%">${groupOrder.reviewReason}</td>
                <td class="right" width="10%">发布时间：</td>
                <td width="40%">${groupOrder.releaseTime}</td>
            </tr>
            <tr>
                <td class="right" width="20%">自提地址：</td>
                <td width="40%">${groupOrder.selfExtractingAddress}</td>
                <td class="right" width="10%">自提时间：</td>
                <td width="40%">${groupOrder.selfExtractingTime}</td>
            </tr>
            <tr>
                <td class="right" width="20%">自提结束时间：</td>
                <td width="40%">${groupOrder.selfExtractingEndTime}</td>
                <td class="right" width="10%">取消时间：</td>
                <td width="40%">${groupOrder.cancelTime}</td>
            </tr>
            <tr>
                <td class="right" width="20%">取消原因：</td>
                <td width="40%">${groupOrder.cancelReason}</td>
                <td class="right" width="10%">发货时间：</td>
                <td width="40%">${groupOrder.deliveryTime}</td>
            </tr>
            <tr>
                <td class="right" width="20%">可提现时间：</td>
                <td width="40%">${groupOrder.cashWithdrawalTime}</td>
                <td class="right" width="10%">浏览量：</td>
                <td width="40%">${groupOrder.browseVolume}</td>
            </tr>
            <tr>
                <td class="right" width="20%">团单状态：</td>
                <td width="40%">${groupOrder.groupOrderState.states.desc}</td>
                <td class="right" width="10%">所属用户：</td>
                <td width="40%">${groupOrder.weChatUser.openId}</td>
            </tr>
            <tr>
                <td class="right" width="20%">是否完成发货并结算金额：</td>
                <td width="40%">${groupOrder.isFinish ? "是":"否"}</td>
                <td class="right" width="10%">所属小区：</td>
                <td width="40%">${groupOrder.community.name}</td>
            </tr>
            <tr>
                <td class="right" width="20%">联系电话：</td>
                <td width="40%">${groupOrder.phone}</td>
                <td class="right" width="10%">省：</td>
                <td width="40%">${groupOrder.province}</td>
            </tr>
            <tr>
                <td class="right" width="20%">市：</td>
                <td width="40%">${groupOrder.city}</td>
                <td class="right" width="10%">区：</td>
                <td width="40%">${groupOrder.townships}</td>
            </tr>
            <tr>
                <td class="right" width="20%">街道：</td>
                <td width="40%">${groupOrder.street}</td>
                <td class="right" width="10%">等待时间：</td>
                <td width="40%">${groupOrder.waitingTime}</td>
            </tr>
            <tr>
                <td class="right" width="20%">是否修改过发货时间：</td>
                <td width="40%">${groupOrder.isDeliveryTime ? "是":"否"}</td>
                <td class="right" width="10%">团单类型：</td>
                <td width="40%">${groupOrder.groupType.desc}</td>
            </tr>
            <tr>
                <td class="right" width="20%">是否组合商品：</td>
                <td width="40%">${groupOrder.isCombination ? "是":"否"}</td>
                <td class="right" width="10%">团单是否置顶：</td>
                <td width="40%">${groupOrder.isTop ? "是":"否"}</td>
            </tr>
            <tr>
                <td class="right" width="20%">是否可升级商品：</td>
                <td width="40%">${groupOrder.isUpgrade ? "是":"否"}</td>
                <td class="right" width="10%">是否允许退款：</td>
                <td width="40%">${groupOrder.isRefund ? "是":"否"}</td>
            </tr>
            <tr>
                <td class="right" width="20%">收货方式：</td>
                <td width="40%">
                    <c:forEach items="${groupOrder.wayOfDeliverys}" var="wayOfDelivery">
                        ${wayOfDelivery.deliveryType.desc}
                    </c:forEach>
                </td>
            </tr>
        </table>
    </form>
</div>
<div class="edit-title">团单商品</div>
<c:forEach items="${groupOrder.groupCommoditys}" var="groupCommodity">
    <div class="edit-box">
        <div class="edit-title">
            <span style="margin-right: 10px">商品名称：${groupCommodity.name}</span>
        </div>
        <div class="edit-form">
            <table>
                <tr>
                    <td class="right" width="10%">所属商家：</td>
                    <td width="40%">${groupCommodity.business.name}</td>
                    <td class="right" width="10%">所属团单：</td>
                    <td width="40%">${groupCommodity.groupOrder.theme}</td>
                </tr>
                <tr>
                    <td class="right" width="10%">原价：</td>
                    <td width="40%">￥${groupCommodity.originalPrice}</td>
                    <td class="right" width="10%">售价：</td>
                    <td width="40%">￥${groupCommodity.price}</td>
                </tr>

                <tr>
                    <td class="right" width="10%">总库存量：</td>
                    <td width="40%">${groupCommodity.totalInventory}</td>
                    <td class="right" width="10%">剩余库存量：</td>
                    <td width="40%">${groupCommodity.remnantInventory}</td>
                </tr>
                <tr>
                    <td class="right" width="10%">所需积分：</td>
                    <td width="40%">${groupCommodity.integral}</td>
                    <td class="right" width="10%">排序：</td>
                    <td width="40%">${groupCommodity.sort}</td>
                </tr>

                <tr>
                    <td class="right" width="10%">上限单数：</td>
                    <td width="40%">${groupCommodity.upperlimit}</td>
                    <td class="right" width="10%">是否可升级商品：</td>
                    <td width="40%">${groupCommodity.isUpgrade ? "是":"否"}</td>
                </tr>
                <tr>
                    <td class="right" width="10%">核销次数：</td>
                    <td width="40%">${groupCommodity.writeOffsNumber}</td>
                    <td class="right" width="10%">商品是否隐藏：</td>
                    <td width="40%">${groupCommodity.isHidden ? "是":"否"}</td>
                </tr>
                <tr>
                    <td class="right" width="10%">规格说明：</td>
                    <td width="40%">${groupCommodity.description}</td>
                    <td class="right" width="10%">商品详情：</td>
                    <td width="40%">${groupCommodity.details}</td>
                </tr>
                <c:if test="${groupCommodity.annexs.size() > 0}">
                    <tr>
                        <td class="right" width="10%">图片：</td>
                        <td colspan="3">
                            <c:forEach items="${groupCommodity.annexs}" var="annex">
                                <img src="${skin}${annex.filePath}"
                                     onclick="showImg('${annex.id}')"
                                     height="150px"/>
                                <img src="${skin}${annex.filePath}"
                                     id="${annex.id}"
                                     style="display: none"/>
                            </c:forEach>
                        </td>
                    </tr>
                </c:if>
            </table>
        </div>
    </div>
</c:forEach>

<div class="edit-box">
    <div class="edit-form">
        <div class="edit-button">
            <button type="button" onclick="returnUrl()">返回</button>
        </div>
    </div>
</div>
</body>
</html>