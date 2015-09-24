package wang.raye.preioc.internal;


/**
 * 保存属性名字与类型的关系类
 * 目前没有reques，与参考的butterknife不一样
 * @author Raye
 *
 */
public class FieldViewBindTypeAndName {
	/**
	 * 属性名字
	 */
	private final String name;
	/**
	 * 类型
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
