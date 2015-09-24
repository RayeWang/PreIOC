package wang.raye.preioc.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;
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
	/** ����OnClick�¼���id�뷽����*/
	private final LinkedHashMap<Integer,String> onClicks = new LinkedHashMap<>();
	/** �Ѿ��������ļ�������ӦonClicks��value,��ֹÿ��OnClickLisenter����һ������*/
	private ArrayList<String> onClickListener = new ArrayList<>();
	
	/** ����������*/
	private String adapter;
	
	/** ��¼��Ҫ�����ݵ����Լ������key ��������value ����*/
	private final LinkedHashMap<String, DataBinding> dataBinds = new LinkedHashMap<>();
	/** ���� */
	private final String classPackage;
	/** ע�⴦���������ͨ������ʵ����������� ���� */
	private final String className;
	/** ��ǰ��ע������ȫ�� */
	private final String targetClass;
	/** ViewBinder�Ľӿ�ʵ���� */
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
	 * ���һ��OnClick�¼��ķ���
	 * @param id ʹ�ô˷����Ŀؼ�id����
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
	 * ���һ����Ҫ�����ݵļ�¼
	 * @param field ��Ҫ���󶨵���������
	 * @param dataName ���󶨵����ݵ����ƣ�ͨ��get+dataName��ȡ���ݣ�
	 */
	protected void addDataBind(String field,String dataName,String adapter,String format){
		dataBinds.put(field, new DataBinding(field, dataName,format));
		this.adapter = adapter;
	}
	/**
	 * �Ƿ���Ҫ������
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
	 * �Զ�����Java����
	 * 
	 * @return
	 */
	protected String toJava() {
		return new AutoBindView(viewIdMap, onClicks, onClickListener, classPackage,
				className, targetClass, parentViewBinder).toJava();
				
	}



	/**
	 * ��ȡ�󶨿ؼ���class����
	 * @return
	 */
	protected String getViewBinderClassName() {
		return new StringBuilder().append(this.classPackage).append(".").append(this.className).toString();
	}
	
	
	
	
	/////////////////////////////�����ݵ��Զ����ɴ��벿��
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
		return new AutoBindData(dataBinds, className, classPackage, targetClass).toJava();
	}
	
}
