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
 * ע��Ԥ�������
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
	 * �ж�ע��ʹ��ʹ�ô���
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
		//�ж������Ƿ���˽�еĻ��߾�̬��
		if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
			error(element, "@%s %s ���Բ�����private��static. (%s.%s)", annotationClass.getSimpleName(),
					targetThing, enclosingElement.getQualifiedName(), element.getSimpleName());
			writeLog(String.format("@%s %s ���Բ�����private��static. (%s.%s)",annotationClass.getSimpleName(),
					targetThing, enclosingElement.getQualifiedName(), element.getSimpleName()));
			hasError = true;
		}

		// �ж��Ƿ�ʹ����������
		if (enclosingElement.getKind() != CLASS) {
			error(enclosingElement, "@%s %s ��ע�ⲻ��������ʹ��. (%s.%s)", annotationClass.getSimpleName(),
					targetThing, enclosingElement.getQualifiedName(), element.getSimpleName());
			writeLog("@%s %s ��ע�ⲻ��������ʹ��. (%s.%s)", annotationClass.getSimpleName(),
					targetThing, enclosingElement.getQualifiedName(), element.getSimpleName());
			hasError = true;
		}

		// �ж��Ƿ�������˽��������
		if (enclosingElement.getModifiers().contains(PRIVATE)) {
			error(enclosingElement, "@%s %s �಻����˽�л�����. (%s.%s)",
					annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
					element.getSimpleName());
			writeLog("@%s %s �಻����˽�л�����. (%s.%s)",
					annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
					element.getSimpleName());
			hasError = true;
		}

		return hasError;
	}
	
	/**
	 * ��ӡ������Ϣ
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
	 * ��¼������־
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
