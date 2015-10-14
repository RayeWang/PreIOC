package wang.raye.preioc.find;

import android.content.Context;
import android.view.View;
/**
 * 控件的find
 * @author Raye
 *
 */
public class ViewFind extends AbstractFind{

	@Override
	protected View findView(Object source, int id) {
		return ((View) source).findViewById(id);
	}

	@Override
	public Context getContext(Object source) {
		return ((View) source).getContext();
	}

	@Override
	protected String getResourceEntryName(Object source, int id) {
		final View view = (View) source;
		if (view.isInEditMode()) {
			return "<unavailable while editing>";
		}
		return super.getResourceEntryName(source, id);
	}
}
