package com.github.andrasbeni.giftwrap.compiler;

import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.util.Elements;

public abstract class AbstractModelBuilder {

	private final Elements elementUtils;
	private final Element elem;

	public AbstractModelBuilder(Elements elementUtils, Element elem) {
		this.elementUtils = elementUtils;
		this.elem = elem;
	}

	protected String getPackageName() {
		Element parent = elem.getEnclosingElement();
		if (!(parent instanceof PackageElement)) {
			throw new ProcessingException("Cannot handle nesting", elem);
		}
		return ((QualifiedNameable) parent).getQualifiedName().toString();
	}

	protected String getVisibility(Element e) {
		for (Modifier m : e.getModifiers()) {
			for (Modifier v : new Modifier[] { Modifier.PRIVATE, Modifier.PROTECTED, Modifier.PUBLIC }) {
				if (m == v) {
					return m.toString();
				}
			}
		}
		return "";
	}

	protected Element getElem() {
		return elem;
	}

	protected Elements getElementUtils() {
		return elementUtils;
	}

	public abstract Map<String, Object> createModel();

}