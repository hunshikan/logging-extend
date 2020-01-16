package com.jiyuanwai.logging.extend.util;

import lombok.experimental.UtilityClass;

import java.util.UUID;

/**
 * CommonUtils
 * 常用工具类
 *
 * @author jiyanwai
 * @date 2020-01-16 06:06:37
 */
@UtilityClass
public class CommonUtils {

	/**
	 * 生成 RequestId
	 *
	 * @return RequestId
	 */
	public String generateRequestId() {
		// 简单实现，分布式环境可能需要发号器
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
