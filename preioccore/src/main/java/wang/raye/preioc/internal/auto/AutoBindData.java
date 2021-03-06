package wang.raye.preioc.internal.auto;

import java.util.LinkedHashMap;

import wang.raye.preioc.internal.DataBinding;
import wang.raye.preioc.internal.PreIOCProcessor;

/**
 * 自动生成绑定数据到控件代码的类
 * @author Raye
 *
 */
public class AutoBindData {


	/** 记录需要绑定数据的属性及其参数key 属性名，value 参数*/
	private final LinkedHashMap<String, DataBinding> dataBinds = new LinkedHashMap<>();
	/** 注解处理的类名，通过反射实例化这个类来 处理 */
	private final String className;
	/** 包名 */
	private final String classPackage;
	/** 当前被注解的类的全称 */
	private final String targetClass;

	private String holderName;
	private int layoutId;


	public AutoBindData(String className, String classPackage,
						String targetClass) {
		super();
		this.className = className;
		this.classPackage = classPackage;
		this.targetClass = targetClass;
	}

    public void setHolderName(String name){
        this.holderName = name;
    }

    public void setLayoutId(int id){
        this.layoutId = id;
    }

	/**
	 * 是否需要绑定数据
	 * @return
	 */
	public boolean isBindData(){
		return dataBinds.size() > 0;
	}

	/**
	 * 添加一个数据绑定键值对
	 * @param field 被绑定的属性
	 * @param dataName 数据名称
	 * @param format format方法名
	 */
	public void addDataBind(String field,String dataName,String format){
		dataBinds.put(field, new DataBinding(field, dataName,format));
	}

	/**
	 * 自动生成Java绑定代码
	 * @return
	 */
	public String toJava(){
//		return new StringBuilder().append(this.classPackage).append(".").
//				append(this.className.substring(0,className.lastIndexOf("$")+1)).
//				append("ViewDataBinder").toString();
		StringBuilder builder = new StringBuilder();
		builder.append("// PreIOC自动生成的代码，请不要修改\n");
		// 设置包名
		builder.append("package ").append(this.classPackage).append(";\n\n");
		builder.append("import android.widget.BaseAdapter;\n");
        builder.append("import wang.raye.preioc.PreIOC;\n");
        builder.append("import android.view.View;\n");
        builder.append("import wang.raye.preioc.PreAdapter;\n");
		builder.append("import wang.raye.preioc.ViewDataBinder;\n\n");
		// 创建类名
		builder.append("public class ").append(className.substring(0, className.lastIndexOf("$$")))
				.append("$$ViewDataBinder")
                        // 创建被注解处理的类的类型,前面是ViewHolder
				.append("<T extends ")
                        //适配器
                .append(targetClass.substring(0, targetClass.lastIndexOf(".")))

                .append(">")
                .append(" extends ViewDataBinder<T>{\n");
        builder.append("\n");


		autoBindDataMethod(builder);
		builder.append("}\n");
		return builder.toString();
	}



	/**
	 * 自动生成绑定数据的方法
	 * @param builder
	 */
	private void autoBindDataMethod(StringBuilder builder) {
        builder.append("    @Override \n")
                .append("	public View bindData(View convertView,int position) {\n");
        builder.append("        ").append(this.targetClass).append(" view = null;\n");
        builder.append("        if(convertView == null){\n");
        builder.append("            convertView = inflater.inflate(").append(layoutId)
                .append(", null);\n");
        builder.append("            view = new ").append(holderName).append("();\n");
        builder.append("            PreIOC.binder(view,convertView);\n");
        builder.append("            convertView.setTag(view);\n");
        builder.append("        }else{\n");
        builder.append("            view = (").append(holderName).append(")")
                .append("convertView.getTag();\n").append("         }\n");
		for(DataBinding binding : dataBinds.values()){
			if(!"".equals(binding.getFormat())){
				builder.append("		view.").append(binding.getField()).append(".setText(");
				builder.append("adapter.").append(binding.getFormat()).append("(position));\n");
			}else if( !"".equals(binding.getDataName())){
				builder.append("		view.").append(binding.getField()).append(".setText(");
				builder.append("adapter.getItem(position).get")
						.append(toFirstUpperCase(binding.getDataName())).append("());\n");
			}

		}
        builder.append("        return convertView;");
		builder.append("\n      }\n");
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
