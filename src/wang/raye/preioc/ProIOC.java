package wang.raye.preioc;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
/**
 * ��������ע�����
 * ����ͨע���ܲ�ͬ���Ǵ�ע���ǵ���Ԥ����õĴ���ʵ��ע�룬
 * �������ṩ���޽ӽ�ԭ��������
 * @author Raye
 *
 */
public class ProIOC {

	private static final boolean debug = true;
	private static final String TAG = ProIOC.class.getName();
	private static final ViewBinder<Object> NULL_BIND = new ViewBinder<Object>() {
		
		@Override
		public void binder(Finder finder, Object target, Object source) {
			
		}
	};
	/** ����*/
	private static final Map<Class<?>, ViewBinder<Object>> BINDERS = new LinkedHashMap<>();
	public enum Finder {
		VIEW {
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
				// In edit mode, getResourceEntryName() is unsupported due to
				// use of BridgeResources
				if (view.isInEditMode()) {
					return "<unavailable while editing>";
				}
				return super.getResourceEntryName(source, id);
			}
		},
		ACTIVITY {
			@Override
			protected View findView(Object source, int id) {
				return ((Activity) source).findViewById(id);
			}

			@Override
			public Context getContext(Object source) {
				return (Activity) source;
			}
		},
		DIALOG {
			@Override
			protected View findView(Object source, int id) {
				return ((Dialog) source).findViewById(id);
			}

			@Override
			public Context getContext(Object source) {
				return ((Dialog) source).getContext();
			}
		};
		/** ȥ����null��view���� */
		private static <T> T[] filterNull(T[] views) {
			int end = 0;
			for (int i = 0; i < views.length; i++) {
				T view = views[i];
				if (view != null) {
					views[end++] = view;
				}
			}
			return Arrays.copyOfRange(views, 0, end);
		}

		public static <T> T[] arrayOf(T... views) {
			return filterNull(views);
		}

		public static <T> List<T> listOf(T... views) {
			return new ImmutableList<>(filterNull(views));
		}

		public <T> T findRequiredView(Object source, int id, String who) {
			T view = findOptionalView(source, id, who);
			if (view == null) {
				String name = getResourceEntryName(source, id);
				throw new IllegalStateException("Required view '" + name + "' with ID " + id + " for " + who
						+ " was not found. If this view is optional add '@Nullable' annotation.");
			}
			return view;
		}

		/**
		 * �ҵ�View
		 * 
		 * @param source
		 * @param id
		 * @param who
		 * @return
		 */
		public <T> T findOptionalView(Object source, int id, String who) {
			View view = findView(source, id);
			return castView(view, id, who);
		}

		/**
		 * ��Viewת����ָ����View
		 * 
		 * @return
		 */
		@SuppressWarnings("unchecked") // That's the point.
		public <T> T castView(View view, int id, String who) {
			try {
				return (T) view;
			} catch (ClassCastException e) {
				if (who == null) {
					throw new AssertionError();
				}
				String name = getResourceEntryName(view, id);
				throw new IllegalStateException("View '" + name + "' with ID " + id + " for " + who
						+ " was of the wrong type. See cause for more info.", e);
			}
		}

		@SuppressWarnings("unchecked") // That's the point.
		public <T> T castParam(Object value, String from, int fromPosition, String to, int toPosition) {
			try {
				return (T) value;
			} catch (ClassCastException e) {
				throw new IllegalStateException("Parameter #" + (fromPosition + 1) + " of method '" + from
						+ "' was of the wrong type for parameter #" + (toPosition + 1) + " of method '" + to
						+ "'. See cause for more info.", e);
			}
		}

		protected String getResourceEntryName(Object source, int id) {
			return getContext(source).getResources().getResourceEntryName(id);
		}

		protected abstract View findView(Object source, int id);

		public abstract Context getContext(Object source);
	}

	public static void binder(Activity activity) {
		binder(activity,activity,Finder.ACTIVITY);
	}

	private static void binder(Object target, Object source, Finder finder) {
		Class<?> targetClass = target.getClass();
		try {
			ViewBinder<Object> viewBinder = findViewBinderForClass(targetClass);
			if (viewBinder != null) {
				Log.i(TAG, "ʵ�����ɹ�����ʼ��:"+viewBinder.getClass().getName());
				viewBinder.binder(finder, target, source);
			}
		} catch (Exception e) {
			throw new RuntimeException("Unable to bind views for " + targetClass.getName(), e);
		}
	}

	private static ViewBinder<Object> findViewBinderForClass(Class<?> cls)
			throws IllegalAccessException, InstantiationException {
		ViewBinder<Object> viewBinder = viewBinder = BINDERS.get(cls);
		if (viewBinder != null) {
			if (debug)
				Log.d(TAG, "ProIOC: �ӻ����л�ȡ��.");
			return viewBinder;
		}
		
		String clsName = cls.getName();
		if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
			if (debug)
				Log.w(TAG, "ProIOC: ��ǰ�಻֧��ע��");
			return NULL_BIND;
		}
		try {
			Class<?> viewBindingClass = Class.forName(clsName + "$$ViewBinder");
			// noinspection unchecked
			viewBinder = (ViewBinder<Object>) viewBindingClass.newInstance();
			if (debug)
				Log.d(TAG, "ProIOC: �ɹ�����ViewBinder.");
		} catch (ClassNotFoundException e) {
			if (debug)
				Log.w(TAG, "ʵ����ʧ�ܣ�������Ѱ " + cls.getSuperclass().getName());
			viewBinder = findViewBinderForClass(cls.getSuperclass());
		}
		BINDERS.put(cls, viewBinder);
		return viewBinder;
	}
	
	
}
