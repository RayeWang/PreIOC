package wang.raye.preioc.internal;
/**
 * ����Դ�󶨵���Ŀ������
 * field �󶨵���������
 * type ��Դ������
 * @author Raye
 *
 */
public class BindResources {

	public final static int STRING = 1;
	public final static int DIMEN = 2;
	public final static int STRINGARRAY = 3;
	public final static int INTARRAY = 4;
//	public final static int 
	private final String field;
	private final int type;
	
	public BindResources(String field, int type) {
		super();
		this.field = field;
		this.type = type;
	}

	public String getField() {
		return field;
	}

	public int getType() {
		return type;
	}
	
	
}
