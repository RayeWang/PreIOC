package wang.raye.preioc;

import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.util.Log;
import wang.raye.preioc.find.AbstractFind;
import wang.raye.preioc.find.ActivityFind;
/**
 * 进行依赖注入的类
 * 与普通注解框架不同的是此注解是调用预编译好的代码实现注入，
 * 所以能提供无限接近原生的性能
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
	/** 缓存*/
	private static final Map<Class<?>, ViewBinder<Object>> BINDERS = new LinkedHashMap<>();
	

	public static void binder(Activity activity) {
		binder(activity,activity,new ActivityFind());
	}

	private static void binder(Object target, Object source, AbstractFind finder) {
		Class<?> targetClass = target.getClass();
		try {
			ViewBinder<Object> viewBinder = findViewBinderForClass(targetClass);
			if (viewBinder != null) {
				Log.i(TAG, "实例化成功，开始绑定:"+viewBinder.getClass().getName());
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
				Log.d(TAG, "ProIOC: 从缓存中获取到.");
			return viewBinder;
		}
		
		String clsName = cls.getName();
		if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
			if (debug)
				Log.w(TAG, "ProIOC: 当前类不支持注入");
			return NULL_BIND;
		}
		try {
			Class<?> viewBindingClass = Class.forName(clsName + "$$ViewBinder");
			viewBinder = (ViewBinder<Object>) viewBindingClass.newInstance();
			if (debug)
				Log.d(TAG, "ProIOC: 成功加载ViewBinder.");
		} catch (ClassNotFoundException e) {
			if (debug)
				Log.w(TAG, "实例化失败，重新搜寻 " + cls.getSuperclass().getName());
			viewBinder = findViewBinderForClass(cls.getSuperclass());
		}
		BINDERS.put(cls, viewBinder);
		return viewBinder;
	}
	
	
}
