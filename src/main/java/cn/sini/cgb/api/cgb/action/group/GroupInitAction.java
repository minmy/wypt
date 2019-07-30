package cn.sini.cgb.api.cgb.action.group;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.sini.cgb.admin.identity.query.AuthorityQuery;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState;
import cn.sini.cgb.api.cgb.entity.group.Label;
import cn.sini.cgb.api.cgb.entity.group.OrderState;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState.States;
import cn.sini.cgb.api.cgb.entity.group.OrderState.OrderStates;
import cn.sini.cgb.api.cgb.entity.group.ProductsSources;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;

/**
 * 我要拼团初始化数据Action
 * 
 * @author gaowei
 */
@Controller
@RequestMapping("/group/init")
public class GroupInitAction {

	/** 初始化入口 */
	@Transactional
	@RequestMapping("/index")
	public void index(HttpRequestWrapper request, HttpResponseWrapper response) {
		if (new AuthorityQuery().count() == 0) {
			createGroupOrderState();
			createOrderState();
			createLabel();
			createProductsSources();
		}
		response.outputJson(true);
	}

	/** 创建产品来源 */
	public void createProductsSources() {
		ProductsSources productsSources = new ProductsSources();
		productsSources.setName("自营");
		productsSources.setRemarks("由平台组织的团购业务");
		productsSources.setSort(10L);
		productsSources.saveOrUpdate();

		productsSources = new ProductsSources();
		productsSources.setName("团营");
		productsSources.setRemarks("由团长自行组织的团购业务");
		productsSources.setSort(20L);
		productsSources.saveOrUpdate();
	}

	/** 创建标签 */
	public void createLabel() {
		Label label = new Label();
		label.setTag("特性");
		label.setSort(10L);
		label.saveOrUpdate();

		Label subLabel = new Label();
		subLabel.setTag("亲民价");
		subLabel.setSuperLabel(label);
		subLabel.saveOrUpdate();

		subLabel = new Label();
		subLabel.setTag("老友价");
		subLabel.setSuperLabel(label);
		subLabel.saveOrUpdate();

		subLabel = new Label();
		subLabel.setTag("尝一尝");
		subLabel.setSuperLabel(label);
		subLabel.saveOrUpdate();

		label = new Label();
		label.setTag("来源");
		label.setSort(20L);
		label.saveOrUpdate();

		subLabel = new Label();
		subLabel.setTag("直供");
		subLabel.setDesc_("平台提供货源");
		subLabel.setSuperLabel(label);
		subLabel.saveOrUpdate();

		subLabel = new Label();
		subLabel.setTag("自荐");
		subLabel.setDesc_("团长提供货源");
		subLabel.setSuperLabel(label);
		subLabel.saveOrUpdate();

		subLabel = new Label();
		subLabel.setTag("自产");
		subLabel.setDesc_("团长自有企业、农场");
		subLabel.setSuperLabel(label);
		subLabel.saveOrUpdate();

		subLabel = new Label();
		subLabel.setTag("自制");
		subLabel.setDesc_("自己生产、制作");
		subLabel.setSuperLabel(label);
		subLabel.saveOrUpdate();
	}

	/** 创建团单状态 */
	public void createGroupOrderState() {
		GroupOrderState groupOrderState = new GroupOrderState();
		groupOrderState.setStates(States.DFB);
		groupOrderState.setDesc(States.DFB.getDesc());
		groupOrderState.setRemove(false);
		groupOrderState.saveOrUpdate();

		groupOrderState = new GroupOrderState();
		groupOrderState.setStates(States.JXZ);
		groupOrderState.setDesc(States.JXZ.getDesc());
		groupOrderState.setRemove(false);
		groupOrderState.saveOrUpdate();

		groupOrderState = new GroupOrderState();
		groupOrderState.setStates(States.WCT);
		groupOrderState.setDesc(States.WCT.getDesc());
		groupOrderState.setRemove(false);
		groupOrderState.saveOrUpdate();

		groupOrderState = new GroupOrderState();
		groupOrderState.setStates(States.YJS);
		groupOrderState.setDesc(States.YJS.getDesc());
		groupOrderState.setRemove(false);
		groupOrderState.saveOrUpdate();
	}

	/** 创建订单状态 */
	public void createOrderState() {
		OrderState orderState = new OrderState();
		orderState.setOrderStates(OrderStates.DFK);
		orderState.setRemove(false);
		orderState.setDesc(OrderStates.DFK.getDesc());
		orderState.saveOrUpdate();

		orderState = new OrderState();
		orderState.setOrderStates(OrderStates.DSH);
		orderState.setRemove(false);
		orderState.setDesc(OrderStates.DSH.getDesc());
		orderState.saveOrUpdate();

		orderState = new OrderState();
		orderState.setOrderStates(OrderStates.YWC);
		orderState.setRemove(false);
		orderState.setDesc(OrderStates.YWC.getDesc());
		orderState.saveOrUpdate();

		orderState = new OrderState();
		orderState.setOrderStates(OrderStates.YQX);
		orderState.setRemove(false);
		orderState.setDesc(OrderStates.YQX.getDesc());
		orderState.saveOrUpdate();
	}
}
