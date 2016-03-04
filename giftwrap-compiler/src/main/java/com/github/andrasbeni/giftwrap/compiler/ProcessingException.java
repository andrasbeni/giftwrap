package com.github.andrasbeni.giftwrap.compiler;

import javax.lang.model.element.Element;

public class ProcessingException extends RuntimeException {
	
		
	private Element elem;

	public ProcessingException(String message, Element elem, Throwable cause) {
		super(message + cause.getMessage(), cause);
		this.elem = elem;
	}
	
	public ProcessingException(String message, Element elem) {
		super(message);
		this.elem = elem;
	}
	
	
	
	public Element getElem() {
		return elem;
	}

}
