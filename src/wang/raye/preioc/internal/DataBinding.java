package wang.raye.preioc.internal;
/**
 * ���������ݵİ󶨵���ز����洢ʵ��
 * @author Raye
 *
 */
public class DataBinding {

	/** ��������*/
	private final String field;
	/** �󶨵���������*/
	private final String dataName;
	/** ͨ��������ȡ���ݣ��˷���Ӧ����������*/
	private final String format;
	public DataBinding(String field,String dataName,String format){
		this.field = field;
		this.dataName = dataName;
		this.format = format;
	}
	
	public String getField() {
		return field;
	}

	public String getDataName() {
		return dataName;
	}
	public String getFormat() {
		return format;
	}
	
	
}
