package wang.raye.preioc.internal;


/**
 * �����������������͵Ĺ�ϵ��
 * Ŀǰû��reques����ο���butterknife��һ��
 * @author Raye
 *
 */
public class FieldViewBindTypeAndName {
	/**
	 * ��������
	 */
	private final String name;
	/**
	 * ����
	 */
	private final String type;

	protected FieldViewBindTypeAndName(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return "field '" + name + "'";
	}

	public boolean requiresCast() {
		return !PreIOCProcessor.VIEW_TYPE.equals(type.toString());
	}
}
