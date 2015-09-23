package wang.raye.preioc;

import android.widget.BaseAdapter;

/**
 * 将数据绑定到控件上的接口，用于适配器
 * @author Raye
 *
 */
public interface ViewDataBinder<T> {

	public void bindData(final T t,final BaseAdapter adapter);
}
