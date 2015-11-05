package wang.raye.preioc.internal;

import java.util.LinkedHashMap;

import wang.raye.preioc.internal.auto.AutoBindData;
import wang.raye.preioc.internal.auto.AutoBindView;

/**
 * �������е�ע������ص����ԣ������󶨵�ֵ
 * 
 * @author Raye
 *
 */
public final class BindClass {
	/** �ؼ���ID�󶨵ļ��� */
	private final LinkedHashMap<Integer, ViewBindById> viewIdMap = new LinkedHashMap<>();
	
	
	/** �Զ����ɴ���Ķ���*/
	private final AutoBindView autoBindView;
	/** �Զ����ɰ����ݵĴ���Ķ���*/
	private final AutoBindData autoBindData;
	
	
	/** ��¼��Ҫ�����ݵ����Լ������key ��������value ����*/
	private final LinkedHashMap<String, DataBinding> dataBinds = new LinkedHashMap<>();
	/** ���� */
	private final String classPackage;
	/** ע�⴦���������ͨ������ʵ����������� ���� */
	private final String className;
	/** ViewBinder�Ľӿ�ʵ���� */
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
	 * ���һ����Ҫ�����ݵļ�¼
	 * @param field ��Ҫ���󶨵���������
	 * @param dataName ���󶨵����ݵ����ƣ�ͨ��get+dataName��ȡ���ݣ�
	 */
	protected void addDataBind(String field,String dataName,String format){
		autoBindData.addDataBind(field, dataName, format);
	}
	/**
	 * �Ƿ���Ҫ������
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
	 * �Զ�����Java����
	 * 
	 * @return
	 */
	protected String toJava() {
		return autoBindView.toJava();
				
	}



	/**
	 * ��ȡ�󶨿ؼ���class����
	 * @return
	 */
	protected String getViewBinderCN() {
		return new StringBuilder().append(this.classPackage).append(".").append(this.className).toString();
	}
	
	
	
	
	/**
	 * ��ȡ�󶨿ؼ����ݵ�����
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
