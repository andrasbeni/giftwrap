package com.github.andrasbeni.giftwrap.example;

import com.github.andrasbeni.giftwrap.GiftWrap;

@GiftWrap
public interface Service {
 
	@Multiplied(3)
	@Logged(LogLevel.INFO)
	public int add(int a, int b);
	
	public static class ServiceUsage {
		public static void main(String[] args) {
			Service simpleService = new Service() {

				@Override
				public int add(int a, int b) {
					return a + b;
				}};
			Service wrappedService = new ServiceIntercepted(
					simpleService, 
					new Multiplier(),
					new LoggerInterceptor());
			wrappedService.add(40, 2);
		}
	}
}
