package wang.raye.preioc;

import wang.raye.preioc.find.AbstractFind;

/**
 * 绑定控件的接口，用于自动生成的代码中具体实现
 * @author Raye
 *
 */
public interface ViewBinder<T> {

	public void binder(final AbstractFind finder, final T target, Object source);
}
