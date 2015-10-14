package wang.raye.preioc.find;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
/**
 * dialog的查询控件
 * @author Raye
 *
 */
public class DialogFind extends AbstractFind {

	@Override
	protected View findView(Object source, int id) {
		return ((Dialog) source).findViewById(id);
	}

	@Override
	public Context getContext(Object source) {
		return ((Dialog) source).getContext();
	}

}
