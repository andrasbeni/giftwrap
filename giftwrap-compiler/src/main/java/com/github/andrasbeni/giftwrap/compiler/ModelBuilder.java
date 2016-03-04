package com.github.andrasbeni.giftwrap.compiler;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.lang.model.element.ElementKind.METHOD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import com.github.andrasbeni.giftwrap.InterceptedBy;

public class ModelBuilder extends AbstractModelBuilder {

	public ModelBuilder(Elements elementUtils, Element elem) {
		super(elementUtils, elem);
	}


	private Map<String, String> interceptorNames = new HashMap<>();
	private Map<String, Map<String, Object>> defaultInterceptorParams = new HashMap<>(); 
	private List<Map<String, Object>> constructors = new ArrayList<>();
	private List<Map<String, Object>> methods = new ArrayList<>();
	private List<Map<String, Object>> interceptors = new ArrayList<>();
	private int interceptorCounter = 0;

	public Map<String, Object> createModel() {
		if (!(getElem().getKind() == INTERFACE
				|| getElem().getKind() == CLASS && isExtensible(getElem()))) {
			throw new ProcessingException("Cannot subclass " + getElem().getSimpleName(), getElem());
		}
		processAnnotationsOn(getElem(), defaultInterceptorParams);
		
		List<? extends Element> elementsToProcess = 
				getElem().getKind() == CLASS 
				? getElem().getEnclosedElements() 
			    : getElementUtils().getAllMembers((TypeElement)getElem());
		for (Element child : elementsToProcess) {
			switch (child.getKind()) {
			case CONSTRUCTOR:
				ExecutableElement init = (ExecutableElement) child;
				Map<String, Object> model = new HashMap<>();
				model.put("visibility", getVisibility(init));
				model.put("parameters", collectParameters(init));
				model.put("exceptions", collectThrownTypes(init));
				constructors.add(model);
				break;
			case METHOD:
				ExecutableElement m = (ExecutableElement) child;
				if (m.getKind() == METHOD && isExtensible(m) && (!getInterceptedAnnotations(m).isEmpty()
							|| getElem().getKind() == INTERFACE || !defaultInterceptorParams.isEmpty())) {
					methods.add(buildMethodModel(m));
				}
				break;
			default:
				break;
				
			
			}
		}

		Map<String, Object> map = new HashMap<>();
		map.put("super", getElem().getSimpleName());
		map.put("name", getElem().getSimpleName() + "Intercepted");
		map.put("package", getPackageName());
		map.put("visibility", getVisibility(getElem()));
		map.put("methods", methods);
		map.put("constructors", constructors);
		map.put("interceptors", interceptors);
		return map;
	}


	private void processAnnotationsOn(Element el, Map<String, Map<String, Object>> interceptorParams) {
		for (AnnotationMirror mirror : el.getAnnotationMirrors()) {
	    	if (mirror.getAnnotationType().asElement().getAnnotation(InterceptedBy.class) != null) {
	    		String className = getInterceptorClassName(mirror);
				if (!interceptorNames.containsKey(className)) {
					String fieldName = "$interceptor" + (interceptorCounter++);
					interceptorNames.put(className, fieldName);
					Map<String, Object> interceptor = new HashMap<String, Object>();
					interceptor.put("type", className);
					interceptor.put("name", fieldName);
					interceptors.add(interceptor);
				}
				
				List<Map<String, Object>> properties = new ArrayList<>(); 
				for (Entry<? extends ExecutableElement, ? extends AnnotationValue> i : mirror.getElementValues()
						.entrySet()) {
					Map<String, Object> property = new HashMap<>();
					property.put("name", i.getKey().getSimpleName().toString());
					property.put("value", i.getValue().toString());
					properties.add(property);
				
				}
				Map<String, Object> annotation = new HashMap<>();
				annotation.put("type", mirror.getAnnotationType().asElement().toString());
				annotation.put("properties", properties);
				interceptorParams.put(className, annotation);
			}
	    }
	}

	private List<Map<String, Object>> collectParameters(ExecutableElement exe) {
		List<Map<String, Object>> parameters = new ArrayList<>();
		for (VariableElement par : exe.getParameters()) {
			Map<String, Object> parameter = new HashMap<>();
			parameter.put("name", par.getSimpleName().toString());
			parameter.put("type", par.asType().toString());
			parameters.add(parameter);

		}
		return parameters;
	}
	
	
	private List<Map<String, Object>> collectTypeParameters(ExecutableElement exe) {
		List<Map<String, Object>> parameters = new ArrayList<>();
		for (TypeParameterElement par : exe.getTypeParameters()) {
			Map<String, Object> parameter = new HashMap<>();
			parameter.put("name", par.toString());
			parameter.put("type", par.asType().toString());
			parameters.add(parameter);

		}
		return parameters;
	}
	private List<String> collectThrownTypes(ExecutableElement exe) {
		List<String> types = new ArrayList<>();
		for (TypeMirror type : exe.getThrownTypes()) {
			types.add(type.toString());
		}
		return types;
	}

	private Map<String, Object> buildMethodModel(ExecutableElement exe) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("visibility", getVisibility(exe));
		model.put("name", exe.getSimpleName().toString());
		model.put("returnType", "void".equals(exe.getReturnType().toString()) ? null : exe.getReturnType().toString());
		model.put("parameters", collectParameters(exe));
		model.put("exceptions", collectThrownTypes(exe));
		model.put("typeParameters", collectTypeParameters(exe));
		Map<String, Map<String, Object>> interceptorParams = new LinkedHashMap<>(defaultInterceptorParams);
		processAnnotationsOn(exe, interceptorParams );
		model.put("annotations", toAnnotationList(interceptorParams));
		if (!interceptorParams.isEmpty()) {
			model.put("intercepted", true);
		}
		return model;
	}

	
	private List<Map<String, Object>> toAnnotationList(Map<String, Map<String, Object>> interceptorParams) {
		List<Map<String, Object>> list = new ArrayList<>();
		for (Entry<String, Map<String, Object>> entry : interceptorParams.entrySet()) {
			Map<String, Object> annotation = new HashMap<>();
			annotation.put("type", entry.getValue().get("type"));
			annotation.put("properties", entry.getValue().get("properties"));
			annotation.put("interceptorName", interceptorNames.get(entry.getKey()));
			
			list.add(annotation );
		}
		return list ;
	}

	private String getInterceptorClassName(AnnotationMirror annot) {
		InterceptedBy intercepted = annot.getAnnotationType().asElement().getAnnotation(InterceptedBy.class);
		try {
			return intercepted.value().getCanonicalName();
		} catch (MirroredTypeException e) {
			return e.getTypeMirror().toString();
		}
	}

	private List<AnnotationMirror> getInterceptedAnnotations(Element m) {
		List<AnnotationMirror> annotations = new ArrayList<>();
		for (AnnotationMirror mirror : m.getAnnotationMirrors()) {
			if (mirror.getAnnotationType().asElement().getAnnotation(InterceptedBy.class) != null) {
				annotations.add(mirror);
			}
		}
		return annotations;
	}

	private static boolean isExtensible(Element elem) {
		return !(elem.getModifiers().contains(Modifier.FINAL) || elem.getModifiers().contains(Modifier.PRIVATE));
	}
}

