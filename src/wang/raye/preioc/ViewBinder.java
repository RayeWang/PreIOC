package wang.raye.preioc;

import wang.raye.preioc.find.AbstractFind;

/**
 * �󶨿ؼ��Ľӿڣ������Զ����ɵĴ����о���ʵ��
 * @author Raye
 *
 */
public interface ViewBinder<T> {

	public void binder(final AbstractFind finder, final T target, Object source);
}
