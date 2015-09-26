package wang.raye.preioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * °ó¶¨Êý×é
 * @author Raye
 *
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface BindStringArray {

	int value();
}
