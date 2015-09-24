package wang.raye.preioc.internal.auto;

import java.util.LinkedHashMap;

import wang.raye.preioc.internal.DataBinding;

public class AutoBindData {


	/** ��¼��Ҫ�����ݵ����Լ������key ��������value ����*/
	private final LinkedHashMap<String, DataBinding> dataBinds;
	/** ע�⴦���������ͨ������ʵ����������� ���� */
	private final String className;
	/** ���� */
	private final String classPackage;
	/** ��ǰ��ע������ȫ�� */
	private final String targetClass;
	
	
	public AutoBindData(LinkedHashMap<String, DataBinding> dataBinds, String className, String classPackage,
			String targetClass) {
		super();
		this.dataBinds = dataBinds;
		this.className = className;
		this.classPackage = classPackage;
		this.targetClass = targetClass;
	}

	/**
	 * ��ȡ�󶨿ؼ����ݵ�����
	 * @return
	 */
	public String toJava(){
		return new StringBuilder().append(this.classPackage).append(".").
				append(this.className.substring(0,className.lastIndexOf("$")+1)).
				append("ViewDataBinder").toString();

	}
	
	protected String toDataBinderJava(){
		StringBuilder builder = new StringBuilder();
		builder.append("// PreIOC�Զ����ɵĴ��룬�벻Ҫ�޸�\n");
		// ���ð���
		builder.append("package ").append(this.classPackage).append(";\n\n");
		builder.append("import android.widget.BaseAdapter;\n");
		builder.append("import wang.raye.preioc.ViewDataBinder;\n\n");
		// ��������
		builder.append("public class ").append(className.substring(0,className.lastIndexOf("$$")))
		.append("$$ViewDataBinder")
		// ������ע�⴦����������,ǰ����ViewHolder
		.append("<T extends ").append(this.targetClass).append(",A extends ")
		//������
		.append(targetClass.substring(0, targetClass.lastIndexOf(".")))
		
		.append(">")
		.append(" implements ViewDataBinder<T,A>{\n");
		builder.append("\n");
		
		builder.append('\n');
		
		autoBindDataMethod(builder);
		builder.append("}\n");
		return builder.toString();
	}
	
	/**
	 * �Զ����ɰ����ݵķ���
	 * @param builder
	 */
	private void autoBindDataMethod(StringBuilder builder){
		builder.append("  @Override ")
		.append("	public void bindData(final T t,final A")
		.append(" adapter,int position) {\n");
		
		for(DataBinding binding : dataBinds.values()){
			builder.append("		t."+binding.getFiled()).append(".setText(");
			builder.append("adapter.getItem(position).get")
			.append(toFirstUpperCase(binding.getDataName())).append("());\n");
		}
		
		builder.append("\n}\n");
	}
	
	private String toFirstUpperCase(String str){
//		name = name.substring(0, 1).toUpperCase() + name.substring(1);
//		return  name;
		char[] cs=str.toCharArray();
		if(cs[0] >= 97){
			cs[0]-=32;
		}
		return String.valueOf(cs);
	}
}
