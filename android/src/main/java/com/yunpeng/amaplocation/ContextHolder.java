/**
 * 
 */
package com.yunpeng.amaplocation;

import com.facebook.react.bridge.ReactApplicationContext;

/**
 * @author iDay
 *
 */
public class ContextHolder {
	static ReactApplicationContext context;
	public static ReactApplicationContext getReactApplicationContext() {
		return context;
	}
}
