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
	/** ����*/
	private final String classPackage;
	/** ע�⴦���������ͨ������ʵ����������� ����*/
	private final String className;
	/** ��ǰ��ע������ȫ��*/
	private final String targetClass;

	protected BindClass(String classPackage, String className, String targetClass) {
		this.classPackage = classPackage;
		this.className = className;
		this.targetClass = targetClass;
	}

	protected void addField(int id, FieldViewBindTypeAndName binding) {
		getOrCreateViewBindings(id).setField(binding);
		;
	}

	private ViewBindById getOrCreateViewBindings(int id) {
		ViewBindById viewId = viewIdMap.get(id);
		if (viewId == null) {
			viewId = new ViewBindById(id);
			viewIdMap.put(id, viewId);
		}
		return viewId;
	}

	protected ViewBindById getViewBinding(int id) {
		return viewIdMap.get(id);
	}
}
