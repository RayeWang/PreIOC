package com.ray.preioc;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import com.ray.preioc.annotation.BindById;

/**
 * 注解预处理的类
 * 
 * @author Ray
 */
public class ProIOCProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> arg0, RoundEnvironment arg1) {
		return true;
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> types = new LinkedHashSet<>();
		types.add(BindById.class.getCanonicalName());
		return types;
	}

	/**
	 * 判断注解使用使用错误
	 * @param annotationClass
	 * @param targetThing
	 * @param element
	 * @return
	 */
	private boolean isInaccessibleViaGeneratedCode(Class<? extends Annotation> annotationClass, String targetThing,
			Element element) {
		boolean hasError = false;
		TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
			
		Set<Modifier> modifiers = element.getModifiers();
		//判断属性是否是私有的或者静态的
		if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
			error(element, "@%s %s 属性不能是private或static. (%s.%s)", annotationClass.getSimpleName(),
					targetThing, enclosingElement.getQualifiedName(), element.getSimpleName());
			writeLog(String.format("@%s %s 属性不能是private或static. (%s.%s)",annotationClass.getSimpleName(),
					targetThing, enclosingElement.getQualifiedName(), element.getSimpleName()));
			hasError = true;
		}

		// 判断是否使用在了类上
		if (enclosingElement.getKind() != CLASS) {
			error(enclosingElement, "@%s %s 此注解不能在类上使用. (%s.%s)", annotationClass.getSimpleName(),
					targetThing, enclosingElement.getQualifiedName(), element.getSimpleName());
			writeLog("@%s %s 此注解不能在类上使用. (%s.%s)", annotationClass.getSimpleName(),
					targetThing, enclosingElement.getQualifiedName(), element.getSimpleName());
			hasError = true;
		}

		// 判断是否用在了私有类上面
		if (enclosingElement.getModifiers().contains(PRIVATE)) {
			error(enclosingElement, "@%s %s 类不能是私有或匿名. (%s.%s)",
					annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
					element.getSimpleName());
			writeLog("@%s %s 类不能是私有或匿名. (%s.%s)",
					annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
					element.getSimpleName());
			hasError = true;
		}

		return hasError;
	}
	
	/**
	 * 打印错误消息
	 * @param element
	 * @param message
	 * @param args
	 */
	private void error(Element element, String message, Object... args) {
		if (args.length > 0) {
			message = String.format(message, args);
		}
		processingEnv.getMessager().printMessage(ERROR, message, element);
	}
	/**
	 * 记录错误日志
	 * @param str
	 */
	private void writeLog(String message, Object... args) {
		try {
			FileWriter fw = new FileWriter(new File("D:/PreIOCLog.txt"), true);
			fw.write(String.format(message, args) + "\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
