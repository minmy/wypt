<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
    <%@ include file="/WEB-INF/jsp/common/quoteHead.jsp" %>
    <link type="text/css" rel="stylesheet" href="${skin}/css/list.css"/>
    <link type="text/css" rel="stylesheet" href="${skin}/css/edit.css"/>
    <script type="text/javascript">
        //使用$(functioin(){})会报错，因为没加载完layer就执行了
        window.onload = function () {
            if ($("#randomNumber").val()) {
                search();
            }
        }

        function search() {
            $("#detail").html('');
            $.ajax({
                url: '${path}/verify/queryOrder.action?${_csrf.parameterName}=${_csrf.token}',
                method: "POST",
                data: {
                    randomNumber: $("#randomNumber").val()
                }
            }).then(function (res) {
                if (res.status == -1) {
                    alertError(res.message);
                    return
                }
                queryOrder(res.data)
            })
        }

        function queryOrder(data) {
            console.log(data.background)
            $.ajax({
                url: '${path}/verification/queryorder.action',
                method: "POST",
                contentType: 'application/json',
                data: JSON.stringify(data),
            }).then(function (res) {
                if (res.status != 0 && res.status != 20012) {
                    alertError(res.message);
                    return
                }
                $("#detail").html(pack(res.data, res.status, data.mchId))
            })
        }

        function getVerifyParam() {
            var commodityNumber = $('input:radio:checked').val()
            if (!commodityNumber) {
                alertError('请选择核销商品');
                return
            }
            $.ajax({
                url: '${path}/verify/getVerifyParam.action?${_csrf.parameterName}=${_csrf.token}',
                method: "POST",
                data: {
                    commodityNumber: commodityNumber
                }
            }).then(function (res) {
                if (res.status == -1) {
                    alertError(res.message);
                    return
                }
                verify(res.data)
            })
        }

        function verify(data) {
            $.ajax({
                url: '${path}/verification/writeoff.action',
                method: "POST",
                contentType: 'application/json',
                data: JSON.stringify(data),
            }).then(function (res) {
                console.log(res)
                if (res.status != 0) {
                    alertError(res.message);
                    return
                }
                msgSuccess(res.message, function () {

                })

            })
        }

        function verificationList(orderNumber, mchId) {
            location.href = '${path}/verify/sheetList.action?orderNumber=' + orderNumber + '&mchId=' + mchId + '&fromOther=true'
        }

        function cancel() {
            $("#randomNumber").val('')
            $("#detail").html('');
        }

        function pack(data, status, mchId) {
            var button = status == 0 ? '<button type="button" onclick="getVerifyParam()" >核销</button>' : '';
            return ' <div class="edit-title">订单详情</div>\n' +
                '    <div class="edit-form">\n' +
                '        <table>' +
                '           <tr>\n' +
                '                <td class="right" width="20%">拼团主题：</td>\n' +
                '                <td width="40%">' + data.groupOrderName + '</td>\n' +
                '                <td class="right" width="10%">核销状态：</td>\n' +
                '                <td width="40%">' + (status == 0 ? '未核销' : '已核销') + '</td>\n' +
                '            </tr>\n' +
                '            <tr>\n' +
                '                <td class="right" width="20%">参团人：</td>\n' +
                '                <td width="40%">' + data.consumer + '</td>\n' +
                '                <td class="right" width="10%">参团时间：</td>\n' +
                '                <td width="40%">' + data.joinDate + '</td>\n' +
                '            </tr>\n' +
                '            <tr>\n' +
                '                <td class="right" width="20%">商家名：</td>\n' +
                '                <td width="40%">' + data.businessName + '</td>\n' +
                '                <td class="right" width="10%">核销人：</td>\n' +
                '                <td width="40%">' + data.currentVerificationer + '</td>\n' +
                '            </tr>\n' +
                '            <tr>\n' +
                '                <td class="right" width="20%">核销时间：</td>\n' +
                '                <td width="40%">' + data.currentDate + '</td>\n' +
                '            </tr>\n' + packCommodity(data.orderList, status) +
                '        </table>\n' +
                '        <div class="edit-box">\n' +
                '            <div class="edit-button">\n' + button +
                '                <button style="width: 96px" type="button" onclick="verificationList(\'' + data.orderNumber + '\',' + mchId + ')" >查看核销记录</button>\n' +
                '                <button type="button" onclick="cancel()" >取消</button>\n' +
                '            </div>\n' +
                '        </div>\n' +
                '    </div>'
        }

        function packCommodity(list, status) {
            var commodity = ''
            list.forEach(function (data) {
                commodity += '<div>\n' +
                    '            <input type="radio" name="commodityNumber" value="' + data.commodityNumber + '">\n' +
                    '            <img src="' + data.pic + '" height="200px">\n' +
                    '            <span>' + data.name + '</span>\n'
                if (status == 20012) {
                    commodity += '   <span>' + data.serialNumber + '</span>\n' +
                        '            <span>' + data.verificationer + '</span>\n' +
                        '            <span>' + data.verificationDate + '</span>\n'
                }
                commodity += '          </div>';
            })
            return '         <tr>\n' +
                '                <td class="right" width="20%">商品：</td>\n' +
                '                <td colspan="3">\n' + commodity +
                '                </td>\n' +
                '            </tr>'
        }
    </script>
</head>
<body class="login_body">
<div class="list-box">
    <div class="list-title">
        订单查询
    </div>
    <div class="list-search">
        订单查询号码 <input type="text" name="randomNumber" value="" id="randomNumber"/>
        <button class="list-search-button" onclick="search()">搜索</button>
    </div>

</div>
<div class="edit-box" id="detail">

</div>
</body>
</html>