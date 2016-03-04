package com.github.andrasbeni.giftwrap.example;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.andrasbeni.giftwrap.Interception;
import com.github.andrasbeni.giftwrap.Interceptor;

public class LoggerInterceptor implements Interceptor<Logged> {

	private static final Logger logger = Logger.getLogger("entryexitlogger");
	
	@Override
	public Object intercept(Interception<Logged> interception) throws Exception {
		Level level = interception.getAnnotation().value().julLevel();
		logger.log(level, "Entering method " + interception.getMethodName());
		try {
			Object result = interception.proceed();
			logger.log(level, "Returning from method " + interception.getMethodName());
			return result;
		} catch (Exception e) {
			logger.log(level, "Throwing from method " + interception.getMethodName());
			throw e;
		}
	}

}
