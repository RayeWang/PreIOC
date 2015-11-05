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
	/** 通过方法获取数据，此方法应在适配器中*/
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
