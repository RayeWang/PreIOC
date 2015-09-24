package wang.raye.preioc.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 保存类中的注解与相关的属性，方法绑定的值
 * 
 * @author Raye
 *
 */
public final class BindClass {
	/** 控件与ID绑定的集合 */
	private final LinkedHashMap<Integer, ViewBindById> viewIdMap = new LinkedHashMap<>();
	/** 保存OnClick事件的id与方法名*/
	private final LinkedHashMap<Integer,String> onClicks = new LinkedHashMap<>();
	/** 已经创建过的监听，对应onClicks的value,防止每个OnClickLisenter建立一个监听*/
	private ArrayList<String> onClickListener = new ArrayList<>();
	
	/** 适配器类名*/
	private String adapter;
	
	/** 记录需要绑定数据的属性及其参数key 属性名，value 参数*/
	private final LinkedHashMap<String, DataBinding> dataBinds = new LinkedHashMap<>();
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
	/**
	 * 添加一个OnClick事件的方法
	 * @param id 使用此方法的控件id集合
	 * @param name 
	 */
	protected void addOnClick(int[] ids,String methonName) {
		if(ids != null){
			for(int id : ids){
				onClicks.put(id, methonName);
			}
		}
	}
	
	/**
	 * 添加一行需要绑定数据的记录
	 * @param field 需要被绑定的属性名称
	 * @param dataName 被绑定的数据的名称（通过get+dataName获取数据）
	 */
	protected void addDataBind(String field,String dataName,String adapter){
		dataBinds.put(field, new DataBinding(field, dataName));
		this.adapter = adapter;
	}
	/**
	 * 是否需要绑定数据
	 * @return
	 */
	protected boolean isBindData(){
		return dataBinds.size() > 0;
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
		builder.append("// PreIOC自动生成的代码，请不要修改\n");
		// 设置包名
		builder.append("package ").append(this.classPackage).append(";\n\n");
		// 有控件需要被绑定，导入View包
		if ((!this.viewIdMap.isEmpty())) {
			builder.append("import android.view.View;\n");
			builder.append("import wang.raye.preioc.find.AbstractFind;\n");
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
				.append("public void binder(final AbstractFind finder, final T target, Object source) {\n");
		if (this.parentViewBinder != null) {
			builder.append("    super.binder(finder, target, source);\n\n");
		}
		if ((!this.viewIdMap.isEmpty())) {
			// 需要绑定控件
			for (ViewBindById bindById : viewIdMap.values()) {
				autoViewBinding(builder, bindById);
			}
		}
		//绑定onClickListener
		for(Entry<Integer, String> entry : onClicks.entrySet()){
			bindOnClick(builder,entry.getKey(),null);
		}
		builder.append("  }\n");
	}

	/**
	 * 自动生成控件获取的代码
	 * 
	 * @param builder
	 *            代码StringBuilder
	 * @param bindById
	 *            控件的id与filed绑定的对象
	 */
	private void autoViewBinding(StringBuilder builder, ViewBindById bindById) {
		builder.append("    target.").append(bindById.getField().getName()).append(" = ");
		builder.append("(").append(bindById.getField().getType()).append(")");
		builder.append("finder.findRequiredView(source").append(", ").append(bindById.getId()).append(", \"")
				.append(bindById.getField().getName());
		builder.append("\");\n");
		//绑定onClickListener
		bindOnClick(builder,bindById.getId(),bindById.getField().getName());
	}
	
	/**
	 * 设置为控件设置监听
	 * @param builder 代码StringBuilder
	 * @param id 空间的id
	 * @param viewName 控件的名称（如果为空说明此控件不需要引用）
	 */
	private void bindOnClick(StringBuilder builder,int id,String viewName){
		//获取监听的方法
		if(!onClicks.containsKey(id)){
			//此id不需要设置OnClickListener
			return;
		}
		String methonName = onClicks.get(id);
		if(!onClickListener.contains(methonName)){
			
			//监听不存在，创建监听
			builder.append("	View.OnClickListener ").append(methonName).append(" = new View.OnClickListener() {\n")
			.append("		public void onClick(View view) {\n			target.").append(methonName)
			.append("(view);\n").append("			}\n		};\n");
			//已经建立OnClickListener监听了
			onClickListener.add(methonName);
			View view = null;
		}
		if(viewName == null){
			//不需要被引用的
			builder.append("	((View)finder.findRequiredView(source").append(", ").append(id).append(", \"")
				.append("\")).setOnClickListener(").append(methonName).append(");\n");
		}else{
			builder.append("    target.").append(viewName).append(".setOnClickListener(").append(methonName).append(");\n");
			//避免后面的重新设置
			onClicks.remove(id);
		}
	}

	/**
	 * 获取绑定控件的class名称
	 * @return
	 */
	protected String getViewBinderClassName() {
		return new StringBuilder().append(this.classPackage).append(".").append(this.className).toString();
	}
	
	
	
	
	/////////////////////////////绑定数据的自动生成代码部分
	/**
	 * 获取绑定控件数据的名称
	 * @return
	 */
	protected String getViewDataBinderCN(){
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
