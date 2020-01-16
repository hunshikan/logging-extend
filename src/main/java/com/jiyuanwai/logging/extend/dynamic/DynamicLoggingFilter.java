package com.jiyuanwai.logging.extend.dynamic;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * DynamicLoggingFilter
 *
 * @author xuning
 * @date 2020-01-15 16:09:16
 */
@Slf4j
public class DynamicLoggingFilter extends TurboFilter {

	public static final String DEBUG_HEADER_NAME = "X-Debug";
	public static final String DEBUG_SESSION_KEY = DEBUG_HEADER_NAME;

	/**
	 * 返回值 FilterReply.DENY，FilterReply.NEUTRAL或FilterReply.ACCEPT
	 * 如果是 DENY，则不会再输出
	 * 如果是 ACCEPT，则直接输出
	 * 如果是 NEUTRAL，走来日志输出判断逻辑
	 *
	 * @return FilterReply.DENY，FilterReply.NEUTRAL或FilterReply.ACCEPT
	 */
	@Override
	public FilterReply decide(Marker marker, Logger logger, Level level,
	                          String format, Object[] params, Throwable t) {

		// 这里可以过滤我们自己的logger
		boolean isMyLogger = logger.getName().startsWith("com.jiyuanwai");
		if (!isMyLogger) {
			return FilterReply.NEUTRAL;
		}

		RequestAttributes requestAttributes = RequestContextHolder
				.getRequestAttributes();

		// 项目启动或者运行定时器时，可能没有 RequestAttributes
		if (requestAttributes == null) {
			return FilterReply.NEUTRAL;
		}

		// 先判断 RequestHeader，用于区分线程
		ServletRequestAttributes servletRequestAttributes =
				(ServletRequestAttributes) requestAttributes;
		// 按照请求参数判断，实际生产环境可以开发功能给管理人员功能，将用户唯一标示放入缓存或者session
		HttpServletRequest request = servletRequestAttributes.getRequest();
		String debug = request.getHeader(DEBUG_HEADER_NAME);
		boolean debugBoolean = Boolean.parseBoolean(debug);
		if (debugBoolean) {
			return FilterReply.ACCEPT;
		}

		// 再判断 Session
		HttpSession session = request.getSession(false);
		if (session == null) {
			return FilterReply.NEUTRAL;
		}
		Object attribute = session.getAttribute(DEBUG_SESSION_KEY);
		debugBoolean = Boolean.parseBoolean((String) attribute);
		if (debugBoolean) {
			return FilterReply.ACCEPT;
		}

		return FilterReply.NEUTRAL;
	}

}
