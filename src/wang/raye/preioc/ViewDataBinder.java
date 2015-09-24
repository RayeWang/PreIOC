package wang.raye.preioc;

import android.widget.BaseAdapter;

/**
 * 将数据绑定到控件上的接口，用于适配器
 * @author Raye
 *
 */
public interface ViewDataBinder<T,A extends BaseAdapter> {

	/**
	 * 绑定数据的方法
	 * @param t ViewHolder的对象
	 * @param adapter 适配器
	 * @param position 当前数据行
	 */
	public void bindData(final T t,final A adapter,int position);
}
