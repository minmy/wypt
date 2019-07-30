package cn.sini.cgb.api.cgb.action.address;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.sini.cgb.admin.identity.entity.address.TaAdministrativeDivision;
import cn.sini.cgb.admin.identity.entity.address.TaAdministrativeDivision.DivisionEnum;
import cn.sini.cgb.admin.identity.query.address.TaAdministrativeDivisionQuery;
import cn.sini.cgb.api.cgb.entity.group.Community;
import cn.sini.cgb.api.cgb.query.group.CommunityQuery;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.json.JsonUtils;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 地址action
 * 
 * @author gaowei
 */
@Controller
@RequestMapping("/api/address")
public class AddressAction {

	/** 查询所有省 */
	@RequestMapping(value = "/getProvinceList", method = RequestMethod.POST)
	public void getProvinceList(HttpRequestWrapper request, HttpResponseWrapper response) {
		List<TaAdministrativeDivision> taAdministrativeDivisionList = new TaAdministrativeDivisionQuery().divisionType(DivisionEnum.province).readOnly().list();
		ObjectNode objectNode = JsonUtils.createObjectNode();
		ArrayNode arrayNode = JsonUtils.createArrayNode();
		for (TaAdministrativeDivision tad : taAdministrativeDivisionList) {
			ObjectNode node = JsonUtils.createObjectNode();
			node.put("code", tad.getCode());
			node.put("name", tad.getFullName());
			arrayNode.add(node);
		}
		objectNode.set("provinces", arrayNode);
		response.outputJson(0, objectNode);
	}

	/** 根据省级code，查询所有省下的行政区域，包括小区 */
	@RequestMapping(value = "/getSubDivisionByCode", method = RequestMethod.POST)
	public void getSubDivisionByCode(HttpRequestWrapper request, HttpResponseWrapper response) {
		String provinceCode = request.getStringMust("provinceCode");
		TaAdministrativeDivision taAdministrativeDivision = new TaAdministrativeDivisionQuery().code(provinceCode).readOnly().uniqueResult();
		ObjectNode objectNode = JsonUtils.createObjectNode();
		// 市一级
		ArrayNode cityArrayNode = JsonUtils.createArrayNode();
		for (TaAdministrativeDivision tad : taAdministrativeDivision.getChildrens()) {
			ObjectNode cityNode = JsonUtils.createObjectNode();
			cityNode.put("code", tad.getCode());
			cityNode.put("name", tad.getFullName());
			// 区一级
			ArrayNode areaArrayNode = JsonUtils.createArrayNode();
			for (TaAdministrativeDivision ta : tad.getChildrens()) {
				ObjectNode areaNode = JsonUtils.createObjectNode();
				areaNode.put("code", ta.getCode());
				areaNode.put("name", ta.getFullName());
				// 街道一级
				Set<TaAdministrativeDivision> childrens = ta.getChildrens();
				if (!childrens.isEmpty()) {
					ArrayNode streetArrayNode = JsonUtils.createArrayNode();
					for (TaAdministrativeDivision t : childrens) {
						ObjectNode streetNode = JsonUtils.createObjectNode();
						streetNode.put("code", t.getCode());
						streetNode.put("name", t.getFullName());
						// 小区一级
						ArrayNode communityArrayNode = JsonUtils.createArrayNode();
						List<Community> list = new CommunityQuery().taAdministrativeDivision(t).list();
						for (Community community : list) {
							ObjectNode communityNode = JsonUtils.createObjectNode();
							communityNode.put("code", community.getId());
							communityNode.put("name", community.getName());
							communityArrayNode.add(communityNode);
						}
						streetNode.set("childrens", communityArrayNode);
						streetArrayNode.add(streetNode);
					}
					areaNode.set("childrens", streetArrayNode);
					areaArrayNode.add(areaNode);
				} else {
					// 小区一级
					ArrayNode communityArrayNode = JsonUtils.createArrayNode();
					List<Community> list = new CommunityQuery().taAdministrativeDivision(ta).list();
					for (Community community : list) {
						ObjectNode communityNode = JsonUtils.createObjectNode();
						communityNode.put("code", community.getId());
						communityNode.put("name", community.getName());
						communityArrayNode.add(communityNode);
					}
					areaNode.set("childrens", communityArrayNode);
					areaArrayNode.add(areaNode);
				}
			}
			cityNode.set("childrens", areaArrayNode);
			cityArrayNode.add(cityNode);
		}
		objectNode.set("childrens", cityArrayNode);
		response.outputJson(0, objectNode);
	}

	/** 查询小区 */
	@RequestMapping(value = "/getCommunityList", method = RequestMethod.POST)
	public void getCommunityList(HttpRequestWrapper request, HttpResponseWrapper response) {
		String code = request.getStringMust("code");
		List<Community> communityList = new CommunityQuery().taAdministrativeDivisionQuery(new TaAdministrativeDivisionQuery().code(code)).list();
		ObjectNode objectNode = JsonUtils.createObjectNode();
		ArrayNode arrayNode = JsonUtils.createArrayNode();
		for (Community community : communityList) {
			ObjectNode node = JsonUtils.createObjectNode();
			node.put("communityId", community.getId());
			node.put("communityName", community.getName());
			arrayNode.add(node);
		}
		response.outputJson(0, objectNode);
	}
}
