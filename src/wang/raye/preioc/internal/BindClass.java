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
	/** 包名 */
	private final String classPackage;
	/** 注解处理的类名，通过反射实例化这个类来 处理 */
	private final String className;
	/** 当前被注解的类的全称 */
	private final String targetClass;
	/** ViewBinder的接口实现类 */
	private String parentViewBinder;

	protected BindClass(String classPackage, String className, String targetClass) {
		this.classPackage = classPackage;
		this.className = className;
		this.targetClass = targetClass;
	}

	protected void addField(int id, FieldViewBindTypeAndName binding) {
		getOrCreateViewBindings(id).setField(binding);

	}

	private ViewBindById getOrCreateViewBindings(int id) {
		ViewBindById viewId = viewIdMap.get(id);
		if (viewId == null) {
			viewId = new ViewBindById(id);
			viewIdMap.put(id, viewId);
		}
		return viewId;
	}

	public void setParentViewBinder(String parentViewBinder) {
		this.parentViewBinder = parentViewBinder;
	}

	protected ViewBindById getViewBinding(int id) {
		return viewIdMap.get(id);
	}

	/**
	 * 自动生成Java代码
	 * 
	 * @return
	 */
	protected String toJava() {
		StringBuilder builder = new StringBuilder();
		builder.append("// ProIOC自动生成的代码，请不要修改\n");
		// 设置包名
		builder.append("package ").append(this.classPackage).append(";\n\n");
		// 有控件需要被绑定，导入View包
		if ((!this.viewIdMap.isEmpty())) {
			builder.append("import android.view.View;\n");
		}
		// 需要处理控件绑定，导入接口
		if (this.parentViewBinder == null) {
			builder.append("import wang.raye.preioc.ViewBinder;\n\n");
		}
		// 创建类名
		builder.append("public class ").append(this.className);
		// 创建被注解处理的类的类型
		builder.append("<T extends ").append(this.targetClass).append(">");
		if (this.parentViewBinder != null)
			builder.append(" extends ").append(this.parentViewBinder).append("<T>");
		else {
			builder.append(" implements ViewBinder<T>");
		}
		builder.append(" {\n");

		autoBindMethod(builder);
		builder.append('\n');

		builder.append("}\n");
		return builder.toString();
	}

	/**
	 * 自动生成绑定代码
	 */
	private void autoBindMethod(StringBuilder builder) {
		builder.append("  @Override ")
				.append("public void binder(final Finder finder, final T target, Object source) {\n");
		if (this.parentViewBinder != null) {
			builder.append("    super.binder(finder, target, source);\n\n");
		}
		if((!this.viewIdMap.isEmpty())){
			//需要绑定控件
			for(ViewBindById bindById : viewIdMap.values()){
				autoViewBinding(builder, bindById);
			}
		}
	    builder.append("  }\n");
	}
	
	/**
	 * 自动生成控件获取的代码
	 * @param builder 代码StringBuilder
	 * @param bindById 控件的id与filed绑定的对象
	 */
	private void autoViewBinding(StringBuilder builder,ViewBindById bindById){
		builder.append("    target.")
        .append(bindById.getField().getName())
        .append(" = ");
		builder.append("(").append(bindById.getField().getType()).append(")");
		builder.append("finder.findRequiredView(source")
        .append(", ")
        .append(bindById.getId()).append(", \"")
        .append(bindById.getField().getName());
		
        builder.append("\");\n");
	}
}
