package wang.raye.preioc.internal;
/**
 * 属性与数据的绑定的相关参数存储实体
 * @author Raye
 *
 */
public class DataBinding {

	/** 属性名称*/
	private final String field;
	/** 绑定的数据名称*/
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
