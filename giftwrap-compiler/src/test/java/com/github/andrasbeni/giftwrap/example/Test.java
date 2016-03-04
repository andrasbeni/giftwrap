package com.github.andrasbeni.giftwrap.example;

import org.junit.Assert;

public class Test {
	
	@org.junit.Test
	public void testWhateverIntercepted() throws Exception {
		Whatever object = new WhateverIntercepted("aaa", new LoggerInterceptor(), new TransactionInterceptor());
		String result = object.doWhatever("bbb");
		Assert.assertEquals("aaabbb", result);
	}

}
