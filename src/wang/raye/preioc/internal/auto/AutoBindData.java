package wang.raye.preioc.internal.auto;

import java.util.LinkedHashMap;

import wang.raye.preioc.internal.DataBinding;

public class AutoBindData {


	/** 记录需要绑定数据的属性及其参数key 属性名，value 参数*/
	private final LinkedHashMap<String, DataBinding> dataBinds;
	/** 注解处理的类名，通过反射实例化这个类来 处理 */
	private final String className;
	/** 包名 */
	private final String classPackage;
	/** 当前被注解的类的全称 */
	private final String targetClass;
	
	
	public AutoBindData(LinkedHashMap<String, DataBinding> dataBinds, String className, String classPackage,
			String targetClass) {
		super();
		this.dataBinds = dataBinds;
		this.className = className;
		this.classPackage = classPackage;
		this.targetClass = targetClass;
	}

	/**
	 * 获取绑定控件数据的名称
	 * @return
	 */
	public String toJava(){
		return new StringBuilder().append(this.classPackage).append(".").
				append(this.className.substring(0,className.lastIndexOf("$")+1)).
				append("ViewDataBinder").toString();

	}
	
	protected String toDataBinderJava(){
		StringBuilder builder = new StringBuilder();
		builder.append("// PreIOC自动生成的代码，请不要修改\n");
		// 设置包名
		builder.append("package ").append(this.classPackage).append(";\n\n");
		builder.append("import android.widget.BaseAdapter;\n");
		builder.append("import wang.raye.preioc.ViewDataBinder;\n\n");
		// 创建类名
		builder.append("public class ").append(className.substring(0,className.lastIndexOf("$$")))
		.append("$$ViewDataBinder")
		// 创建被注解处理的类的类型,前面是ViewHolder
		.append("<T extends ").append(this.targetClass).append(",A extends ")
		//适配器
		.append(targetClass.substring(0, targetClass.lastIndexOf(".")))
		
		.append(">")
		.append(" implements ViewDataBinder<T,A>{\n");
		builder.append("\n");
		
		builder.append('\n');
		
		autoBindDataMethod(builder);
		builder.append("}\n");
		return builder.toString();
	}
	
	/**
	 * 自动生成绑定数据的方法
	 * @param builder
	 */
	private void autoBindDataMethod(StringBuilder builder){
		builder.append("  @Override ")
		.append("	public void bindData(final T t,final A")
		.append(" adapter,int position) {\n");
		
		for(DataBinding binding : dataBinds.values()){
			builder.append("		t."+binding.getFiled()).append(".setText(");
			builder.append("adapter.getItem(position).get")
			.append(toFirstUpperCase(binding.getDataName())).append("());\n");
		}
		
		builder.append("\n}\n");
	}
	
	private String toFirstUpperCase(String str){
//		name = name.substring(0, 1).toUpperCase() + name.substring(1);
//		return  name;
		char[] cs=str.toCharArray();
		if(cs[0] >= 97){
			cs[0]-=32;
		}
		return String.valueOf(cs);
	}
}
