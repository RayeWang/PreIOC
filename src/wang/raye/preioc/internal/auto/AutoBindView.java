package wang.raye.preioc.internal.auto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import wang.raye.preioc.internal.BindResources;
import wang.raye.preioc.internal.ViewBindById;

/**
 * �Զ����ɰ󶨿ؼ��������
 * @author Raye
 *
 */
public class AutoBindView {
	/** �ؼ���ID�󶨵ļ��� */
	private final LinkedHashMap<Integer, ViewBindById> viewIdMap = new LinkedHashMap<>();
	/** ����OnClick�¼���id�뷽����*/
	private final LinkedHashMap<Integer,String> onClicks = new LinkedHashMap<>();
	/** ����OnCheckedChangeListener�¼���id�뷽����*/
	private final LinkedHashMap<Integer, String> onCheckedChanges = new LinkedHashMap<>();
	/** �����¼��ļ���*/
	private final LinkedHashMap<Integer, String> onTouchs = new LinkedHashMap<>();
	/** OnitemClick�¼���id�뷽����*/
	private final LinkedHashMap<Integer, String> onItemClicks = new LinkedHashMap<>();
	/** ��������Դ�󶨵�*/
	private final LinkedHashMap<Integer,BindResources> bindResources = new LinkedHashMap<>();
	
	
	/** �Ѿ��������ļ�������ӦonClicks��value,��ֹÿ��OnClickLisenter����һ������*/
	private final ArrayList<String> onClickListener = new ArrayList<>();
	/** �Ѿ�������OnTouchListener*/
	private final ArrayList<String> onTouchListener = new ArrayList<>();
	/** �Ѿ�������OnItemClickListener*/
	private final ArrayList<String> onItemClickListener = new ArrayList<>();
	/** �Ѿ������˵�CheckedChangeListener*/
	private final ArrayList<String> onCheckedChangedListeners = new ArrayList<>();
	
	
	
	/** ���� */
	private final String classPackage;
	/** ע�⴦���������ͨ������ʵ����������� ���� */
	private final String className;
	/** ��ǰ��ע������ȫ�� */
	private final String targetClass;
	/** ViewBinder�Ľӿ�ʵ���� */
	private String parentViewBinder;
	



	public AutoBindView(String classPackage, String className, String targetClass) {
		super();
		this.classPackage = classPackage;
		this.className = className;
		this.targetClass = targetClass;
	}

	
	/**
	 * ���һ��View��ID�����԰󶨵Ķ���
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
	 * ���һ��OnClick�¼��ķ���
	 * @param id ʹ�ô˷����Ŀؼ�id����
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
	 * ���һ��OnCheckedChangeListener�¼��ķ���
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
	 * ���һ��OnItemClickListener�¼��ķ���
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
	 * ���һ��OnTouch�¼��ķ���
	 * @param ids ʹ�ô˷����Ŀؼ�id����
	 * @param methonName ִ�еķ�����
	 */
	public void addOnTouch(int[] ids,String methonName) {
		if(ids != null){
			for(int id : ids){
				onTouchs.put(id, methonName);
			}
		}
	}
	

	////////////////////////////////��Դ
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
	 * �Զ�����Java����
	 * 
	 * @return
	 */
	public String toJava() {
		StringBuilder builder = new StringBuilder();
		builder.append("// PreIOC�Զ����ɵĴ��룬�벻Ҫ�޸�\n");
		// ���ð���
		builder.append("package ").append(classPackage).append(";\n\n");
		// �пؼ���Ҫ���󶨣�����View��
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
		
		
		// ��Ҫ����ؼ��󶨣�����ӿ�
		if (parentViewBinder == null) {
			builder.append("import wang.raye.preioc.ViewBinder;\n\n");
		}
		// ��������
		builder.append("public class ").append(className);
		// ������ע�⴦����������
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
	 * �Զ����ɰ󶨴���
	 */
	private void autoBindMethod(StringBuilder builder) {
		builder.append("  @Override ")
				.append("public void binder(final AbstractFind finder, final T target, Object source) {\n");
		if (parentViewBinder != null) {
			builder.append("    super.binder(finder, target, source);\n\n");
		}
		if ((!viewIdMap.isEmpty())) {
			// ��Ҫ�󶨿ؼ�
			for (ViewBindById bindById : viewIdMap.values()) {
				autoViewBinding(builder,bindById);
			}
		}
		//��onClickListener
		for(int key : onClicks.keySet()){
			bindOnClick(builder,key,null);
		}
		//��OnTouchListener
		for(int key : onTouchs.keySet()){
			bindOnTouch(builder, key, null);
		}
		//��OnCheckedChangeListener
		for(int key : onCheckedChanges.keySet()){
			bindOnCheckedChanged(builder, key, null);
		}
		for(int key : onItemClicks.keySet()){
			bindOnItemClick(builder, key, null);
		}
		
		//����Դ
		bindResource(builder);
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
		bindOnTouch(builder,bindById.getId(),bindById.getField().getName());
		bindOnCheckedChanged(builder,bindById.getId(),bindById.getField().getName());
		bindOnItemClick(builder,bindById.getId(),bindById.getField().getName());
	}
	
	/**
	 * Ϊ�ؼ����ü���
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
	 * Ϊ�ؼ�����OnTouch����
	 * @param builder ����StringBuilder
	 * @param id �ռ��id
	 * @param viewName �ؼ������ƣ����Ϊ��˵���˿ؼ�����Ҫ���ã�
	 */
	private void bindOnTouch(StringBuilder builder,int id,String viewName){
		//��ȡ�����ķ���
		if(!onTouchs.containsKey(id)){
			//��id����Ҫ����OnTouchListener
			return;
		}
		String methonName = onTouchs.get(id);
		if(!onTouchListener.contains(methonName)){
			
			//���������ڣ���������
			builder.append("	View.OnTouchListener ").append(methonName).append(" = new View.OnTouchListener() {\n")
			.append("		public boolean onTouch(View view, MotionEvent event) {\n			return target.")
			.append(methonName)
			.append("(view,event);\n").append("			}\n		};\n");
			//�Ѿ�����OnClickListener������
			onTouchListener.add(methonName);
		}
		if(viewName == null){
			//����Ҫ�����õ�
			builder.append("	((View)finder.findRequiredView(source").append(", ").append(id).append(", \"")
				.append("\")).setOnTouchListener(").append(methonName).append(");\n");
		}else{
			builder.append("    target.").append(viewName).append(".setOnTouchListener(").append(methonName).append(");\n");
			//����������������
			onTouchs.remove(id);
		}
	}
	
	/**
	 * Ϊ�ؼ�����OnCheckedChanged����
	 * @param builder ����StringBuilder
	 * @param id �ռ��id
	 * @param viewName �ؼ������ƣ����Ϊ��˵���˿ؼ�����Ҫ���ã�
	 */
	private void bindOnCheckedChanged(StringBuilder builder,int id,String viewName){
		//��ȡ�����ķ���
		if(!onCheckedChanges.containsKey(id)){
			//��id����Ҫ����OnTouchListener
			return;
		}
		String methonName = onCheckedChanges.get(id);
		if(!onCheckedChangedListeners.contains(methonName)){
			
			//���������ڣ���������
			builder.append("	CompoundButton.OnCheckedChangeListener ").append(methonName).append(" = new CompoundButton.OnCheckedChangeListener() {\n")
			.append("		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {\n			target.")
			.append(methonName)
			.append("(buttonView,isChecked);\n").append("			}\n		};\n");
			//�Ѿ�����OnClickListener������
			onCheckedChangedListeners.add(methonName);
		}
		if(viewName == null){
			//����Ҫ�����õ�
			builder.append("	((CompoundButton)finder.findRequiredView(source").append(", ").append(id).append(", \"")
				.append("\")).setOnCheckedChangeListener(").append(methonName).append(");\n");
		}else{
			builder.append("    target.").append(viewName).append(".setOnCheckedChangeListener(").append(methonName).append(");\n");
			//����������������
			onCheckedChanges.remove(id);
		}
	}
	
	
	/**
	 * Ϊ�ؼ�����OnItemClick����
	 * @param builder ����StringBuilder
	 * @param id �ռ��id
	 * @param viewName �ؼ������ƣ����Ϊ��˵���˿ؼ�����Ҫ���ã�
	 */
	private void bindOnItemClick(StringBuilder builder,int id,String viewName){
		//��ȡ�����ķ���
		if(!onItemClicks.containsKey(id)){
			//��id����Ҫ����OnTouchListener
			return;
		}
		String methonName = onItemClicks.get(id);
		if(!onItemClickListener.contains(methonName)){
			
			//���������ڣ���������
			builder.append("	AdapterView.OnItemClickListener ").append(methonName).append(" = new AdapterView.OnItemClickListener() {\n")
			.append("		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {\n			target.")
			.append(methonName)
			.append("(parent,view,position,id);\n").append("			}\n		};\n");
			//�Ѿ�����OnClickListener������
			onItemClickListener.add(methonName);
		}
		if(viewName == null){
			//����Ҫ�����õ�
			builder.append("	((AdapterView)finder.findRequiredView(source").append(", ").append(id).append(", \"")
				.append("\")).setOnItemClickListener(").append(methonName).append(");\n");
		}else{
			builder.append("    target.").append(viewName).append(".setOnItemClickListener(").append(methonName).append(");\n");
			//����������������
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
