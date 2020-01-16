package com.jiyuanwai.logging.extend.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import com.jiyuanwai.logging.extend.dynamic.DynamicLoggingFilter;
import com.jiyuanwai.logging.extend.filter.LogFilter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Controller
 *
 * @author jiyanwai
 * @date 2020-01-15 16:11:56
 */
@Slf4j
@RestController
@RequestMapping("/log/level")
public class LogController {

	/**
	 * 测试日志输出
	 */
	@GetMapping
	public void debugLevel() {
		log.debug("A debug Log");
	}

	/**
	 * 为当前用户开启debug，测试使用，生产环境请配合在线用户管理相关功能来实现
	 *
	 * @param session session
	 */
	@PutMapping
	public void startDebugBySession(HttpSession session,
	                                @RequestParam(required = false) boolean standAlone) {
		// 仅供测试，线上需要开发功能，获取对对应用户 session，然后放入属性
		session.setAttribute(DynamicLoggingFilter.DEBUG_SESSION_KEY, "true");
		if (standAlone) {
			session.setAttribute(LogFilter.STAND_ALONE_SESSION_KEY, "");
		}
	}

	/**
	 * 修改单个日志级别
	 *
	 * @param loggerName 日志实例名称
	 * @param level      级别
	 */
	@PostMapping
	public void changeLoggingLevel(String loggerName,
	                               @RequestParam(required = false, defaultValue = "DEBUG") String level) {
		ch.qos.logback.classic.Logger logger =
				(ch.qos.logback.classic.Logger) LoggerFactory.getLogger(loggerName);
		logger.setLevel(Level.toLevel(level));
	}

	/**
	 * 将日志级别重置为配置文件默认
	 */
	@DeleteMapping
	public void restoreLoggingLevel() {
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(context);
			context.reset();
			configurator.doConfigure(new ClassPathResource("logback.xml").getInputStream());
		} catch (JoranException | IOException ignore) {
			// StatusPrinter will handle this
		}
		StatusPrinter.printInCaseOfErrorsOrWarnings(context);
	}
}
