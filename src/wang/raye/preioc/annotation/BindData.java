package wang.raye.preioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * �����ݵ�ע��
 * Ŀǰֻ֧�ֿؼ���setText
 * ��ע��ֻ֧������������̳���BaseAdapter��
 * @author Raye
 *
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface BindData {
	/** �󶨵�����������*/
	String value() default "";
	/** �����ݵĸ�ʽ������ ��ʽ��  String  methonName(int position)*/
	String format() default "";
}
