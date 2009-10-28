package com.appspot.baotwits.server.service.util;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class RequestInjection implements MethodInterceptor{

	protected String requestSetterName;
	protected String responseSetterName;
	private static final Logger logger = Logger.getLogger(RequestInjection.class.getName());

	private void setRequestOnTarget(HttpServletRequest request, HttpServletResponse response, Object target) throws Exception {
		logger.info("Request Injection In Progress 1");
		if (requestSetterName != null)
			try {
				Method method = target.getClass().getMethod(requestSetterName, new Class[] { HttpServletRequest.class });
				method.invoke(target, new Object[] { request });
			} catch (NoSuchMethodException e) {
			}
		if (responseSetterName != null)
			try {
				Method method = target.getClass().getMethod(responseSetterName, new Class[] { HttpServletResponse.class });
				method.invoke(target, new Object[] { response });
			} catch (NoSuchMethodException e) {
			}
	}

	public Object invoke(MethodInvocation invocation) throws Throwable {
		logger.info("Request Injection In Progress 2");
		Object target = invocation.getThis();
		setRequestOnTarget(ServiceUtils.getRequest(), ServiceUtils.getResponse(), target);
		return invocation.proceed();
	}

	/**
	 * Specify the name of the setter method that can be invoked to set the
	 * current request and response on the service. If the method does not exist
	 * on the service, it is silently discarded.
	 * 
	 * @param setterName
	 */
	public void setRequestSetterName(String setterName) {
		this.requestSetterName = setterName;
	}

	/**
	 * Specify the name of the setter method that can be invoked to set the
	 * current response on the service. If the method does not exist on the
	 * service, it is silently discarded.
	 * 
	 * @param setterName
	 */
	public void setResponseSetterName(String setterName) {
		this.responseSetterName = setterName;
	}

}
