package com.ray.preioc.internal;

import com.squareup.javapoet.TypeName;

/**
 * 保存属性名字与类型的关系类
 * 目前没有reques，与参考的butterknife不一样
 * @author Ray
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
	private final TypeName type;

	protected FieldViewBindTypeAndName(String name, TypeName type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public TypeName getType() {
		return type;
	}

	public String getDescription() {
		return "field '" + name + "'";
	}

	public boolean requiresCast() {
		return !ProIOCProcessor.VIEW_TYPE.equals(type.toString());
	}
}
