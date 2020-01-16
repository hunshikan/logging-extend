package com.jiyuanwai.logging.extend.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import com.jiyuanwai.logging.extend.dynamic.DynamicLoggingFilter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Controller
 *
 * @author xuning
 * @date 2020-01-15 16:11:56
 */
@Slf4j
@RestController
@RequestMapping("/log/level")
public class LogController {

	@GetMapping
	public void debugLevel(HttpSession session) {
		session.setAttribute(DynamicLoggingFilter.DEBUG_SESSION_KEY, "true");
		log.debug("A debug Log");
	}

	@PostMapping
	public void changeLoggingLevel(String loggerName, Level level) {
		ch.qos.logback.classic.Logger logger =
				(ch.qos.logback.classic.Logger) LoggerFactory.getLogger(loggerName);
		logger.setLevel(level);
	}

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
