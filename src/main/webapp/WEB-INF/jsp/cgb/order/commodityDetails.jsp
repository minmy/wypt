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
<div class="edit-title">订单商品</div>
<c:forEach items="${orderCommoditys}" var="orderCommodity">
    <div class="edit-box">
        <div class="edit-title">
            <span style="margin-right: 10px">商品名称：${orderCommodity.name}</span>
        </div>
        <div class="edit-form">
            <table>
                <tr>
                    <td class="right" width="10%">商品编号：</td>
                    <td width="40%">${orderCommodity.id}</td>
                    <td class="right" width="10%">商品是否隐藏：</td>
                    <td width="40%">${orderCommodity.isHidden ? "是":"否"}</td>
                </tr>
                <tr>
                    <td class="right" width="10%">所属商家：</td>
                    <td width="40%">${orderCommodity.business.name}</td>
                    <td class="right" width="10%">所属团单：</td>
                    <td width="40%">${orderCommodity.groupOrder.theme}</td>
                </tr>
                <tr>
                    <td class="right" width="10%">原价：</td>
                    <td width="40%">￥${orderCommodity.originalPrice}</td>
                    <td class="right" width="10%">售价：</td>
                    <td width="40%">￥${orderCommodity.price}</td>
                </tr>

                <tr>
                    <td class="right" width="10%">总库存量：</td>
                    <td width="40%">${orderCommodity.totalInventory}</td>
                    <td class="right" width="10%">剩余库存量：</td>
                    <td width="40%">${orderCommodity.remnantInventory}</td>
                </tr>
                <tr>
                    <td class="right" width="10%">所需积分：</td>
                    <td width="40%">${orderCommodity.integral}</td>
                    <td class="right" width="10%">排序：</td>
                    <td width="40%">${orderCommodity.sort}</td>
                </tr>

                <tr>
                    <td class="right" width="10%">上限单数：</td>
                    <td width="40%">${orderCommodity.upperlimit}</td>
                    <td class="right" width="10%">是否可升级商品：</td>
                    <td width="40%">${orderCommodity.isUpgrade ? "是":"否"}</td>
                </tr>
                <c:if test="${orderCommodity.writeOffsNumber != null}">
                    <tr>
                        <td class="right" width="10%">核销次数：</td>
                        <td width="40%">${orderCommodity.writeOffsNumber}</td>
                        <td class="right" width="10%">已核销次数：</td>
                        <td width="40%">${orderCommodity.verifyNumber}</td>
                    </tr>
                </c:if>
                <tr>
                    <td class="right" width="10%">规格说明：</td>
                    <td width="40%">${orderCommodity.description}</td>
                    <td class="right" width="10%">商品详情：</td>
                    <td width="40%">${orderCommodity.details}</td>
                </tr>
                <c:if test="${orderCommodity.annexs.size() > 0}">
                    <tr>
                        <td class="right" width="10%">图片：</td>
                        <td colspan="3">
                            <c:forEach items="${orderCommodity.annexs}" var="annex">
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