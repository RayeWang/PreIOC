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
	protected DataBinding(String field,String dataName){
		this.field = field;
		this.dataName = dataName;
	}
	public String getFiled() {
		return field;
	}
	public String getDataName() {
		return dataName;
	}
	
	
}
