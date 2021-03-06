package com.github.andrasbeni.giftwrap.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.andrasbeni.giftwrap.InterceptedBy;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@InterceptedBy(LoggerInterceptor.class)
public @interface Logged {

	LogLevel value() default LogLevel.DEBUG;
	
}
