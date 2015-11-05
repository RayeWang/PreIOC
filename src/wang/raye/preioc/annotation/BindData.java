package wang.raye.preioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 绑定数据的注解
 * 目前只支持控件的setText
 * 此注解只支持适配器（需继承至BaseAdapter）
 * @author Raye
 *
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface BindData {
	/** 绑定的数据属性名*/
	String value() default "";
	/** 绑定数据的格式化方法 格式：  String  methonName(int position)*/
	String format() default "";
}
