package com.github.andrasbeni.giftwrap.example;

import com.github.andrasbeni.giftwrap.Interception;
import com.github.andrasbeni.giftwrap.Interceptor;

public class Multiplier implements Interceptor<Multiplied> {

	@Override
	public Object intercept(Interception<Multiplied> interception) throws Exception {
		Object originalValue = interception.proceed();
		if (originalValue instanceof Integer) {
			return interception.getAnnotation().value() * (Integer) originalValue;
		}
		throw new RuntimeException("Multiplier cannot multiply " + originalValue);
	}

}
