package com.github.andrasbeni.giftwrap.compiler;

import static javax.lang.model.element.ElementKind.CLASS;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import com.github.andrasbeni.giftwrap.GiftWrap;
import com.github.andrasbeni.giftwrap.InterceptedBy;
import com.google.auto.service.AutoService;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

@AutoService(Processor.class)
public class GiftWrapProcessor extends AbstractProcessor {

	private static Configuration cfg = initConfig();

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (Element elem : roundEnv.getElementsAnnotatedWith(InterceptedBy.class)) {
			try {
				AbstractModelBuilder modelBuilder = new AnnotationModelBuilder(processingEnv.getElementUtils(), elem);
				Map<String, Object> model = modelBuilder.createModel();
				writeImplementation(elem, model, "AnnotationDefaultImplementation.ftl");
			} catch (ProcessingException e) {
				processingEnv.getMessager().printMessage(Kind.ERROR, e.getMessage(), elem);
			}
		}
		for (Element elem : roundEnv.getElementsAnnotatedWith(GiftWrap.class)) {
			try {
				ModelBuilder modelBuilder = new ModelBuilder(processingEnv.getElementUtils(), elem);
				Map<String, Object> model = modelBuilder.createModel();
				writeImplementation(elem, model,
						elem.getKind() == CLASS 
						    ? "ClassIntercepted.ftl" 
						    : "InterfaceIntercepted.ftl");
			} catch (ProcessingException e) {
				processingEnv.getMessager().printMessage(Kind.ERROR, e.getMessage(), elem);
			}
		}
		return false;
	}

	private static Configuration initConfig() {
		Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		cfg.setClassLoaderForTemplateLoading(GiftWrapProcessor.class.getClassLoader(), "/");
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		return cfg;
	}

	private void writeImplementation(Element elem, Map<String, Object> model, String templateName) {
		String packageName = (String) model.get("package");
		String className = (String) model.get("name");
		
		try (OutputStream os = processingEnv.getFiler()
				.createSourceFile(packageName + "." + className, elem)
				.openOutputStream(); Writer out = new OutputStreamWriter(os)) {
			Template template = cfg.getTemplate(templateName);
			template.process(model, out);
		} catch (TemplateException | IOException ex) {
			throw new ProcessingException("Cannot write file: ", elem, ex);
		}
	}


	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return new HashSet<String>(
				Arrays.asList(GiftWrap.class.getCanonicalName(), InterceptedBy.class.getCanonicalName()));
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

}
