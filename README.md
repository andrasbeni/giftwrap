# giftwrap

A [Dagger 2](http://google.github.io/dagger/) inspired wrapper generator library

## Overview

giftwrap generates decorator classes at compile-time. 

## Benefits

 * Faster startup
 * Readable decorator class names
 * Readable decorator classes
 

## Usage

#### Create the annotation
```java
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@InterceptedBy(LoggerInterceptor.class)
public @interface Logged {

	LogLevel value() default LogLevel.DEBUG;
	
}
```

#### Create the corresponding interceptor
```java
public class LoggerInterceptor implements Interceptor<Logged> {

	private static final Logger logger = Logger.getLogger("entrylogger");
	
	@Override
	public Object intercept(Interception<Logged> interception) throws Exception {
		Level level = interception.getAnnotation().value().julLevel();
		logger.log(level, "Entering method " + interception.getMethodName());
		return interception.proceed();
	}

}
```

#### Annotate your class
```java
@GiftWrap
public class Whatever {

	@Logged(LogLevel.DEBUG)
	@Transactional
	public String doWhatever(String result) throws IOException {
		return result;
	}

}
```

#### Instantiate the wrapper
```java
Whatever object = new WhateverIntercepted(
        contructor, arguments, ofWhatever,
		new LoggerInterceptor(), 
		new TransactionInterceptor());
```

#### Have a look at the stack
```
c.g.a.g.example.Whatever.doWhatever(Whatever.java:20)
c.g.a.g.example.WhateverIntercepted.access$0(WhateverIntercepted.java:1)
c.g.a.g.example.WhateverIntercepted$1.call(WhateverIntercepted.java:21)
c.g.a.g.Interception.proceed(Interception.java:32)
c.g.a.g.example.LoggerInterceptor.intercept(LoggerInterceptor.java:18)
c.g.a.g.InterceptorCallable.call(InterceptorCallable.java:27)
c.g.a.g.Interception.proceed(Interception.java:32)
c.g.a.g.example.TransactionInterceptor.intercept(TransactionInterceptor.java:10)
c.g.a.g.InterceptorCallable.call(InterceptorCallable.java:27)
c.g.a.g.example.WhateverIntercepted.doWhatever(WhateverIntercepted.java:35)
```

## TODO
- [ ] Fix codegen for interfaces
- [ ] Implement tests
- [ ] Release




