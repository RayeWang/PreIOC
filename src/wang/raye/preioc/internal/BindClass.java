package wang.raye.preioc.internal;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * �������е�ע������ص����ԣ������󶨵�ֵ
 * 
 * @author Raye
 *
 */
public final class BindClass {
	/** �ؼ���ID�󶨵ļ��� */
	private final Map<Integer, ViewBindById> viewIdMap = new LinkedHashMap<>();
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
		builder.append("// ProIOC�Զ����ɵĴ��룬�벻Ҫ�޸�\n");
		// ���ð���
		builder.append("package ").append(this.classPackage).append(";\n\n");
		// �пؼ���Ҫ���󶨣�����View��
		if ((!this.viewIdMap.isEmpty())) {
			builder.append("import android.view.View;\n");
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
				.append("public void binder(final Finder finder, final T target, Object source) {\n");
		if (this.parentViewBinder != null) {
			builder.append("    super.binder(finder, target, source);\n\n");
		}
		if((!this.viewIdMap.isEmpty())){
			//��Ҫ�󶨿ؼ�
			for(ViewBindById bindById : viewIdMap.values()){
				autoViewBinding(builder, bindById);
			}
		}
	    builder.append("  }\n");
	}
	
	/**
	 * �Զ����ɿؼ���ȡ�Ĵ���
	 * @param builder ����StringBuilder
	 * @param bindById �ؼ���id��filed�󶨵Ķ���
	 */
	private void autoViewBinding(StringBuilder builder,ViewBindById bindById){
		builder.append("    target.")
        .append(bindById.getField().getName())
        .append(" = ");
		builder.append("(").append(bindById.getField().getType()).append(")");
		builder.append("finder.findRequiredView(source")
        .append(", ")
        .append(bindById.getId()).append(", \"")
        .append(bindById.getField().getName());
		
        builder.append("\");\n");
	}
}
