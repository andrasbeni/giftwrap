package com.github.andrasbeni.giftwrap;

import java.lang.annotation.Annotation;

public interface Interceptor<A extends Annotation> {

	public Object intercept(Interception<A> interception) throws Exception;
	
}
