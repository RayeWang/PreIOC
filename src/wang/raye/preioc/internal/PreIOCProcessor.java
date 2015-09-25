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
 * 注解预处理的类
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
	 * 获取所有需要处理的注解类
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
		// 被注解的类和类中属性相关的注解键值对
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
	 * 获取所有
	 * 
	 * @param evn
	 * @return
	 */
	private LinkedHashMap<TypeElement, BindClass> parseTargets(RoundEnvironment env) {
		LinkedHashMap<TypeElement, BindClass> targets = new LinkedHashMap<>();
		// 哪些类以及是处理过的
		LinkedHashSet<String> erasedTargetNames = new LinkedHashSet<>();
		for (Element element : env.getElementsAnnotatedWith(BindById.class)) {
			// 绑定控件的
			try {
				parseBindById(element, targets, erasedTargetNames);
			} catch (Exception e) {
				error(element, BindById.class.getName() + "parse error:%s", e);
			}
		}
		//绑定数据
		for(Element element : env.getElementsAnnotatedWith(BindData.class)){
			try{
				parseBindData(element, targets, erasedTargetNames);
			}catch (Exception e) {
				error(element, BindData.class.getName() + "parse error:%s", e);
			}
		}
		
		for(Element element : env.getElementsAnnotatedWith(OnClick.class)){
			//绑定点击事件的
			try{
				parseLisenter(element, targets, erasedTargetNames,OnClick.class);
			}catch(Exception e){
				error(element, OnClick.class.getName() + "parse error:%s", e);
			}
		}
		for(Element element : env.getElementsAnnotatedWith(OnTouch.class)){
			//绑定Touch事件的
			try{
				parseLisenter(element, targets, erasedTargetNames,OnTouch.class);
			}catch(Exception e){
				error(element, OnTouch.class.getName() + "parse error:%s", e);
			}
		}
		return targets;
	}

	/**
	 * 获取所有根据id绑定控件的
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
			// 多个绑定，暂时不做
			// parseBindMany(element, targets, erasedTargetNames);
		} else {
			parseBindOne(element, targets, erasedTargetNames);
		}
	}

	private void parseBindOne(Element element, Map<TypeElement, BindClass> targets, Set<String> erasedTargetNames) {
		boolean hasError = false;
		// 获取被当前注解Element所在的类的TypeElement（toString等于当前的类名（含包名））
		TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
		// 获取当前被注解的属性的类型的TypeMirror
		TypeMirror elementType = element.asType();
		if (elementType.getKind() == TypeKind.TYPEVAR) {
			TypeVariable typeVariable = (TypeVariable) elementType;
			elementType = typeVariable.getUpperBound();
		}
		if (!isSubtypeOfType(elementType, VIEW_TYPE) && !isInterface(elementType)) {
			error(element, "@%s 属性必须是View的子类并且不能是接口. (%s.%s)", BindById.class.getSimpleName(),
					enclosingElement.getQualifiedName(), element.getSimpleName());
			hasError = true;
		}

		int id = element.getAnnotation(BindById.class).value();
		if (id == 0) {
			error(element, "@%s 必须是正确的id值: %s. (%s.%s)", BindById.class.getSimpleName(), id,
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
		//获取被注解的属性名称
		String name = element.getSimpleName().toString();
//		writeLog("this elementType kind is:%s", elementType.toString());

		//获取被注解的属性的类型
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
	 * 获取或者创建一个BindClass，
	 * 
	 * @param targetClassMap
	 *            注解所在的类与注解绑定的属性键值对集合
	 * @param enclosingElement
	 *            当前处理的类
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
		
		//理论上不用判断是否是使用在方法上的
		//
		ExecutableElement executableElement = (ExecutableElement) element;
		// 获取被当前注解Element所在的类的TypeElement（toString等于当前的类名（含包名））
	    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
	    //获取value的值（由于不知制定的类名，所以不能使用方法直接获取）
	    Annotation annotation = element.getAnnotation(annotationClass);
	    Method annotationValue = annotationClass.getDeclaredMethod("value");
	   //理论上是不用判断value是不是int型的
	    //获取value的值
	    int[] ids = (int[]) annotationValue.invoke(annotation);
	    //被注解的方法名称
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
	 * 记录绑定数据的
	 * @param element
	 * @param targets
	 * @param erasedTargetNames
	 */
	private void parseBindData(Element element, LinkedHashMap<TypeElement, BindClass> targets,
			LinkedHashSet<String> erasedTargetNames){
		TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
		if (!isSubtypeOfType(element.asType(), VIEW_TYPE) ) {
			error(element, "@%s 属性必须是View的子类 (%s.%s)", BindById.class.getSimpleName(),
					enclosingElement.getQualifiedName(), element.getSimpleName());
			return ;
		}
		//获取要绑定的数据名称
		BindData bindData = element.getAnnotation(BindData.class);
		String dataName = bindData.value();
		String format = bindData.format();
		//获取被注解的属性
		String filedName = element.getSimpleName().toString();
	
		BindClass bindingClass = targets.get(enclosingElement);
	    if(bindingClass != null){
	    	bindingClass.addDataBind(filedName, dataName,getClassNameInClass(enclosingElement),format);
	    }else{
	    	//创建一个被注解的类
	    	bindingClass = getOrCreateTargetClass(targets, enclosingElement);
	    	bindingClass.addDataBind(filedName, dataName,getClassNameInClass(enclosingElement),format);
	    }
	
	}

	
	/**
	 * 获取当前的TypeElement所在的类所在的类的名称（类中的类）
	 * 主要是parseBindData用的
	 * @param type
	 * @return (含包名)
	 */
	private String getClassNameInClass(TypeElement type){
		String clzName = type.getQualifiedName().toString();
		return clzName.substring(0,clzName.lastIndexOf("."));
	}
	/**
	 * 获取类名
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
	 * 获取包名
	 * 
	 * @param type
	 * @return
	 */
	private String getPackageName(TypeElement type) {
		return elementUtils.getPackageOf(type).getQualifiedName().toString();
	}

	/**
	 * 判断是否是接口
	 * 
	 * @param typeMirror
	 * @return
	 */
	private boolean isInterface(TypeMirror typeMirror) {
		return typeMirror instanceof DeclaredType && ((DeclaredType) typeMirror).asElement().getKind() == INTERFACE;
	}

	/**
	 * 判断对象是否继承自指定的基类
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
	 * 判断注解使用使用错误
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
		// 判断属性是否是私有的或者静态的
		if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
			error(element, "@%s %s 注解的属性不能是private或static. (%s.%s)", annotationClass.getSimpleName(), targetThing,
					enclosingElement.getQualifiedName(), element.getSimpleName());
			hasError = true;
		}

		// 判断是否使用在了类上
		if (enclosingElement.getKind() != CLASS) {
			error(enclosingElement, "@%s %s 此注解不能在类上使用. (%s.%s)", annotationClass.getSimpleName(), targetThing,
					enclosingElement.getQualifiedName(), element.getSimpleName());
			hasError = true;
		}

		// 判断是否用在了私有类上面
		if (enclosingElement.getModifiers().contains(PRIVATE)) {
			error(enclosingElement, "@%s %s 不能为私有或匿名类注解. (%s.%s)", annotationClass.getSimpleName(), targetThing,
					enclosingElement.getQualifiedName(), element.getSimpleName());
			hasError = true;
		}

		return hasError;
	}

	/**
	 * 打印错误消息
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
	 * 记录错误日志
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
