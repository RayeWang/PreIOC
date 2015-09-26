package wang.raye.preioc.internal.auto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import wang.raye.preioc.internal.BindResources;
import wang.raye.preioc.internal.ViewBindById;

/**
 * 自动生成绑定控件代码的类
 * @author Raye
 *
 */
public class AutoBindView {
	/** 控件与ID绑定的集合 */
	private final LinkedHashMap<Integer, ViewBindById> viewIdMap = new LinkedHashMap<>();
	/** 保存OnClick事件的id与方法名*/
	private final LinkedHashMap<Integer,String> onClicks = new LinkedHashMap<>();
	/** 保存OnCheckedChangeListener事件的id与方法名*/
	private final LinkedHashMap<Integer, String> onCheckedChanges = new LinkedHashMap<>();
	/** 触摸事件的集合*/
	private final LinkedHashMap<Integer, String> onTouchs = new LinkedHashMap<>();
	/** OnitemClick事件的id与方法名*/
	private final LinkedHashMap<Integer, String> onItemClicks = new LinkedHashMap<>();
	/** 属性与资源绑定的*/
	private final LinkedHashMap<Integer,BindResources> bindResources = new LinkedHashMap<>();
	
	
	/** 已经创建过的监听，对应onClicks的value,防止每个OnClickLisenter建立一个监听*/
	private final ArrayList<String> onClickListener = new ArrayList<>();
	/** 已经建立的OnTouchListener*/
	private final ArrayList<String> onTouchListener = new ArrayList<>();
	/** 已经建立的OnItemClickListener*/
	private final ArrayList<String> onItemClickListener = new ArrayList<>();
	/** 已经创建了的CheckedChangeListener*/
	private final ArrayList<String> onCheckedChangedListeners = new ArrayList<>();
	
	
	
	/** 包名 */
	private final String classPackage;
	/** 注解处理的类名，通过反射实例化这个类来 处理 */
	private final String className;
	/** 当前被注解的类的全称 */
	private final String targetClass;
	/** ViewBinder的接口实现类 */
	private String parentViewBinder;
	



	public AutoBindView(String classPackage, String className, String targetClass) {
		super();
		this.classPackage = classPackage;
		this.className = className;
		this.targetClass = targetClass;
	}

	
	/**
	 * 添加一个View的ID与属性绑定的对象
	 * @param id
	 * @return
	 */
	public ViewBindById getOrCreateViewBindings(int id) {
		ViewBindById viewId = viewIdMap.get(id);
		if (viewId == null) {
			viewId = new ViewBindById(id);
			viewIdMap.put(id, viewId);
		}
		return viewId;
	}

	/**
	 * 添加一个OnClick事件的方法
	 * @param id 使用此方法的控件id集合
	 * @param name 
	 */
	public void addOnClick(int[] ids,String methonName) {
		if(ids != null){
			for(int id : ids){
				onClicks.put(id, methonName);
			}
		}
	}
	
	/**
	 * 添加一个OnCheckedChangeListener事件的方法
	 * @param ids
	 * @param methonName
	 */
	public void addOnCheckedChanged(int[] ids,String methonName){
		if(ids != null){
			for(int id : ids){
				onCheckedChanges.put(id, methonName);
			}
		}
	}
	
	
	/**
	 * 添加一个OnItemClickListener事件的方法
	 * @param ids
	 * @param methonName
	 */
	public void addOnItemClick(int[] ids,String methonName){
		if(ids != null){
			for(int id : ids){
				onItemClicks.put(id, methonName);
			}
		}
	}
	
	/**
	 * 添加一个OnTouch事件的方法
	 * @param ids 使用此方法的控件id集合
	 * @param methonName 执行的方法名
	 */
	public void addOnTouch(int[] ids,String methonName) {
		if(ids != null){
			for(int id : ids){
				onTouchs.put(id, methonName);
			}
		}
	}
	

	////////////////////////////////资源
	public void addBindString(int id,String field){
		bindResources.put(id, new BindResources(field, BindResources.STRING));
	}
	public void addBindDimen(int id,String field){
		bindResources.put(id, new BindResources(field, BindResources.DIMEN));
	}
	public void addBindStringArray(int id,String field){
		bindResources.put(id, new BindResources(field, BindResources.STRINGARRAY));
	}
	/**
	 * 自动生成Java代码
	 * 
	 * @return
	 */
	public String toJava() {
		StringBuilder builder = new StringBuilder();
		builder.append("// PreIOC自动生成的代码，请不要修改\n");
		// 设置包名
		builder.append("package ").append(classPackage).append(";\n\n");
		// 有控件需要被绑定，导入View包
		if ((!viewIdMap.isEmpty())) {
			builder.append("import android.view.View;\n");
			builder.append("import wang.raye.preioc.find.AbstractFind;\n");
		}
		if(onTouchs.size() > 0){
			builder.append("import android.view.MotionEvent;\n");
		}
		if(onCheckedChanges.size() > 0){
			builder.append("import android.widget.CompoundButton;\n");
		}
		if(onItemClicks.size() > 0){
			builder.append("import android.widget.AdapterView;\n");
		}
		
		
		// 需要处理控件绑定，导入接口
		if (parentViewBinder == null) {
			builder.append("import wang.raye.preioc.ViewBinder;\n\n");
		}
		// 创建类名
		builder.append("public class ").append(className);
		// 创建被注解处理的类的类型
		builder.append("<T extends ").append(targetClass).append(">");
		if (parentViewBinder != null)
			builder.append(" extends ").append(parentViewBinder).append("<T>");
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
		if (parentViewBinder != null) {
			builder.append("    super.binder(finder, target, source);\n\n");
		}
		if ((!viewIdMap.isEmpty())) {
			// 需要绑定控件
			for (ViewBindById bindById : viewIdMap.values()) {
				autoViewBinding(builder,bindById);
			}
		}
		//绑定onClickListener
		for(int key : onClicks.keySet()){
			bindOnClick(builder,key,null);
		}
		//绑定OnTouchListener
		for(int key : onTouchs.keySet()){
			bindOnTouch(builder, key, null);
		}
		//绑定OnCheckedChangeListener
		for(int key : onCheckedChanges.keySet()){
			bindOnCheckedChanged(builder, key, null);
		}
		for(int key : onItemClicks.keySet()){
			bindOnItemClick(builder, key, null);
		}
		
		//绑定资源
		bindResource(builder);
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
		bindOnTouch(builder,bindById.getId(),bindById.getField().getName());
		bindOnCheckedChanged(builder,bindById.getId(),bindById.getField().getName());
		bindOnItemClick(builder,bindById.getId(),bindById.getField().getName());
	}
	
	/**
	 * 为控件设置监听
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
	 * 为控件设置OnTouch监听
	 * @param builder 代码StringBuilder
	 * @param id 空间的id
	 * @param viewName 控件的名称（如果为空说明此控件不需要引用）
	 */
	private void bindOnTouch(StringBuilder builder,int id,String viewName){
		//获取监听的方法
		if(!onTouchs.containsKey(id)){
			//此id不需要设置OnTouchListener
			return;
		}
		String methonName = onTouchs.get(id);
		if(!onTouchListener.contains(methonName)){
			
			//监听不存在，创建监听
			builder.append("	View.OnTouchListener ").append(methonName).append(" = new View.OnTouchListener() {\n")
			.append("		public boolean onTouch(View view, MotionEvent event) {\n			return target.")
			.append(methonName)
			.append("(view,event);\n").append("			}\n		};\n");
			//已经建立OnClickListener监听了
			onTouchListener.add(methonName);
		}
		if(viewName == null){
			//不需要被引用的
			builder.append("	((View)finder.findRequiredView(source").append(", ").append(id).append(", \"")
				.append("\")).setOnTouchListener(").append(methonName).append(");\n");
		}else{
			builder.append("    target.").append(viewName).append(".setOnTouchListener(").append(methonName).append(");\n");
			//避免后面的重新设置
			onTouchs.remove(id);
		}
	}
	
	/**
	 * 为控件设置OnCheckedChanged监听
	 * @param builder 代码StringBuilder
	 * @param id 空间的id
	 * @param viewName 控件的名称（如果为空说明此控件不需要引用）
	 */
	private void bindOnCheckedChanged(StringBuilder builder,int id,String viewName){
		//获取监听的方法
		if(!onCheckedChanges.containsKey(id)){
			//此id不需要设置OnTouchListener
			return;
		}
		String methonName = onCheckedChanges.get(id);
		if(!onCheckedChangedListeners.contains(methonName)){
			
			//监听不存在，创建监听
			builder.append("	CompoundButton.OnCheckedChangeListener ").append(methonName).append(" = new CompoundButton.OnCheckedChangeListener() {\n")
			.append("		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {\n			target.")
			.append(methonName)
			.append("(buttonView,isChecked);\n").append("			}\n		};\n");
			//已经建立OnClickListener监听了
			onCheckedChangedListeners.add(methonName);
		}
		if(viewName == null){
			//不需要被引用的
			builder.append("	((CompoundButton)finder.findRequiredView(source").append(", ").append(id).append(", \"")
				.append("\")).setOnCheckedChangeListener(").append(methonName).append(");\n");
		}else{
			builder.append("    target.").append(viewName).append(".setOnCheckedChangeListener(").append(methonName).append(");\n");
			//避免后面的重新设置
			onCheckedChanges.remove(id);
		}
	}
	
	
	/**
	 * 为控件设置OnItemClick监听
	 * @param builder 代码StringBuilder
	 * @param id 空间的id
	 * @param viewName 控件的名称（如果为空说明此控件不需要引用）
	 */
	private void bindOnItemClick(StringBuilder builder,int id,String viewName){
		//获取监听的方法
		if(!onItemClicks.containsKey(id)){
			//此id不需要设置OnTouchListener
			return;
		}
		String methonName = onItemClicks.get(id);
		if(!onItemClickListener.contains(methonName)){
			
			//监听不存在，创建监听
			builder.append("	AdapterView.OnItemClickListener ").append(methonName).append(" = new AdapterView.OnItemClickListener() {\n")
			.append("		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {\n			target.")
			.append(methonName)
			.append("(parent,view,position,id);\n").append("			}\n		};\n");
			//已经建立OnClickListener监听了
			onItemClickListener.add(methonName);
		}
		if(viewName == null){
			//不需要被引用的
			builder.append("	((AdapterView)finder.findRequiredView(source").append(", ").append(id).append(", \"")
				.append("\")).setOnItemClickListener(").append(methonName).append(");\n");
		}else{
			builder.append("    target.").append(viewName).append(".setOnItemClickListener(").append(methonName).append(");\n");
			//避免后面的重新设置
			onItemClicks.remove(id);
		}
	}
	
	
	private void bindResource(StringBuilder builder){
		for(Entry<Integer, BindResources> entry : bindResources.entrySet()){
			int id = entry.getKey();
			BindResources value = entry.getValue();
			switch (value.getType()) {
				case BindResources.STRING:

					builder.append("		target.").append(value.getField())
					.append(" = finder.getString(source,").append(id).append(");\n");
					break;
				case BindResources.DIMEN:
					builder.append("		target.").append(value.getField())
					.append(" = finder.getDimen(source,").append(id).append(");\n");
					break;
				case BindResources.STRINGARRAY:
					builder.append("		target.").append(value.getField())
					.append(" = finder.getStringArray(source,").append(id).append(");\n");
					break;
				default:
					break;
			}
		}
	}
}
