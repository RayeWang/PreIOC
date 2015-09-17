package wang.raye.preioc.internal;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;

import com.squareup.javapoet.TypeName;

import wang.raye.preioc.annotation.BindById;


/**
 * ע��Ԥ�������
 * 
 * @author Ray
 */
public class ProIOCProcessor extends AbstractProcessor {
	static final String VIEW_TYPE = "android.view.View";
	private static final String BINDING_CLASS_SUFFIX = "$$ViewBinder";
	private Elements elementUtils;

	public synchronized void init(ProcessingEnvironment env) {
		super.init(env);

		elementUtils = env.getElementUtils();
	}
	
	/**
	 * ��ȡ������Ҫ�����ע����
	 */
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> types = new LinkedHashSet<>();
		types.add(BindById.class.getCanonicalName());
		return types;
	}

	@Override
	public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
		writeLog("this is ProIOC process ");
		//��ע����������������ص�ע���ֵ��
		LinkedHashMap<TypeElement, BindClass> targetClassMap = parseTargets(env);
		return true;
	}

	/**
	 * ��ȡ����
	 * 
	 * @param evn
	 * @return
	 */
	private LinkedHashMap<TypeElement, BindClass> parseTargets(RoundEnvironment env) {
		LinkedHashMap<TypeElement, BindClass> targets = new LinkedHashMap<>();
		//��Щ���Լ��Ǵ������
		LinkedHashSet<String> erasedTargetNames = new LinkedHashSet<>();
		for (Element element : env.getElementsAnnotatedWith(BindById.class)) {
			//�󶨿ؼ���
			try {
				parseBindById(element, targets, erasedTargetNames);
			} catch (Exception e) {
				error(element, BindById.class.getName() + "parse error:%s", e);
			}
		}
		return targets;
	}

	/**
	 * ��ȡ���и���id�󶨿ؼ���
	 * 
	 * @param element
	 * @param targets
	 * @param erasedTargetNames
	 */
	private void parseBindById(Element element, LinkedHashMap<TypeElement, BindClass> targets,
			LinkedHashSet<String> erasedTargetNames) {
		if (isInaccessibleViaGeneratedCode(BindById.class, "fields", element)) {
			return;
		}
		TypeMirror elementType = element.asType();
		if (elementType.getKind() == TypeKind.ARRAY) {
			// ����󶨣���ʱ����
			// parseBindMany(element, targets, erasedTargetNames);
		}else{
			parseBindOne(element, targets, erasedTargetNames);
		}
	}

	private void parseBindOne(Element element, Map<TypeElement, BindClass> targets, Set<String> erasedTargetNames) {
		boolean hasError = false;
		//��ȡ����ǰע��Element���ڵ����TypeElement
		TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
		//��ȡ��ǰ��ע������Ե����͵�TypeMirror
		TypeMirror elementType = element.asType();
		if (elementType.getKind() == TypeKind.TYPEVAR) {
			TypeVariable typeVariable = (TypeVariable) elementType;
			elementType = typeVariable.getUpperBound();
		}
		if (!isSubtypeOfType(elementType, VIEW_TYPE) && !isInterface(elementType)) {
			error(element, "@%s ���Ա�����View�����ಢ�Ҳ����ǽӿ�. (%s.%s)", BindById.class.getSimpleName(),
					enclosingElement.getQualifiedName(), element.getSimpleName());
			hasError = true;
		}

		int id = element.getAnnotation(BindById.class).value();
		if (id == 0) {
			error(element, "@%s ��������ȷ��idֵ: %s. (%s.%s)", BindById.class.getSimpleName(), id,
					enclosingElement.getQualifiedName(), element.getSimpleName());
			hasError = true;
		}

		if (hasError) {
			return;
		}

		BindClass bindingClass = targets.get(enclosingElement);
		if (bindingClass != null) {
			ViewBindById viewBindings = bindingClass.getViewBinding(id);
			if (viewBindings != null) {
				// id�Ѿ���ؼ���
				// TODO �Ƿ�����һ����ķ�Χ������Ƕ����᲻���ͻ
				return;
			}
		} else {
			bindingClass = getOrCreateTargetClass(targets, enclosingElement);
		}

		String name = element.getSimpleName().toString();
		writeLog("this elementType kind is:%s", elementType.getKind().name());

		TypeName type = TypeName.get(elementType);

		FieldViewBindTypeAndName binding = new FieldViewBindTypeAndName(name, type);
		bindingClass.addField(id, binding);
		writeLog("parseBindOne erasedTargetNames.add(%s)  ,new FieldViewBindTyp"
				+ "eAndName(%s, %s)   bindingClass.addField(%s,binding)",
				enclosingElement.toString(),name,type,id);
		
		erasedTargetNames.add(enclosingElement.toString());
	}

	/**
	 * ��ȡ���ߴ���һ��BindClass��
	 * @param targetClassMap ע�����ڵ�����ע��󶨵����Լ�ֵ�Լ���
	 * @param enclosingElement ��ǰ�������
	 * @return
	 */
	private BindClass getOrCreateTargetClass(Map<TypeElement, BindClass> targetClassMap,
			TypeElement enclosingElement) {
		writeLog("getOrCreateTargetClass TypeElement:%s",enclosingElement.getQualifiedName().toString());
		BindClass bindingClass = targetClassMap.get(enclosingElement);
		if (bindingClass == null) {
			String targetType = enclosingElement.getQualifiedName().toString();
			String classPackage = getPackageName(enclosingElement);
			
			String className = getClassName(enclosingElement, classPackage) + BINDING_CLASS_SUFFIX;

			bindingClass = new BindClass(classPackage, className, targetType);
			targetClassMap.put(enclosingElement, bindingClass);
		}
		return bindingClass;
	}

	/**
	 * ��ȡ����
	 * 
	 * @param type
	 * @param packageName
	 * @return
	 */
	private static String getClassName(TypeElement type, String packageName) {
		int packageLen = packageName.length() + 1;
		return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
	}

	/**
	 * ��ȡ����
	 * 
	 * @param type
	 * @return
	 */
	private String getPackageName(TypeElement type) {
		return elementUtils.getPackageOf(type).getQualifiedName().toString();
	}

	/**
	 * �ж��Ƿ��ǽӿ�
	 * 
	 * @param typeMirror
	 * @return
	 */
	private boolean isInterface(TypeMirror typeMirror) {
		return typeMirror instanceof DeclaredType && ((DeclaredType) typeMirror).asElement().getKind() == INTERFACE;
	}

	/**
	 * �ж϶����Ƿ�̳���ָ���Ļ���
	 * 
	 * @param typeMirror
	 * @param otherType
	 * @return
	 */
	private boolean isSubtypeOfType(TypeMirror typeMirror, String otherType) {
		if (otherType.equals(typeMirror.toString())) {
			return true;
		}
		if (typeMirror.getKind() != TypeKind.DECLARED) {
			return false;
		}
		DeclaredType declaredType = (DeclaredType) typeMirror;
		List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
		if (typeArguments.size() > 0) {
			StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());
			typeString.append('<');
			for (int i = 0; i < typeArguments.size(); i++) {
				if (i > 0) {
					typeString.append(',');
				}
				typeString.append('?');
			}
			typeString.append('>');
			if (typeString.toString().equals(otherType)) {
				return true;
			}
		}
		Element element = declaredType.asElement();
		if (!(element instanceof TypeElement)) {
			return false;
		}
		TypeElement typeElement = (TypeElement) element;
		TypeMirror superType = typeElement.getSuperclass();
		if (isSubtypeOfType(superType, otherType)) {
			return true;
		}
		for (TypeMirror interfaceType : typeElement.getInterfaces()) {
			if (isSubtypeOfType(interfaceType, otherType)) {
				return true;
			}
		}
		return false;
	}

	

	/**
	 * �ж�ע��ʹ��ʹ�ô���
	 * 
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
		// �ж������Ƿ���˽�еĻ��߾�̬��
		if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
			error(element, "@%s %s ע������Բ�����private��static. (%s.%s)", annotationClass.getSimpleName(), targetThing,
					enclosingElement.getQualifiedName(), element.getSimpleName());
			hasError = true;
		}

		// �ж��Ƿ�ʹ����������
		if (enclosingElement.getKind() != CLASS) {
			error(enclosingElement, "@%s %s ��ע�ⲻ��������ʹ��. (%s.%s)", annotationClass.getSimpleName(), targetThing,
					enclosingElement.getQualifiedName(), element.getSimpleName());
			hasError = true;
		}

		// �ж��Ƿ�������˽��������
		if (enclosingElement.getModifiers().contains(PRIVATE)) {
			error(enclosingElement, "@%s %s ����Ϊ˽�л�������ע��. (%s.%s)", annotationClass.getSimpleName(), targetThing,
					enclosingElement.getQualifiedName(), element.getSimpleName());
			hasError = true;
		}

		return hasError;
	}

	/**
	 * ��ӡ������Ϣ
	 * 
	 * @param element
	 * @param message
	 * @param args
	 */
	private void error(Element element, String message, Object... args) {
		if (args.length > 0) {
			message = String.format(message, args);
			writeLog(message, args);
		}
		processingEnv.getMessager().printMessage(ERROR, message, element);
	}

	/**
	 * ��¼������־
	 * 
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
	private void writeLog(String message) {
		try {
			FileWriter fw = new FileWriter(new File("D:/PreIOCLog.txt"), true);
			fw.write(message + "\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
