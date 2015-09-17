package wang.raye.preioc.internal;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 保存类中的注解与相关的属性，方法绑定的值
 * 
 * @author Raye
 *
 */
public final class BindClass {
	/** 控件与ID绑定的集合 */
	private final Map<Integer, ViewBindById> viewIdMap = new LinkedHashMap<>();
	/** 包名*/
	private final String classPackage;
	/** 注解处理的类名，通过反射实例化这个类来 处理*/
	private final String className;
	/** 当前被注解的类的全称*/
	private final String targetClass;

	protected BindClass(String classPackage, String className, String targetClass) {
		this.classPackage = classPackage;
		this.className = className;
		this.targetClass = targetClass;
	}

	protected void addField(int id, FieldViewBindTypeAndName binding) {
		getOrCreateViewBindings(id).setField(binding);
		;
	}

	private ViewBindById getOrCreateViewBindings(int id) {
		ViewBindById viewId = viewIdMap.get(id);
		if (viewId == null) {
			viewId = new ViewBindById(id);
			viewIdMap.put(id, viewId);
		}
		return viewId;
	}

	protected ViewBindById getViewBinding(int id) {
		return viewIdMap.get(id);
	}
}
