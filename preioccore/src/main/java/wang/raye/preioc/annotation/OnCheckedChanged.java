package wang.raye.preioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CompoundButton的OnCheckedChangeListener
 * 方法签名void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
 * @author Raye
 *
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface OnCheckedChanged {

	int[] value();
}
