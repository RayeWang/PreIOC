package wang.raye.preioc;

import android.widget.BaseAdapter;

/**
 * �����ݰ󶨵��ؼ��ϵĽӿڣ�����������
 * @author Raye
 *
 */
public interface ViewDataBinder<T> {

	public void bindData(final T t,final BaseAdapter adapter);
}
