package com.github.andrasbeni.giftwrap.example;

import com.github.andrasbeni.giftwrap.Interception;
import com.github.andrasbeni.giftwrap.Interceptor;

public class TransactionInterceptor implements Interceptor<Transactional> {

	@Override
	public Object intercept(Interception<Transactional> interception) throws Exception {
		return interception.proceed();
	}

}
