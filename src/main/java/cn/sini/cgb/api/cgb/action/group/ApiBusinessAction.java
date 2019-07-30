package cn.sini.cgb.api.cgb.action.group;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.sini.cgb.api.cgb.entity.verification.Business;
import cn.sini.cgb.api.cgb.query.verification.BusinessQuery;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.json.JsonUtils;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 商家action
 * 
 * @author gaowei
 */
@Controller
@RequestMapping("/api/business")
public class ApiBusinessAction {

	/** 商家集合 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public void list(HttpRequestWrapper request, HttpResponseWrapper response) {
		List<Business> businessList = new BusinessQuery().readOnly().list();
		ObjectNode createObjectNode = JsonUtils.createObjectNode();
		ArrayNode businessArrayNode = JsonUtils.createArrayNode();
		for (Business business : businessList) {
			ObjectNode node = JsonUtils.createObjectNode();
			node.put("id", business.getId());
			node.put("name", business.getName());
			businessArrayNode.add(node);
		}
		createObjectNode.set("business", businessArrayNode);
		response.outputJson(0, createObjectNode);
	}

}
