package wang.raye.preioc;

import wang.raye.preioc.ProIOC.Finder;

/**
 * �󶨿ؼ��Ľӿڣ������Զ����ɵĴ����о���ʵ��
 * @author Raye
 *
 */
public interface ViewBinder<T> {

	public void binder(final Finder finder, final T target, Object source);
}
