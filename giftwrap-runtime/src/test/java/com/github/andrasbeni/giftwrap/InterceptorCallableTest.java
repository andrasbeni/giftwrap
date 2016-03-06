package com.github.andrasbeni.giftwrap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.lang.annotation.Annotation;
import java.util.concurrent.Callable;

import org.junit.Test;

public class InterceptorCallableTest {

	public static @interface TestAnnotation {}
	
	public static class TestImpl implements TestAnnotation {

		@Override
		public Class<? extends Annotation> annotationType() {
			return TestAnnotation.class;
		}
		
	}
	
	@Test
	public void testCall() throws Exception {
		final TestAnnotation annotation = new TestImpl();
		
		class CallableMock implements Callable<Object> {
			private int counter;
			@Override
			public Object call() throws Exception {
				return ++counter;
			}
			int getCount() {
				return counter;
			}
			
			
		};
		
		final CallableMock next = new CallableMock();
		
		final Object target = new Object();
		
		final Object[] parameters = new Object[]{};
		
		final String methodName= "methodName";
			
		final Object returnValue = new Object();
			
		Interceptor<TestAnnotation> mockInterceptor = new Interceptor<TestAnnotation>() {

			@Override
			public Object intercept(Interception<TestAnnotation> interception) throws Exception {
				assertEquals(methodName, interception.getMethodName());
				assertSame("Target should be the same", target, interception.getTarget());
				assertSame("Annotation should be the same", annotation, interception.getAnnotation());
				assertSame("Parameters should be the same", parameters, interception.getParameters());
				assertEquals("Next should be called by proceed", 0, next.getCount());
				int resultOfProceed = (Integer) interception.proceed();
				assertEquals("Proceed should call next", 1, resultOfProceed);
				return returnValue;
			}
		};
		
		InterceptorCallable<TestAnnotation> callable = 
				new InterceptorCallable<TestAnnotation>(mockInterceptor, annotation, next, methodName, target, parameters);
		Object result = callable.call();
		assertEquals("InterceptorCallable should return what the interceptor returns", returnValue, result);
		
	}

}
