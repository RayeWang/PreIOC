package wang.raye.preioc.internal;

import java.util.LinkedHashMap;

import wang.raye.preioc.internal.auto.AutoBindData;
import wang.raye.preioc.internal.auto.AutoBindView;

/**
 * 保存类中的注解与相关的属性，方法绑定的值
 * 
 * @author Raye
 *
 */
public final class BindClass {
	/** 控件与ID绑定的集合 */
	private final LinkedHashMap<Integer, ViewBindById> viewIdMap = new LinkedHashMap<>();
	
	
	/** 自动生成代码的对象*/
	private final AutoBindView autoBindView;
	
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
		this.autoBindView = new AutoBindView(classPackage, className, targetClass);
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
		autoBindView.addOnClick(ids, methonName);
	}
	
	/**
	 * 添加一个OnTouch事件的方法
	 * @param ids 使用此方法的控件id集合
	 * @param methonName 执行的方法名
	 */
	protected void addOnTouch(int[] ids,String methonName) {
		autoBindView.addOnTouch(ids, methonName);
	}
	
	/**
	 * 添加一行需要绑定数据的记录
	 * @param field 需要被绑定的属性名称
	 * @param dataName 被绑定的数据的名称（通过get+dataName获取数据）
	 */
	protected void addDataBind(String field,String dataName,String adapter,String format){
		dataBinds.put(field, new DataBinding(field, dataName,format));
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
		return autoBindView.getOrCreateViewBindings(id);
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
		return autoBindView.toJava();
				
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
		return new AutoBindData(dataBinds, className, classPackage, targetClass).toJava();
	}
	
}
