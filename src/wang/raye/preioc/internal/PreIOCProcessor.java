package wang.raye.preioc.internal;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

import wang.raye.preioc.annotation.BindById;
import wang.raye.preioc.annotation.BindData;
import wang.raye.preioc.annotation.OnClick;
import wang.raye.preioc.annotation.OnTouch;

/**
 * ע��Ԥ�������
 * 
 * @author Raye
 */
public class PreIOCProcessor extends AbstractProcessor {
	static final String VIEW_TYPE = "android.view.View";
	private static final String BINDING_CLASS_SUFFIX = "$$ViewBinder";
	private Elements elementUtils;

	private Filer filer;

	public synchronized void init(ProcessingEnvironment env) {
		super.init(env);

		elementUtils = env.getElementUtils();
		filer = env.getFiler();
	}

	/**
	 * ��ȡ������Ҫ�����ע����
	 */
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> types = new LinkedHashSet<>();
		types.add(BindById.class.getCanonicalName());
		types.add(OnClick.class.getCanonicalName());
		return types;
	}

	@Override
	public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
		// ��ע����������������ص�ע���ֵ��
		LinkedHashMap<TypeElement, BindClass> targetClassMap = parseTargets(env);
		for (Map.Entry<TypeElement, BindClass> entry : targetClassMap.entrySet()) {
			TypeElement typeElement = entry.getKey();
			BindClass bindingClass = entry.getValue();

			try {
				JavaFileObject jfo = this.filer.createSourceFile(bindingClass.getViewBinderClassName(), new Element[] { typeElement });
		        Writer writer = jfo.openWriter();
		        writer.write(bindingClass.toJava());
		        writer.flush();
		        writer.close();
		        if(bindingClass.isBindData()){
		        	JavaFileObject djfo = this.filer.createSourceFile(bindingClass.getViewDataBinderCN(), new Element[]{typeElement});
		        	Writer dw = djfo.openWriter();
		        	dw.write(bindingClass.toDataBinderJava());
		        	dw.flush();
		        	dw.close();
//		        	writeLog(bindingClass.toDataBinderJava());
		        }
//					writeLog(bindingClass.toJava());
			} catch (IOException e) {
				error(typeElement, "Unable to write view binder for type %s: %s", typeElement, e.getMessage());
			}
		}
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
		// ��Щ���Լ��Ǵ������
		LinkedHashSet<String> erasedTargetNames = new LinkedHashSet<>();
		for (Element element : env.getElementsAnnotatedWith(BindById.class)) {
			// �󶨿ؼ���
			try {
				parseBindById(element, targets, erasedTargetNames);
			} catch (Exception e) {
				error(element, BindById.class.getName() + "parse error:%s", e);
			}
		}
		//������
		for(Element element : env.getElementsAnnotatedWith(BindData.class)){
			try{
				parseBindData(element, targets, erasedTargetNames);
			}catch (Exception e) {
				error(element, BindData.class.getName() + "parse error:%s", e);
			}
		}
		
		for(Element element : env.getElementsAnnotatedWith(OnClick.class)){
			//�󶨵���¼���
			try{
				parseLisenter(element, targets, erasedTargetNames,OnClick.class);
			}catch(Exception e){
				error(element, OnClick.class.getName() + "parse error:%s", e);
			}
		}
		for(Element element : env.getElementsAnnotatedWith(OnTouch.class)){
			//��Touch�¼���
			try{
				parseLisenter(element, targets, erasedTargetNames,OnTouch.class);
			}catch(Exception e){
				error(element, OnTouch.class.getName() + "parse error:%s", e);
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
		} else {
			parseBindOne(element, targets, erasedTargetNames);
		}
	}

	private void parseBindOne(Element element, Map<TypeElement, BindClass> targets, Set<String> erasedTargetNames) {
		boolean hasError = false;
		// ��ȡ����ǰע��Element���ڵ����TypeElement��toString���ڵ�ǰ������������������
		TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
		// ��ȡ��ǰ��ע������Ե����͵�TypeMirror
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
				return;
			}
		} else {
			bindingClass = getOrCreateTargetClass(targets, enclosingElement);
		}
		//��ȡ��ע�����������
		String name = element.getSimpleName().toString();
//		writeLog("this elementType kind is:%s", elementType.toString());

		//��ȡ��ע������Ե�����
		String typeName = elementType.toString();
		FieldViewBindTypeAndName binding = new FieldViewBindTypeAndName(name, typeName);
		bindingClass.addField(id, binding);
//		writeLog(
//				"parseBindOne erasedTargetNames.add(%s)  ,new FieldViewBindTyp"
//						+ "eAndName(%s, %s)   bindingClass.addField(%s,binding)",
//				enclosingElement.toString(), name, elementType.getKind().name(), id);

		erasedTargetNames.add(enclosingElement.toString());
	}

	/**
	 * ��ȡ���ߴ���һ��BindClass��
	 * 
	 * @param targetClassMap
	 *            ע�����ڵ�����ע��󶨵����Լ�ֵ�Լ���
	 * @param enclosingElement
	 *            ��ǰ�������
	 * @return
	 */
	private BindClass getOrCreateTargetClass(Map<TypeElement, BindClass> targetClassMap, TypeElement enclosingElement) {
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
	
	private void parseLisenter(Element element, LinkedHashMap<TypeElement, BindClass> targets,
			LinkedHashSet<String> erasedTargetNames,Class annotationClass) throws Exception{
		
		//�����ϲ����ж��Ƿ���ʹ���ڷ����ϵ�
		//
		ExecutableElement executableElement = (ExecutableElement) element;
		// ��ȡ����ǰע��Element���ڵ����TypeElement��toString���ڵ�ǰ������������������
	    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
	    //��ȡvalue��ֵ�����ڲ�֪�ƶ������������Բ���ʹ�÷���ֱ�ӻ�ȡ��
	    Annotation annotation = element.getAnnotation(annotationClass);
	    Method annotationValue = annotationClass.getDeclaredMethod("value");
	   //�������ǲ����ж�value�ǲ���int�͵�
	    //��ȡvalue��ֵ
	    int[] ids = (int[]) annotationValue.invoke(annotation);
	    //��ע��ķ�������
	    String methonName = executableElement.getSimpleName().toString();
	    
	    BindClass bindingClass = targets.get(enclosingElement);
	    if(bindingClass == null){
	    	bindingClass = getOrCreateTargetClass(targets, enclosingElement);
	    }
	    if(annotationClass == OnClick.class){
	    	bindingClass.addOnClick(ids, methonName);
	    }else if(annotationClass == OnTouch.class){
	    	bindingClass.addOnTouch(ids, methonName);
	    }
	    
	    erasedTargetNames.add(enclosingElement.toString());
	}
	
	/**
	 * ��¼�����ݵ�
	 * @param element
	 * @param targets
	 * @param erasedTargetNames
	 */
	private void parseBindData(Element element, LinkedHashMap<TypeElement, BindClass> targets,
			LinkedHashSet<String> erasedTargetNames){
		TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
		if (!isSubtypeOfType(element.asType(), VIEW_TYPE) ) {
			error(element, "@%s ���Ա�����View������ (%s.%s)", BindById.class.getSimpleName(),
					enclosingElement.getQualifiedName(), element.getSimpleName());
			return ;
		}
		//��ȡҪ�󶨵���������
		BindData bindData = element.getAnnotation(BindData.class);
		String dataName = bindData.value();
		String format = bindData.format();
		//��ȡ��ע�������
		String filedName = element.getSimpleName().toString();
	
		BindClass bindingClass = targets.get(enclosingElement);
	    if(bindingClass != null){
	    	bindingClass.addDataBind(filedName, dataName,getClassNameInClass(enclosingElement),format);
	    }else{
	    	//����һ����ע�����
	    	bindingClass = getOrCreateTargetClass(targets, enclosingElement);
	    	bindingClass.addDataBind(filedName, dataName,getClassNameInClass(enclosingElement),format);
	    }
	
	}

	
	/**
	 * ��ȡ��ǰ��TypeElement���ڵ������ڵ�������ƣ����е��ࣩ
	 * ��Ҫ��parseBindData�õ�
	 * @param type
	 * @return (������)
	 */
	private String getClassNameInClass(TypeElement type){
		String clzName = type.getQualifiedName().toString();
		return clzName.substring(0,clzName.lastIndexOf("."));
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
	public static void writeLog(String message, Object... args) {
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
