package cn.sini.cgb.api.cgb.action.group;

import cn.sini.cgb.api.cgb.entity.group.*;
import cn.sini.cgb.api.cgb.query.group.*;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.json.JsonUtils;
import cn.sini.cgb.common.query.Page;
import cn.sini.cgb.common.util.DateTimeUtils;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.LockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 分享劵action
 *
 * @author lijianxin
 */
@Controller
@RequestMapping("/api/shareticket")
public class ShareTicketAction {
    private Logger logger = LoggerFactory.getLogger(ShareTicketAction.class);

    /**
     * 团长查看分享劵
     */
    @RequestMapping("colonel")
    public void queryByColonel(HttpRequestWrapper request, HttpResponseWrapper response) {
        String openId = request.getStringMust("openId");
        WeChatUser weChatUser = new WeChatUserQuery().openId(openId).readOnly().uniqueResult();
        if (null == weChatUser) {
            response.outputJson(-1, "openId错误");
            return;
        }
        GroupOrder groupOrder = new GroupOrderQuery().id(request.getLongMust("groupOrderId")).readOnly().uniqueResult();
        if (null == groupOrder) {
            response.outputJson(-1, "groupOrderId错误");
            return;
        }
        //判断是否为团长
        if (groupOrder.getWeChatUser() != weChatUser) {
            response.outputJson(-1, "此用户非本团团长");
            return;
        }
        //全部or可兑换or已兑换
        Integer discountNumber = null;
        ShareTicketDetailQuery shareTicketDetailQuery = null;
        String type = request.getString("type");
        if (StringUtils.equals(type, "CANCHANGE")) {
            discountNumber = groupOrder.getDiscountNumber();
        } else if (StringUtils.equals(type, "HASCHANGED")) {
            shareTicketDetailQuery = new ShareTicketDetailQuery().changeType(ShareTicketDetail.ChangeType.EXCHANGE);
        }
        Page<ShareTicket> page = new ShareTicketQuery().groupOrder(groupOrder).enableExchange(discountNumber).orderByAmount(request.getBoolean("asc"))
                .shareTicketDetailQuery(shareTicketDetailQuery).readOnly().pageHasCount(request.getPageNum(), request.getPageSize());
        ArrayNode shareTickets = JsonUtils.createArrayNode();
        page.getRecordList().forEach(shareTicket -> {
            ObjectNode objectNode = JsonUtils.createObjectNode();
            Set<GroupCommodity> groupCommodities = shareTicket.getGroupOrder().getGroupCommoditys();
            objectNode.put("contacts", shareTicket.getOwner().getContacts().substring(0, 1) + "**");
            objectNode.put("headImgUrl", shareTicket.getOwner().getHeadImgUrl());
            objectNode.put("amount", shareTicket.getAmount());
            objectNode.put("shareTicketId", shareTicket.getId());
            if (!groupCommodities.isEmpty()) {
                objectNode.put("commodityName", groupCommodities.iterator().next().getName());
            }
            ShareTicketDetail detail = new ShareTicketDetailQuery().shareTicket(shareTicket).changeType(ShareTicketDetail.ChangeType.EXCHANGE)
                    .orderBy("createTime", false).readOnly().firstResult();
            if (null != detail) {
                objectNode.put("date", DateTimeUtils.format(detail.getCreateTime(), "yyyy-MM-dd HH:mm"));
            }
            shareTickets.add(objectNode);
        });
        ObjectNode data = JsonUtils.createObjectNode();
        data.put("pageNum", page.getPageNum());
        data.put("pageSize", page.getPageSize());
        data.put("totalPage", page.getTotalPage());
        data.put("totalRecord", page.getTotalRecord());
        data.put("theme", groupOrder.getTheme());
        data.put("discountNumber", groupOrder.getDiscountNumber());
        data.put("shareDesc", groupOrder.getShareCouponInstructions());
        data.set("shareTickets", shareTickets);
        response.outputJson(0, data);
    }

    /**
     * 团长兑换分享劵
     */
    @Transactional
    @RequestMapping("exchange")
    public void exchange(HttpRequestWrapper request, HttpResponseWrapper response) {
        String openId = request.getStringMust("openId");
        //团长
        WeChatUser weChatUser = new WeChatUserQuery().openId(openId).readOnly().uniqueResult();
        if (null == weChatUser) {
            response.outputJson(-1, "openId错误");
            return;
        }
        ShareTicket shareTicket = new ShareTicketQuery().id(request.getLongMust("shareTicketId")).uniqueResult();
        if (null == shareTicket) {
            response.outputJson(-1, "shareTicketId错误");
            return;
        }
        GroupOrder groupOrder = new GroupOrderQuery().id(request.getLongMust("groupOrderId")).readOnly().uniqueResult();
        if (null == groupOrder) {
            response.outputJson(-1, "groupOrderId错误");
            return;
        }
        if (weChatUser != groupOrder.getWeChatUser()) {
            response.outputJson(-1, "对不起，你非本团团长");
            return;
        }
        if (groupOrder != shareTicket.getGroupOrder()) {
            response.outputJson(-1, "对不起，分享劵不属于此团单");
            return;
        }
        if (groupOrder.getGroupOrderState().getStates() == GroupOrderState.States.YJS) {
            response.outputJson(-1, "对不起，团单已结束");
            return;
        }
        if (shareTicket.getAmount() < groupOrder.getDiscountNumber()) {
            response.outputJson(-1, "对不起，分享劵数不足");
            return;
        }
        //是否已兑换过
        if (new ShareTicketDetailQuery().shareTicket(shareTicket).changeType(ShareTicketDetail.ChangeType.EXCHANGE).readOnly().count() > 0) {
            response.outputJson(-1, "对不起，此顾客已兑换过商品");
            return;
        }
        saveShareTicketAndDetail(shareTicket.getOwner(), weChatUser, groupOrder, null, null, ShareTicketDetail.ChangeType.EXCHANGE, -groupOrder.getDiscountNumber());
        response.outputJson(0, "兑换成功");
    }

    //团长也可以查看明细
    @RequestMapping("detailbycolonel")
    public void detailbycolonel(HttpRequestWrapper request, HttpResponseWrapper response) {
        //判断openId
        WeChatUser weChatUser = new WeChatUserQuery().openId(request.getStringMust("openId")).readOnly().uniqueResult();
        if (null == weChatUser) {
            response.outputJson(-1, "openId错误");
            return;
        }
        ShareTicket shareTicket = new ShareTicketQuery().id(request.getLongMust("shareTicketId")).readOnly().uniqueResult();
        if (null == shareTicket) {
            response.outputJson(-1, "shareTicketId错误");
            return;
        }
        if (shareTicket.getGroupOrder().getWeChatUser() != weChatUser) {
            response.outputJson(-1, "你非本团团长，无法查看分享劵明细记录");
            return;
        }
        List<ShareTicketDetail> list = new ShareTicketDetailQuery().shareTicket(shareTicket).readOnly().list();
        ArrayNode shareTicketDetails = JsonUtils.createArrayNode();
        list.forEach(shareTicketDetail -> {
            ObjectNode objectNode = JsonUtils.createObjectNode();
            if (shareTicketDetail.getChangeType() == ShareTicketDetail.ChangeType.EXCHANGE) {
                objectNode.put("contacts", shareTicket.getOwner().getContacts().substring(0,1)+"**");
                objectNode.put("headImgUrl", shareTicket.getOwner().getHeadImgUrl());
            } else {
                objectNode.put("contacts", shareTicketDetail.getRelatedUser().getContacts().substring(0,1)+"**");
                objectNode.put("headImgUrl", shareTicketDetail.getRelatedUser().getHeadImgUrl());
            }
            objectNode.put("type", shareTicketDetail.getGetWay() != null ? shareTicketDetail.getGetWay().getDesc() : shareTicketDetail.getChangeType().getDesc());
            objectNode.put("data", DateTimeUtils.format(shareTicketDetail.getCreateTime(), "yyyy-MM-dd HH:mm"));
            objectNode.put("amount", shareTicketDetail.getAmount());
            shareTicketDetails.add(objectNode);
        });
        response.outputJson(0, shareTicketDetails);
    }

    /**
     * 自己查看分享劵
     */
    @RequestMapping("owner")
    public void queryByOwner(HttpRequestWrapper request, HttpResponseWrapper response) {
        String openId = request.getStringMust("openId");
        WeChatUser weChatUser = new WeChatUserQuery().openId(openId).readOnly().uniqueResult();
        if (null == weChatUser) {
            response.outputJson(-1, "openId错误");
            return;
        }
        Page<ShareTicket> page = new ShareTicketQuery().owner(weChatUser).orderBy("updateTime", false).readOnly().pageHasCount(request.getPageNum(), request.getPageSize());
        ArrayNode shareTickets = JsonUtils.createArrayNode();
        page.getRecordList().forEach(shareTicket -> {
            ObjectNode objectNode = JsonUtils.createObjectNode();
            Date date = shareTicket.getUpdateTime() == null ? shareTicket.getCreateTime() : shareTicket.getUpdateTime();
            objectNode.put("groupOrderName", shareTicket.getGroupOrder().getTheme());
            objectNode.put("date", DateTimeUtils.format(date, "yyyy-MM-dd HH:mm"));
            objectNode.put("shareDesc", shareTicket.getGroupOrder().getShareCouponInstructions());
            objectNode.put("shareTicketId", shareTicket.getId());
            shareTickets.add(objectNode);
        });
        ObjectNode data = JsonUtils.createObjectNode();
        data.put("pageNum", page.getPageNum());
        data.put("pageSize", page.getPageSize());
        data.put("totalPage", page.getTotalPage());
        data.put("totalRecord", page.getTotalRecord());
        data.set("shareTickets", shareTickets);
        response.outputJson(0, data);
    }

    //自己查看分享劵明细
    @RequestMapping("ownerdetail")
    public void queryByOwnerDetail(HttpRequestWrapper request, HttpResponseWrapper response) {
        //判断openId
        ShareTicket shareTicket = new ShareTicketQuery().id(request.getLongMust("shareTicketId")).readOnly().uniqueResult();
        if (null == shareTicket) {
            response.outputJson(-1, "shareTicketId错误");
            return;
        }
        String openId = request.getStringMust("openId");
        WeChatUser weChatUser = new WeChatUserQuery().openId(openId).readOnly().uniqueResult();
        if (null == weChatUser) {
            response.outputJson(-1, "openId错误");
            return;
        }
        if (shareTicket.getOwner() != weChatUser) {
            response.outputJson(-1, "你非本卷拥有者，无法查看分享劵明细");
            return;
        }
        List<ShareTicketDetail> list = new ShareTicketDetailQuery().shareTicket(shareTicket).readOnly().list();
        ArrayNode shareTicketDetails = JsonUtils.createArrayNode();
        list.forEach(shareTicketDetail -> {
            ObjectNode objectNode = JsonUtils.createObjectNode();
            if (shareTicketDetail.getChangeType() == ShareTicketDetail.ChangeType.EXCHANGE) {
                objectNode.put("contacts", shareTicketDetail.getShareTicket().getOwner().getContacts().substring(0,1)+"**");
                objectNode.put("headImgUrl", shareTicketDetail.getShareTicket().getOwner().getHeadImgUrl());
            } else {
                objectNode.put("contacts", shareTicketDetail.getRelatedUser().getContacts().substring(0,1)+"**");
                objectNode.put("headImgUrl", shareTicketDetail.getRelatedUser().getHeadImgUrl());
            }
            objectNode.put("type", shareTicketDetail.getGetWay() != null ? shareTicketDetail.getGetWay().getDesc() : shareTicketDetail.getChangeType().getDesc());
            objectNode.put("data", DateTimeUtils.format(shareTicketDetail.getCreateTime(), "yyyy-MM-dd HH:mm"));
            objectNode.put("amount", shareTicketDetail.getAmount());
            shareTicketDetails.add(objectNode);
        });
        response.outputJson(0, shareTicketDetails);
    }

    //自己查看分享劵明细
    @RequestMapping("ownerdetailofpage")
    public void queryByOwnerDetailOfPage(HttpRequestWrapper request, HttpResponseWrapper response) {
        Page<ShareTicketDetail> page = new ShareTicketDetailQuery().shareTicket(new ShareTicketQuery().id(request.getLongMust("shareTicketId")).uniqueResult()).changeType(ShareTicketDetail.ChangeType.GAIN)
                .readOnly().pageHasCount(request.getPageNum(), request.getPageSize());
        ArrayNode shareTicketDetails = JsonUtils.createArrayNode();
        page.getRecordList().forEach(shareTicketDetail -> {
            ObjectNode objectNode = JsonUtils.createObjectNode();
            if (shareTicketDetail.getChangeType() == ShareTicketDetail.ChangeType.EXCHANGE) {
                objectNode.put("contacts", shareTicketDetail.getShareTicket().getOwner().getContacts());
                objectNode.put("headImgUrl", shareTicketDetail.getShareTicket().getOwner().getHeadImgUrl());
            } else {
                objectNode.put("contacts", shareTicketDetail.getRelatedUser().getContacts());
                objectNode.put("headImgUrl", shareTicketDetail.getRelatedUser().getHeadImgUrl());
            }
            objectNode.put("type", shareTicketDetail.getGetWay().getDesc());
            objectNode.put("data", DateTimeUtils.format(shareTicketDetail.getCreateTime(), "yyyy-MM-dd HH:mm"));
            objectNode.put("amount", shareTicketDetail.getAmount());
            shareTicketDetails.add(objectNode);
        });
        ObjectNode data = JsonUtils.createObjectNode();
        data.put("pageNum", page.getPageNum());
        data.put("pageSize", page.getPageSize());
        data.put("totalPage", page.getTotalPage());
        data.put("totalRecord", page.getTotalRecord());
        data.set("shareTicketDetails", shareTicketDetails);
        response.outputJson(0, data);
    }

    /**
     * 分享成功获得分享劵
     */
    @Transactional
    @RequestMapping("getbyshare")
    public void getByShare(HttpRequestWrapper request, HttpResponseWrapper response) {
        String message = getShareTicketByShare(request.getStringMust("openId"), request.getStringMust("shareRandomNumber"));
//        response.outputJson(StringUtils.isEmpty(message) ? 0 : -1, StringUtils.isEmpty(message) ? "成功" : message);
        response.outputJson(0, StringUtils.isEmpty(message) ? "成功" : message);

    }

    /**
     * 购买成功获得分享劵
     */
    @Transactional
    @RequestMapping("getByBuy")
    public void getByBuy(HttpRequestWrapper request, HttpResponseWrapper response) {
        String message = getShareTicketByBuy(request.getStringMust("beSharerOpenId"), request.getStringMust("orderNumber"));
        response.outputJson(StringUtils.isEmpty(message) ? 0 : -1, StringUtils.isEmpty(message) ? "成功" : message);
    }

    /**
     * 退货
     */
    @Transactional
    @RequestMapping("refund")
    public void refund(HttpRequestWrapper request, HttpResponseWrapper response) {
        String message = refund(request.getStringMust("openId"), request.getStringMust("orderNumber"));
        response.outputJson(StringUtils.isEmpty(message) ? 0 : -1, StringUtils.isEmpty(message) ? "成功" : message);
    }

    /**
     * 分享成功获得分享劵
     *
     * @param beSharerOpenId 被分享者openId
     * @param randomNumber   分享记录的唯一标识
     * @return 错误信息，为null是表示成功
     */
    @Transactional
    public String getShareTicketByShare(String beSharerOpenId, String randomNumber) {
        String errorMessage = shareOrBuy(beSharerOpenId, randomNumber, null, ShareTicketDetail.GetWay.SHARE);
        if (StringUtils.isNotEmpty(errorMessage)) {
            logger.info("分享成功获得分享劵:{}", errorMessage);
        }
        return errorMessage;
    }

    /**
     * 购买成功获得分享劵
     *
     * @param beSharerOpenId 被分享者openId
     * @param orderNumber    订单编号
     * @return 错误信息，为null是表示成功
     */
    @Transactional
    public String getShareTicketByBuy(String beSharerOpenId, String orderNumber) {
        String prefix = "购买成功获得分享劵:{}";
        String errorMessage;
        Order order = new OrderQuery().orderNumber(orderNumber).readOnly().uniqueResult();
        if (null == order) {
            errorMessage = "订单编号错误";
            logger.info(prefix, errorMessage);
            return errorMessage;
        }
        if (StringUtils.isEmpty(order.getShareRandomNumber())) {
            errorMessage = "非通过分享页购买的订单";
            logger.info(prefix, errorMessage);
            return errorMessage;
        }
        errorMessage = shareOrBuy(beSharerOpenId, order.getShareRandomNumber(), orderNumber, ShareTicketDetail.GetWay.BUY);
        if (StringUtils.isNotEmpty(errorMessage)) {
            logger.info(prefix, errorMessage);
        }
        return errorMessage;
    }

    /**
     * 分享劵退货
     *
     * @param openId      退货者openId
     * @param orderNumber 团单编号
     * @return 错误信息，为null是表示成功
     */
    @Transactional
    public String refund(String openId, String orderNumber) {
        String prefix = "分享劵退货:{}";
        String errorMessage;
        //退货者
        WeChatUser weChatUser = new WeChatUserQuery().openId(openId).readOnly().uniqueResult();
        if (null == weChatUser) {
            errorMessage = "openId错误";
            logger.info(prefix, errorMessage);
            return errorMessage;
        }
        Order order = new OrderQuery().orderNumber(orderNumber).readOnly().uniqueResult();
        if (null == order) {
            errorMessage = "orderNumber错误";
            logger.info(prefix, errorMessage);
            return errorMessage;
        }
        if (StringUtils.isEmpty(order.getShareRandomNumber())) {
            errorMessage = "非通过分享页购买的订单";
            logger.info(prefix, errorMessage);
            return errorMessage;
        }
        //todo 是否要判断订单已经处于退货状态
        //退货已经扣过分享劵
        if (new ShareTicketDetailQuery().orderNumber(orderNumber).changeType(ShareTicketDetail.ChangeType.REFUND).readOnly().count() > 0) {
            errorMessage = "退货已经扣过分享劵";
            logger.info(prefix, errorMessage);
            return errorMessage;
        }
        GroupOrder groupOrder = order.getGroupOrder();
        Share share = new ShareQuery().randomNumber(order.getShareRandomNumber()).readOnly().uniqueResult();
        if (null == share) {
            errorMessage = "找不到分享表";
            logger.info(prefix, errorMessage);
            return errorMessage;
        }
        //买时获取的分享劵
//        ShareTicket shareTicket = new ShareTicketQuery().owner(weChatUser).groupOrder(groupOrder).lockMode(LockMode.PESSIMISTIC_WRITE).uniqueResult();
//        if (null == shareTicket){
//            errorMessage = "被分享者购买时没有获得劵";
//            logger.info(prefix, errorMessage);
//            return errorMessage;
//        }
//        List<ShareTicketDetail> shareTicketDetails = new ShareTicketDetailQuery().shareTicket(shareTicket).relatedUser(share.getWeChatUser())
//                .getWay(ShareTicketDetail.GetWay.BUY).readOnly().list();
//        if (!CollectionUtils.isEmpty(shareTicketDetails)) {
        //扣除被分享者购买时获得的劵
//            saveShareTicketAndDetail(weChatUser, weChatUser, groupOrder, share, null, ShareTicketDetail.ChangeType.REFUND, -groupOrder.getSuccessInvitedNumber(), orderNumber);
//        }
        //分享者在别人购买时获得了劵
        ShareTicket shareTicket = new ShareTicketQuery().owner(share.getWeChatUser()).groupOrder(groupOrder).lockMode(LockMode.PESSIMISTIC_WRITE).uniqueResult();
        if (null == shareTicket){
            errorMessage = "分享者在别人购买时没有获得劵";
            logger.info(prefix, errorMessage);
            return errorMessage;
        }
        List<ShareTicketDetail> shareTicketDetails = new ShareTicketDetailQuery().shareTicket(shareTicket).relatedUser(weChatUser)
                .getWay(ShareTicketDetail.GetWay.FEEDBACK).readOnly().list();
        if (!CollectionUtils.isEmpty(shareTicketDetails)) {
            //扣除被分享者购买时,分享者获得的劵
            saveShareTicketAndDetail(share.getWeChatUser(), weChatUser, groupOrder, share, null, ShareTicketDetail.ChangeType.REFUND, -groupOrder.getShareSuccessNumber(), orderNumber);
        }
        return null;
    }

    /**
     * 购买成功获得分享劵
     *
     * @param beSharerOpenId 被分享者openId
     * @param randomNumber   分享记录唯一标识
     * @param orderNumber    订单编码
     * @param getWay         获取途径
     */
    private String shareOrBuy(String beSharerOpenId, String randomNumber, String orderNumber, ShareTicketDetail.GetWay getWay) {
        //被分享者
        WeChatUser beSharer = new WeChatUserQuery().openId(beSharerOpenId).readOnly().uniqueResult();
        if (null == beSharer) {
            return "被分享者openId错误";
        }
        if (StringUtils.isEmpty(randomNumber)) {
            return "分享随机数为空";
        }
        //分享记录
        Share share = new ShareQuery().randomNumber(randomNumber).readOnly().uniqueResult();
        if (null == share) {
            return "分享记录randomNumber错误";
        }
        WeChatUser sharer = share.getWeChatUser();
        //自己分享给自己没奖励
        if (beSharer == sharer) {
            return "自己分享给自己没奖励";
        }
        //团单
        GroupOrder groupOrder = share.getGroupOrder();
        //必须为审核通过
        if (groupOrder.getReviewStates() != GroupOrder.ReviewStates.TG) {
            return "该团单未审核通过";
        }
        if (groupOrder.getGroupOrderState().getStates() != GroupOrderState.States.JXZ) {
            return "该团单已结束";
        }
        if (!groupOrder.getShareCoupon()) {
            return "此团未开启分享劵功能";
        }
        if (groupOrder.getGroupType() != GroupOrder.GroupType.PTT) {
            return "此团非普通团";
        }
        //过期团
        if (groupOrder.getEndTime() != null && new Date().after(groupOrder.getEndTime())) {
            return "团单已过期";
        }
        //区分是分享还是购买
        if (ShareTicketDetail.GetWay.SHARE == getWay) {
            //检查重复点击分享
            ShareTicket shareTicket = getShareTicket(sharer, groupOrder);
            Long count = new ShareTicketDetailQuery().shareTicket(shareTicket).relatedUser(beSharer).readOnly().count();
            if (count != null && count > 0) {
                return "重复获取分享劵";
            }
            //被分享者点击获得奖励
            saveShareTicketAndDetail(beSharer, sharer, groupOrder, share, ShareTicketDetail.GetWay.CLICK, ShareTicketDetail.ChangeType.GAIN, groupOrder.getInvitedNumber());
            //分享者同样获得奖励
            saveShareTicketAndDetail(sharer, beSharer, groupOrder, share, ShareTicketDetail.GetWay.SHARE, ShareTicketDetail.ChangeType.GAIN, groupOrder.getShareNumber());
        } else {
            //检查是否支付成功
            Order order = new OrderQuery().orderNumber(orderNumber).readOnly().uniqueResult();
            if (null == order) {
                return "订单编号错误";
            }
            if (order.getPayState() != Order.PayState.YZF) {
                return "订单未支付";
            }
            //判断同一订单重复获取
            if (new ShareTicketDetailQuery().orderNumber(orderNumber).changeType(ShareTicketDetail.ChangeType.GAIN).count() > 0) {
                return "重复获取分享劵";
            }
            //被分享者购买获得奖励
            saveShareTicketAndDetail(beSharer, sharer, groupOrder, share, ShareTicketDetail.GetWay.BUY, ShareTicketDetail.ChangeType.GAIN, groupOrder.getSuccessInvitedNumber(), orderNumber);
            //分享者同样获得回馈
            saveShareTicketAndDetail(sharer, beSharer, groupOrder, share, ShareTicketDetail.GetWay.FEEDBACK, ShareTicketDetail.ChangeType.GAIN, groupOrder.getShareSuccessNumber(), orderNumber);
        }
        return null;
    }

    /**
     * 保存分享劵详情
     *
     * @param shareTicket 分享劵总表
     * @param relatedUser 与这条记录相关的人
     * @param share       分享单
     * @param getWay      获得方式
     * @param changeType  变动
     * @param causeChange 变动原因
     * @param amount      变动原因
     * @param orderNumber 订单编号
     */
    private void saveShareTicketDetail(ShareTicket shareTicket, WeChatUser relatedUser, Share share, ShareTicketDetail.GetWay getWay, ShareTicketDetail.ChangeType changeType, String causeChange, Integer amount, String orderNumber) {
        ShareTicketDetail shareTicketDetail = new ShareTicketDetail();
        shareTicketDetail.setShareTicket(shareTicket);
        shareTicketDetail.setRelatedUser(relatedUser);
        shareTicketDetail.setShare(share);
        shareTicketDetail.setGetWay(getWay);
        shareTicketDetail.setChangeType(changeType);
        shareTicketDetail.setCauseChange(causeChange);
        shareTicketDetail.setAmount(amount);
        shareTicketDetail.setOrderNumber(orderNumber);
        shareTicketDetail.saveOrUpdate();
    }

    /**
     * 保存分享劵
     *
     * @param owner      获得方式
     * @param groupOrder 团单
     */
    private ShareTicket getShareTicket(WeChatUser owner, GroupOrder groupOrder) {
        ShareTicket shareTicket = new ShareTicketQuery().owner(owner).groupOrder(groupOrder).lockMode(LockMode.PESSIMISTIC_WRITE).uniqueResult();
        if (null == shareTicket) {
            shareTicket = new ShareTicket();
            shareTicket.setOwner(owner);
            shareTicket.setGroupOrder(groupOrder);
            shareTicket.saveOrUpdate();
        }
        return shareTicket;
    }

    /**
     * 保存分享劵详情
     *
     * @param owner       分享劵拥有者
     * @param relatedUser 与这条记录相关的人
     * @param groupOrder  团单
     * @param share       分享单
     * @param getWay      获得方式
     * @param changeType  变动
     * @param amount      数量
     * @param causeChange 变动原因
     * @param orderNumber 订单编号
     */
    private void saveShareTicketAndDetail(WeChatUser owner, WeChatUser relatedUser, GroupOrder groupOrder, Share share, ShareTicketDetail.GetWay getWay, ShareTicketDetail.ChangeType changeType, Integer amount, String causeChange, String orderNumber) {
        if (null != amount && amount != 0) {
            //获得分享劵总表
            ShareTicket sharerTicket = getShareTicket(owner, groupOrder);
            //保存分享劵明细
            saveShareTicketDetail(sharerTicket, relatedUser, share, getWay, changeType, causeChange, amount, orderNumber);
            //增加分享劵总数
            sharerTicket.setAmount(sharerTicket.getAmount() + amount);
            sharerTicket.saveOrUpdate();
        }
    }

    /**
     * 重写saveShareTicketAndDetail方法,没有causeChange和orderNumber
     */
    private void saveShareTicketAndDetail(WeChatUser owner, WeChatUser relatedUser, GroupOrder groupOrder, Share share, ShareTicketDetail.GetWay getWay, ShareTicketDetail.ChangeType changeType, Integer amount) {
        saveShareTicketAndDetail(owner, relatedUser, groupOrder, share, getWay, changeType, amount, null, null);
    }

    /**
     * 重写saveShareTicketAndDetail方法，没有orderNumber
     */
    private void saveShareTicketAndDetail(WeChatUser owner, WeChatUser relatedUser, GroupOrder groupOrder, Share share, ShareTicketDetail.GetWay getWay, ShareTicketDetail.ChangeType changeType, Integer amount, String orderNumber) {
        saveShareTicketAndDetail(owner, relatedUser, groupOrder, share, getWay, changeType, amount, null, orderNumber);
    }
}
