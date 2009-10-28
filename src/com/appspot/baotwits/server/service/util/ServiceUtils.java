package com.appspot.baotwits.server.service.util;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.baotwits.server.service.AuthServiceImpl;

public class ServiceUtils {
	private static ThreadLocal<HttpServletRequest> servletRequest = new ThreadLocal<HttpServletRequest>();

	private static ThreadLocal<HttpServletResponse> servletResponse = new ThreadLocal<HttpServletResponse>();
	private static final Logger logger = Logger.getLogger(ServiceUtils.class.getName());
	/**
	 * Adjusts HTTP headers so that browsers won't cache response.
	 * @param response
	 * For more background see <a href="http://www.onjava.com/pub/a/onjava/excerpt/jebp_3/index2.html">this</a>.
	 */
	public static void disableResponseCaching(HttpServletResponse response) {
		response.setHeader("Expires", "Sat, 1 January 2000 12:00:00 GMT");
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
	}

	/**
	 * Return the request which invokes the service. Valid only if used in the
	 * dispatching thread.
	 * 
	 * @return the servlet request
	 */
	public static HttpServletRequest getRequest() {
		return servletRequest.get();
	}

	/**
	 * Return the response which accompanies the request. Valid only if used in
	 * the dispatching thread.
	 * 
	 * @return the servlet response
	 */
	public static HttpServletResponse getResponse() {
		return servletResponse.get();
	}

	/**
	 * Assign the current servlet request to a thread local variable. Valid only
	 * if used inside the invoking thread scope.
	 * 
	 * @param request
	 */
	public static void setRequest(HttpServletRequest request) {
		logger.info("Set Request");
		servletRequest.set(request);
	}

	/**
	 * Assign the current servlet response to a thread local variable. Valid
	 * only if used inside the invoking thread scope.
	 * 
	 * @param response
	 */
	public static void setResponse(HttpServletResponse response) {
		logger.info("Set Response");
		servletResponse.set(response);
	}

}
