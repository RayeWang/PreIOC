package wang.raye.preioc;

import android.widget.BaseAdapter;

/**
 * �����ݰ󶨵��ؼ��ϵĽӿڣ�����������
 * @author Raye
 *
 */
public interface ViewDataBinder<T,A extends BaseAdapter> {

	/**
	 * �����ݵķ���
	 * @param t ViewHolder�Ķ���
	 * @param adapter ������
	 * @param position ��ǰ������
	 */
	public void bindData(final T t,final A adapter,int position);
}
