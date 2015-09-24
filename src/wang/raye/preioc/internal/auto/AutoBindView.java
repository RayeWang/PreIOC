package wang.raye.preioc.internal.auto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import android.view.View;
import wang.raye.preioc.internal.DataBinding;
import wang.raye.preioc.internal.ViewBindById;

/**
 * �Զ����ɰ󶨿ؼ��������
 * @author Raye
 *
 */
public class AutoBindView {
	/** �ؼ���ID�󶨵ļ��� */
	private final LinkedHashMap<Integer, ViewBindById> viewIdMap ;
	/** ����OnClick�¼���id�뷽����*/
	private final LinkedHashMap<Integer,String> onClicks ;
	/** �Ѿ��������ļ�������ӦonClicks��value,��ֹÿ��OnClickLisenter����һ������*/
	private ArrayList<String> onClickListener = new ArrayList<>();

	/** ���� */
	private final String classPackage;
	/** ע�⴦���������ͨ������ʵ����������� ���� */
	private final String className;
	/** ��ǰ��ע������ȫ�� */
	private final String targetClass;
	/** ViewBinder�Ľӿ�ʵ���� */
	private String parentViewBinder;
	

	public AutoBindView(LinkedHashMap<Integer, ViewBindById> viewIdMap, LinkedHashMap<Integer, String> onClicks,
			ArrayList<String> onClickListener, String classPackage, String className, String targetClass,
			String parentViewBinder) {
		super();
		this.viewIdMap = viewIdMap;
		this.onClicks = onClicks;
		this.onClickListener = onClickListener;
		this.classPackage = classPackage;
		this.className = className;
		this.targetClass = targetClass;
		this.parentViewBinder = parentViewBinder;
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
}
