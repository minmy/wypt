<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<%@ include file="/WEB-INF/jsp/common/quoteHead.jsp"%>
<link type="text/css" rel="stylesheet" href="${skin}/css/edit.css" />
<script type="text/javascript">
	$(document).ready(function() {
		$("#submitForm").Validform({
			tipSweep : true,
			tiptype : tiptypeRight,
			beforeSubmit : function(form) {
				$.ajax({
					url : "${path}/address/community/saveCommunity.action?${_csrf.parameterName}=${_csrf.token}",
					type : "POST",
					data : $("#submitForm").serialize(),
					success : function(result) {
						if (result.status) {
							msgSuccess("保存成功", function() {
								returnUrl();
							});
						} else {
							alertError(result.message);
						}
					}
				});
				return false;
			}
		});
	});

	function returnUrl() {
		history.go(-1);
	}

	//获取子级行政区划
    function loadDivision(type, childType) {
	    var id = $('#'+type).val();
	    $.ajax({
            url: '${path}/address/division/getSubDivisionById.action?${_csrf.parameterName}=${_csrf.token}',
            data : {id : id},
            type : 'post',
            dataType : 'json',
            success : function (data) {
                if (data.success) {
                    $('#'+childType).empty();
                    var s = "";
                    if (childType == 'city') {
                        s = '<option value="">请选择市级</option>';
                    } else if (childType == 'county') {
                        s = '<option value="">请选择区/县级</option>';
                    } else if (childType == 'town') {
                        s = '<option value="">请选择镇/街道级</option>';
                    }
                    $('#'+childType).append(s);
                    $.each(data.childDivisions, function (i,o) {
                        $('#'+childType).append('<option value="'+o.id+'">'+o.fullName+'</option>')
                    });
                }
            }

        })
    }
</script>
</head>
<body class="login_body">
	<div class="edit-box">
		<div class="edit-title">${empty community ? "新增" : "编辑"}小区</div>
		<form class="edit-form" id="submitForm">
			<input type="hidden" name="id" value="${community.id}" />
			<table>
				<tr>
					<td class="right" width="10%"><span class="required">*</span>小区名称：</td>
					<td width="40%">
						<input type="text" name="name" value="${community.name}" maxlength="30" datatype="*" nullmsg="请输入小区名称" />
					</td>
					<td class="right" width="10%">排序序号：</td>
					<td width="40%">
						<input type="text" name="sort" value="${community.sort}"/>
					</td>
				</tr>
				<tr>
					<td class="right">上级区划：</td>
					<td>
                        <c:set var="defaultProvince" value=""/>
                        <c:set var="defaultCity" value=""/>
                        <c:set var="defaultCounty" value=""/>
                        <c:set var="defaultTown" value=""/>
                        <c:set var="division" value="${community.town}"/>
                        <c:if test="${not empty division}">
                            <c:choose>
                                <c:when test="${division.divisionType.name() == 'city'}">
                                    <c:set var="defaultProvince" value="${division.parentDivision.id}"/>
                                    <c:set var="defaultCity" value="${division.id}"/>
                                </c:when>
                                <c:when test="${division.divisionType.name() == 'area_or_county'}">
                                    <c:set var="defaultProvince" value="${division.parentDivision.parentDivision.id}"/>
                                    <c:set var="defaultCity" value="${division.parentDivision.id}"/>
                                    <c:set var="defaultCounty" value="${division.id}"/>
                                </c:when>
                                <c:when test="${division.divisionType.name() == 'town_or_street'}">
                                    <c:set var="defaultProvince" value="${division.parentDivision.parentDivision.parentDivision.id}"/>
                                    <c:set var="defaultCity" value="${division.parentDivision.parentDivision.id}"/>
                                    <c:set var="defaultCounty" value="${division.parentDivision.id}"/>
                                    <c:set var="defaultTown" value="${division.id}"/>
                                </c:when>
                            </c:choose>
                        </c:if>
                        <select id="province" name="provinceId" defaultValue="${defaultProvince}" onchange="loadDivision('province','city')">
                            <option value="">请选择省级</option>
                            <c:forEach items="${provinceList}" var="province" >
                                <option value="${province.id}">${province.fullName}</option>
                            </c:forEach>
                        </select>
                        <select id="city" name="cityId" defaultValue="${defaultCity}" onchange="loadDivision('city','county')">
                            <option value="">请选择市级</option>
                            <c:forEach items="${cityList}" var="city" >
                                <option value="${city.id}">${city.fullName}</option>
                            </c:forEach>
                        </select>
                        <select id="county" name="countyId" defaultValue="${defaultCounty}" onchange="loadDivision('county', 'town')">
                            <option value="">请选择区/县级</option>
                            <c:forEach items="${countyList}" var="county">
                                <option value="${county.id}">${county.fullName}</option>
                            </c:forEach>
                        </select>
                        <select id="town" name="townId" defaultValue="${defaultTown}">
                            <option value="">请选择镇/街道</option>
                            <c:forEach items="${townList}" var="town">
                                <option value="${town.id}">${town.fullName}</option>
                            </c:forEach>
                        </select>
                    </td>
				</tr>
                <tr>
                    <td class="right">备注：</td>
                    <td>
                        <textarea name="remarks" rows="3" cols="80">${community.remarks}</textarea>
                    </td>
                    <td class="right"></td>
                    <td>
                    </td>
                </tr>
			</table>
			<div class="edit-button">
				<button type="submit">保存</button>
				<button type="button" onclick="returnUrl()">返回</button>
			</div>
		</form>
	</div>
</body>
</html>