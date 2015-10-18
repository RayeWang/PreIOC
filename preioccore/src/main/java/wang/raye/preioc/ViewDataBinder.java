package wang.raye.preioc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;

/**
 * 将数据绑定到控件上的接口，用于适配器
 * @author Raye
 *
 */
public abstract class ViewDataBinder<T extends BaseAdapter> {

	protected T adapter;
	protected LayoutInflater inflater;
	/**
	 * 绑定数据的方法
	 * @param convertView getView的View对象
	 * @param position 当前数据行
	 */
	public abstract View bindData(View convertView,int position);

	/**
	 * 初始化绑定方法
	 * @param context
	 * @param adapter
	 */
	protected final void init(Context context,T adapter){
		this.inflater = LayoutInflater.from(context);
		this.adapter = adapter;
	}
}
