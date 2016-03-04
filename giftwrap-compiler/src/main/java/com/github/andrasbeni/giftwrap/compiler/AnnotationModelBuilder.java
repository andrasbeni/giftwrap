package com.github.andrasbeni.giftwrap.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.Elements;

import com.github.andrasbeni.giftwrap.InterceptedBy;

public class AnnotationModelBuilder extends AbstractModelBuilder {

	public AnnotationModelBuilder(Elements elementUtils, Element elem) {
		super(elementUtils, elem);
	}
	
	public Map<String, Object> createModel() {
		ensureAnnotation();
		Map<String, Object> map = new HashMap<>();
		map.put("super", getElem().getSimpleName());
		map.put("name", getElem().getSimpleName() + "DefaultImplementation");
		map.put("package", getPackageName());
		List<Map<String, Object>> properties = new ArrayList<>();
		for (Element m : getElem().getEnclosedElements()) {
			if (m.getKind() == ElementKind.METHOD) {
				ExecutableElement prop = (ExecutableElement) m;
				Map<String, Object> property = new TreeMap<>();
				property.put("name", prop.getSimpleName().toString());
				property.put("type", prop.getReturnType().toString());
				if (prop.getDefaultValue() != null) {
					property.put("defaultValue", prop.getDefaultValue());
				}
				properties.add(property);
			}
		}
		map.put("properties", properties);
		return map;
	}
	
	protected void ensureAnnotation() {
		if (getElem().getKind() != ElementKind.ANNOTATION_TYPE) {
			throw new ProcessingException(
					"Only annotations can be annotated with " + InterceptedBy.class.getSimpleName(), getElem());
		}
	}


}
