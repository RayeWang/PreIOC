package com.ray.preioc.internal;

import com.squareup.javapoet.TypeName;

/**
 * �����������������͵Ĺ�ϵ��
 * Ŀǰû��reques����ο���butterknife��һ��
 * @author Ray
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
