package com.github.andrasbeni.giftwrap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Giftwrap compiler module creates decorators for 
 * classes and interfaces annotated with <code>GiftWrap</code>. 
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface  GiftWrap {

}
