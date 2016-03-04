package com.github.andrasbeni.giftwrap.example;

import java.util.logging.Level;

public enum LogLevel {
	
	INFO(Level.INFO),
	DEBUG(Level.FINEST);
	
	private Level julLevel;

	private LogLevel(Level julLevel) {
		this.julLevel = julLevel;
		
	}
	
	public Level julLevel() {
		return julLevel;
	}

}
