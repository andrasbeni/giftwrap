package com.github.andrasbeni.giftwrap.example;

import java.io.IOException;

import com.github.andrasbeni.giftwrap.GiftWrap;

@GiftWrap
public class Whatever {
	
	private final String prefix;
	
	public Whatever(String prefix) {
		this.prefix = prefix;
	}
	
	
	@Logged(LogLevel.DEBUG)
	@Transactional()
	public String doWhatever(String result) throws IOException {
		return prefix + result;
	}

}
