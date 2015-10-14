package wang.raye.preioc.find;

import android.app.Activity;
import android.content.Context;
import android.view.View;
/**
 * activity的查询控件
 * @author Raye
 *
 */
public class ActivityFind extends AbstractFind {

	@Override
	protected View findView(Object source, int id) {
		return ((Activity) source).findViewById(id);
	}

	@Override
	public Context getContext(Object source) {
		return (Activity) source;
	}

}
