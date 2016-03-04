package com.github.andrasbeni.giftwrap;

import java.lang.annotation.Annotation;
import java.util.concurrent.Callable;

public class Interception<A extends Annotation> {
	
	private final A annotation;
	private final Object target;
	private final Callable<Object> proceed;
	private final String methodName;
	private final Object[] parameters;

	public Interception(A annotation, Object target, Callable<Object> proceed, String methodName, Object[] parameters) {
		this.annotation = annotation;
		this.target = target;
		this.proceed = proceed;
		this.methodName = methodName;
		this.parameters = parameters;
		
	}

	public A getAnnotation() {
		return annotation;
	}

	public Object getTarget() {
		return target;
	}

	public Object proceed() throws Exception {
		return proceed.call();
	}

	public String getMethodName() {
		return methodName;
	}

	public Object[] getParameters() {
		return parameters;
	}
	
	

}
