package wang.raye.preioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * AdapterView��OnItemClickListener
 * ����ǩ��void onItemClick(AdapterView<?> parent, View view, int position, long id)
 * @author Raye
 *
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface OnItemClick {
	int[] value();
}
