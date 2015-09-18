package wang.raye.preioc;

import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.util.Log;
import wang.raye.preioc.find.AbstractFind;
import wang.raye.preioc.find.ActivityFind;
/**
 * ��������ע�����
 * ����ͨע���ܲ�ͬ���Ǵ�ע���ǵ���Ԥ����õĴ���ʵ��ע�룬
 * �������ṩ���޽ӽ�ԭ��������
 * @author Raye
 *
 */
public class PreIOC {

	private static final boolean debug = true;
	private static final String TAG = PreIOC.class.getName();
	private static final ViewBinder<Object> NULL_BIND = new ViewBinder<Object>() {
		
		@Override
		public void binder(AbstractFind finder, Object target, Object source) {
			
		}
	};
	/** ����*/
	private static final Map<Class<?>, ViewBinder<Object>> BINDERS = new LinkedHashMap<>();
	

	public static void binder(Activity activity) {
		binder(activity,activity,new ActivityFind());
	}

	private static void binder(Object target, Object source, AbstractFind finder) {
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
