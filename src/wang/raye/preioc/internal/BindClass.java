package wang.raye.preioc.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
	protected void addDataBind(String field,String dataName,String adapter){
		dataBinds.put(field, new DataBinding(field, dataName));
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
		StringBuilder builder = new StringBuilder();
		builder.append("// PreIOC�Զ����ɵĴ��룬�벻Ҫ�޸�\n");
		// ���ð���
		builder.append("package ").append(this.classPackage).append(";\n\n");
		// �пؼ���Ҫ���󶨣�����View��
		if ((!this.viewIdMap.isEmpty())) {
			builder.append("import android.view.View;\n");
			builder.append("import wang.raye.preioc.find.AbstractFind;\n");
		}
		// ��Ҫ����ؼ��󶨣�����ӿ�
		if (this.parentViewBinder == null) {
			builder.append("import wang.raye.preioc.ViewBinder;\n\n");
		}
		// ��������
		builder.append("public class ").append(this.className);
		// ������ע�⴦����������
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
	 * �Զ����ɰ󶨴���
	 */
	private void autoBindMethod(StringBuilder builder) {
		builder.append("  @Override ")
				.append("public void binder(final AbstractFind finder, final T target, Object source) {\n");
		if (this.parentViewBinder != null) {
			builder.append("    super.binder(finder, target, source);\n\n");
		}
		if ((!this.viewIdMap.isEmpty())) {
			// ��Ҫ�󶨿ؼ�
			for (ViewBindById bindById : viewIdMap.values()) {
				autoViewBinding(builder, bindById);
			}
		}
		//��onClickListener
		for(Entry<Integer, String> entry : onClicks.entrySet()){
			bindOnClick(builder,entry.getKey(),null);
		}
		builder.append("  }\n");
	}

	/**
	 * �Զ����ɿؼ���ȡ�Ĵ���
	 * 
	 * @param builder
	 *            ����StringBuilder
	 * @param bindById
	 *            �ؼ���id��filed�󶨵Ķ���
	 */
	private void autoViewBinding(StringBuilder builder, ViewBindById bindById) {
		builder.append("    target.").append(bindById.getField().getName()).append(" = ");
		builder.append("(").append(bindById.getField().getType()).append(")");
		builder.append("finder.findRequiredView(source").append(", ").append(bindById.getId()).append(", \"")
				.append(bindById.getField().getName());
		builder.append("\");\n");
		//��onClickListener
		bindOnClick(builder,bindById.getId(),bindById.getField().getName());
	}
	
	/**
	 * ����Ϊ�ؼ����ü���
	 * @param builder ����StringBuilder
	 * @param id �ռ��id
	 * @param viewName �ؼ������ƣ����Ϊ��˵���˿ؼ�����Ҫ���ã�
	 */
	private void bindOnClick(StringBuilder builder,int id,String viewName){
		//��ȡ�����ķ���
		if(!onClicks.containsKey(id)){
			//��id����Ҫ����OnClickListener
			return;
		}
		String methonName = onClicks.get(id);
		if(!onClickListener.contains(methonName)){
			
			//���������ڣ���������
			builder.append("	View.OnClickListener ").append(methonName).append(" = new View.OnClickListener() {\n")
			.append("		public void onClick(View view) {\n			target.").append(methonName)
			.append("(view);\n").append("			}\n		};\n");
			//�Ѿ�����OnClickListener������
			onClickListener.add(methonName);
			View view = null;
		}
		if(viewName == null){
			//����Ҫ�����õ�
			builder.append("	((View)finder.findRequiredView(source").append(", ").append(id).append(", \"")
				.append("\")).setOnClickListener(").append(methonName).append(");\n");
		}else{
			builder.append("    target.").append(viewName).append(".setOnClickListener(").append(methonName).append(");\n");
			//����������������
			onClicks.remove(id);
		}
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
		StringBuilder builder = new StringBuilder();
		builder.append("// PreIOC�Զ����ɵĴ��룬�벻Ҫ�޸�\n");
		// ���ð���
		builder.append("package ").append(this.classPackage).append(";\n\n");
		builder.append("import android.widget.BaseAdapter;\n");
		builder.append("import wang.raye.preioc.ViewDataBinder;\n\n");
		// ��������
		builder.append("public class ").append(className.substring(0,className.lastIndexOf("$$")))
		.append("$$ViewDataBinder")
		// ������ע�⴦����������,ǰ����ViewHolder
		.append("<T extends ").append(this.targetClass).append(",A extends ")
		//������
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
	 * �Զ����ɰ����ݵķ���
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
