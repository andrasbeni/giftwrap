package com.github.andrasbeni.giftwrap;

import java.lang.annotation.Annotation;
import java.util.concurrent.Callable;

public class InterceptorCallable<A extends Annotation> implements Callable<Object> {

	private final A annotation;
	private final Object target;
	private final Callable<Object> next;
	private final String methodName;
	private final Object[] parameters;
	private Interceptor<A> interceptor;
	
	public InterceptorCallable(Interceptor<A> interceptor, A annotation, Callable<Object> next, String methodName, Object target, Object... parameters) {
		this.interceptor = interceptor;
		this.annotation = annotation;
		this.target = target;
		this.next = next;
		this.methodName = methodName;
		this.parameters = parameters;
	}
	
	
	@Override
	public Object call() throws Exception {
		return interceptor.intercept(new Interception<A>(annotation, target, next, methodName, parameters));
	}

}
