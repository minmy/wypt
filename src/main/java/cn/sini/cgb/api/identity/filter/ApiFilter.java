package cn.sini.cgb.api.identity.filter;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.NestedServletException;

import cn.sini.cgb.admin.identity.entity.User;
import cn.sini.cgb.admin.identity.query.UserQuery;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.api.cgb.query.group.WeChatUserQuery;
import cn.sini.cgb.api.identity.entity.ApiAccessLog;
import cn.sini.cgb.api.identity.entity.ApiAccessToken;
import cn.sini.cgb.api.identity.entity.ApiRateLimit;
import cn.sini.cgb.api.identity.entity.ApiRequestLimit;
import cn.sini.cgb.api.identity.entity.ApiResource;
import cn.sini.cgb.api.identity.entity.ApiResource.LimitPeriod;
import cn.sini.cgb.api.identity.query.ApiAccessTokenQuery;
import cn.sini.cgb.api.identity.query.ApiRateLimitQuery;
import cn.sini.cgb.api.identity.query.ApiRequestLimitQuery;
import cn.sini.cgb.api.identity.query.ApiResourceQuery;
import cn.sini.cgb.common.exception.ParamException;
import cn.sini.cgb.common.exception.SystemException;
import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;
import cn.sini.cgb.common.json.JsonUtils;
import cn.sini.cgb.common.util.DateTimeUtils;
import cn.sini.cgb.common.util.Environment;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 接口过滤器
 * 
 * @author 杨海彬
 */
@Component
public class ApiFilter implements Filter {

	protected static final Logger LOGGER = LoggerFactory.getLogger(ApiFilter.class);

	@Value("#{systemProperties['path']}")
	private String prefix;

	@Value("#{systemProperties['api.signKey']}")
	private String signKey;

	@Value("#{systemProperties['api.enableIpLimit']}")
	private Boolean enableIpLimit = false;

	@Value("#{systemProperties['api.ipWhite']}")
	private String ipWhite;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
		HttpRequestWrapper request = req instanceof HttpRequestWrapper ? (HttpRequestWrapper) req : new HttpRequestWrapper((HttpServletRequest) req);
		HttpResponseWrapper response = res instanceof HttpResponseWrapper ? (HttpResponseWrapper) res : new HttpResponseWrapper((HttpServletResponse) res);
		try {
			if (Environment.getBean("apiFilter", ApiFilter.class).preHandle(request, response)) {
				filterChain.doFilter(req, res);
			}
		} catch (Throwable e) {
			response.reset();
			if (e instanceof NestedServletException) {
				Throwable cause = e.getCause();
				if (cause != null) {
					e = e.getCause();
				}
			} else {
				LOGGER.error("接口安全过滤器出现异常", e);
			}
			if (e instanceof ParamException) {
				response.outputJson(40000, e.getMessage());
			} else if (e instanceof SystemException) {
				response.outputJson(50001, e.getMessage());
			} else {
				response.outputJson(50000, "服务器内部错误");
			}
		}
	}

	/** 前置处理 */
	@Transactional
	public boolean preHandle(HttpRequestWrapper request, HttpResponseWrapper response) throws IOException {
		Date currentTime = new Date();
		ApiAccessToken apiAccessToken = null;
		String access_token = StringUtils.defaultIfEmpty(request.getTrim("access_token"), "");
		String ip = request.getIp();
		// 取得用户对象
		User user = null;
		String uri = request.getRequestURI().replaceFirst(this.prefix, "");
		ApiResource apiResource = new ApiResourceQuery().uri(uri).uniqueResult();
		if ("/api/user/login.action".equals(uri)) {
			// 验证app_id
			String app_id = request.getTrim("app_id");
			if (StringUtils.isEmpty(app_id)) {
				response.outputJson(40001, "缺少app_id参数");
				return false;
			}
			user = new UserQuery().username(app_id).uniqueResult();
			if (user == null) {
				response.outputJson(40002, "无效的app_id参数");
				return false;
			}
		} else {
			// 校验接口访问权限
			if (apiResource == null) {
				response.outputJson(10003, "尚未开通的接口地址");
				return false;
			}
			WeChatUser weChatUser = new WeChatUserQuery().openId(request.getStringMust("openId")).uniqueResult();
			if (!CollectionUtils.containsAny(weChatUser.getApiRoles(), apiResource.getApiRoles())) {
				response.outputJson(10004, "没有访问权限");
				return false;
			}
			// 验证access_token
			if (StringUtils.isEmpty(access_token)) {
				response.outputJson(40005, "缺少access_token参数");
				return false;
			}
			apiAccessToken = new ApiAccessTokenQuery().accessToken(access_token).uniqueResult();
			if (apiAccessToken == null) {
				response.outputJson(40006, "无效的access_token参数");
				return false;
			}
			user = apiAccessToken.getUser();
		}
		// 保存访问日志
		ObjectNode header = JsonUtils.createObjectNode();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String name = headerNames.nextElement();
			header.put(name, request.getHeader(name));
		}
		ApiAccessLog apiAccessLog = new ApiAccessLog();
		apiAccessLog.setUsername(user.getUsername());
		apiAccessLog.setIp(ip);
		apiAccessLog.setUrl(request.getRequestURL().toString());
		apiAccessLog.setHeader(header.toString());
		apiAccessLog.setParameter(JsonUtils.toObjectNode(request.getParameterMap()).toString());
		apiAccessLog.saveOrUpdate();
		if (!Environment.isDevMode()) { // 如果不是开发模式，则要校验以下参数
			// 收集参数
			Long timestamp = request.getLong("timestamp");
			String request_id = request.getTrim("request_id");
			String signature = request.getTrim("signature");
			if (timestamp == null) {
				response.outputJson(40008, "缺少timestamp参数");
				return false;
			}
			if (StringUtils.isEmpty(request_id)) {
				response.outputJson(40010, "缺少request_id参数");
				return false;
			}
			if (StringUtils.isEmpty(signature)) {
				response.outputJson(40012, "缺少signature参数");
				return false;
			}
			// 校验timestamp是否与服务器时间相差正负十分钟
			Date minTime = DateTimeUtils.addMinute(currentTime, -10);
			Date maxTime = DateTimeUtils.addMinute(currentTime, 10);
			if (timestamp < minTime.getTime() || timestamp > maxTime.getTime()) {
				response.outputJson(40009, "timestamp参数与服务器时间相差超过正负十分钟", JsonUtils.createObjectNode().put("server_timestamp", currentTime.getTime()));
				return false;
			}
			// 校验request_id在正负十分钟是否重复
			if (new ApiRequestLimitQuery().requestId(request_id).user(user).createTimeBetween(minTime, maxTime).readOnly().count() > 0) {
				response.outputJson(40011, "request_id参数在正负十分钟内重复使用");
				return false;
			} else {
				ApiRequestLimit apiRequestLimit = new ApiRequestLimit();
				apiRequestLimit.setRequestId(request_id);
				apiRequestLimit.setUser(user);
				apiRequestLimit.saveOrUpdate();
			}
			// 校验提交上来的签名值
			if (!signature.equalsIgnoreCase(DigestUtils.md5Hex(access_token + timestamp + request_id + this.signKey))) {
				response.outputJson(40013, "signature参数校验失败");
				return false;
			}
		}
		// 校验用户
		if (user.getDisable()) {
			response.outputJson(10001, "用户已被禁用");
			return false;
		}
		// 校验access_toekn是否过期
		if (apiAccessToken != null && currentTime.after(apiAccessToken.getExpireTime())) {
			response.outputJson(40007, "access_token参数已过期");
			return false;
		}
		// 校验IP白名单
		if (this.enableIpLimit && !ArrayUtils.contains(this.ipWhite.split(","), ip)) {
			response.outputJson(10002, "请求的IP地址不在白名单", JsonUtils.createObjectNode().put("request_ip", ip));
			return false;
		}
		// 校验接口调用次数
		if (apiResource.getEnableRateLimit()) {
			ApiRateLimit apiRateLimit = new ApiRateLimitQuery().user(user).apiResource(apiResource).uniqueResult();
			if (apiRateLimit == null) {
				apiRateLimit = new ApiRateLimit();
				apiRateLimit.setStartTime(getLimitPeriodStartTime(currentTime, apiResource.getLimitPeriod()));
				apiRateLimit.setAccessCount(0);
				apiRateLimit.setApiResource(apiResource);
				apiRateLimit.setUser(user);
			} else if (!isSameLimitPeriod(currentTime, apiRateLimit.getStartTime(), apiResource.getLimitPeriod())) {
				apiRateLimit.setStartTime(getLimitPeriodStartTime(currentTime, apiResource.getLimitPeriod()));
				apiRateLimit.setAccessCount(0);
			}
			if (apiRateLimit.getAccessCount() >= apiResource.getLimitCount()) {
				response.outputJson(10005, "超过接口调用次数");
				return false;
			}
			apiRateLimit.setAccessCount(apiRateLimit.getAccessCount() + 1);
			apiRateLimit.saveOrUpdate();
		}
		// 往request域存入登录用户并放行
		request.setAttribute("API_LOGIN_USER", user);
		return true;
	}

	/** 获取限制周期开始时间 */
	private Date getLimitPeriodStartTime(Date currentTime, LimitPeriod limitPeriod) {
		String pattern = null;
		if (limitPeriod == LimitPeriod.MINUTE) {
			pattern = "yyyy-MM-dd HH:mm";
		} else if (limitPeriod == LimitPeriod.HOUR) {
			pattern = "yyyy-MM-dd HH";
		} else if (limitPeriod == LimitPeriod.DAY) {
			pattern = "yyyy-MM-dd";
		} else if (limitPeriod == LimitPeriod.MONTH) {
			pattern = "yyyy-MM";
		} else if (limitPeriod == LimitPeriod.YEAR) {
			pattern = "yyyy";
		}
		return DateTimeUtils.parse(DateTimeUtils.format(currentTime, pattern), pattern);
	}

	/** 是否为同一个限制周期 */
	private boolean isSameLimitPeriod(Date currentTime, Date startTime, LimitPeriod limitPeriod) {
		Date endTime = null;
		if (limitPeriod == LimitPeriod.MINUTE) {
			endTime = DateTimeUtils.addMinute(startTime, 1);
		} else if (limitPeriod == LimitPeriod.HOUR) {
			endTime = DateTimeUtils.addHour(startTime, 1);
		} else if (limitPeriod == LimitPeriod.DAY) {
			endTime = DateTimeUtils.addDay(startTime, 1);
		} else if (limitPeriod == LimitPeriod.MONTH) {
			endTime = DateTimeUtils.addMonth(startTime, 1);
		} else if (limitPeriod == LimitPeriod.YEAR) {
			endTime = DateTimeUtils.addYear(startTime, 1);
		}
		return currentTime.before(endTime);
	}
}