package com.jiyuanwai.logging.extend.interceptor;

import com.jiyuanwai.logging.extend.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * LogInterceptor
 * 用于处理动态日志
 *
 * @author xuning
 * @date 2020-01-16 06:05:19
 */
@Slf4j
@Component
@Order(Integer.MIN_VALUE)
@WebFilter("/*")
public class LogInterceptor implements Filter {

	public static final String REQUEST_ID_MDC_KEY = "requestId";
	public static final String SESSION_ID_MDC_KEY = "sessionId";
	public static final String STAND_ALONE_HEADER_KEY = "X-StandAlone-File";
	public static final String STAND_ALONE_SESSION_KEY = STAND_ALONE_HEADER_KEY;

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

		try {
			HttpServletRequest request = (HttpServletRequest) servletRequest;
			// requestId 与超级 SessionId
			String requestId = CommonUtils.generateRequestId();
			MDC.put(REQUEST_ID_MDC_KEY, requestId);
			String sessionId = request.getRequestedSessionId();
			MDC.put(SESSION_ID_MDC_KEY, sessionId);


			// 只有开启了独立文件输出，才放入MDC
			String standAlone = request.getHeader(STAND_ALONE_HEADER_KEY);
			if (standAlone == null) {
				standAlone = (String) request.getSession().getAttribute(STAND_ALONE_SESSION_KEY);
			}
			if (standAlone != null) {
				// 此处可以任意定制不同路径
				MDC.put(STAND_ALONE_HEADER_KEY, sessionId);
			}
		} catch (Exception e) {
			// 此处处理异常，不影响业务
			log.error("Error handler dynamic log", e);
		} finally {
			// 切记清理环境
			MDC.clear();
		}

		// 继续执行，不处理其他Filter异常
		filterChain.doFilter(servletRequest, servletResponse);
	}
}
