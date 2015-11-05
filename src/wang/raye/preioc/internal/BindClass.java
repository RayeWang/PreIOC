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
	/** 自动生成绑定数据的代码的对象*/
	private final AutoBindData autoBindData;
	
	
	/** 记录需要绑定数据的属性及其参数key 属性名，value 参数*/
	private final LinkedHashMap<String, DataBinding> dataBinds = new LinkedHashMap<>();
	/** 包名 */
	private final String classPackage;
	/** 注解处理的类名，通过反射实例化这个类来 处理 */
	private final String className;
	/** ViewBinder的接口实现类 */
	private String parentViewBinder;

	protected BindClass(String classPackage, String className, String targetClass) {
		this.classPackage = classPackage;
		this.className = className;
//		this.targetClass = targetClass;
		this.autoBindView = new AutoBindView(classPackage, className, targetClass);
		this.autoBindData = new AutoBindData(className, classPackage, targetClass);
	}

	public AutoBindView getAutoBindView() {
		return autoBindView;
	}

	protected void addField(int id, FieldViewBindTypeAndName binding) {
		getOrCreateViewBindings(id).setField(binding);

	}


	
	/**
	 * 添加一行需要绑定数据的记录
	 * @param field 需要被绑定的属性名称
	 * @param dataName 被绑定的数据的名称（通过get+dataName获取数据）
	 */
	protected void addDataBind(String field,String dataName,String format){
		autoBindData.addDataBind(field, dataName, format);
	}
	/**
	 * 是否需要绑定数据
	 * @return
	 */
	protected boolean isBindData(){
		return autoBindData.isBindData();
	}
	
	
	private ViewBindById getOrCreateViewBindings(int id) {
		return autoBindView.getOrCreateViewBindings(id);
	}

	protected void setParentViewBinder(String parentViewBinder) {
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
	protected String getViewBinderCN() {
		return new StringBuilder().append(this.classPackage).append(".").append(this.className).toString();
	}
	
	
	
	
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
		return autoBindData.toJava();
	}
	
}
