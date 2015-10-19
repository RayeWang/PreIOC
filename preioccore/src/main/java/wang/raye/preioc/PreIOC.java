package wang.raye.preioc;

import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;

import wang.raye.preioc.annotation.BindData;
import wang.raye.preioc.find.AbstractFind;
import wang.raye.preioc.find.ActivityFind;
import wang.raye.preioc.find.DialogFind;
import wang.raye.preioc.find.ViewFind;
/**
 * 进行依赖注入的类
 * 与普通注解框架不同的是此注解是调用预编译好的代码实现注入，
 * 所以能提供无限接近原生的性能
 * @author Raye
 *
 */
public class PreIOC {

	private static final boolean debug = false;
	private static final String TAG = PreIOC.class.getName();
	private static final ViewBinder<Object> NULL_BIND = new ViewBinder<Object>() {

		@Override
		public void binder(AbstractFind finder, Object target, Object source) {

		}
	};
	/** 缓存*/
	private static final Map<Class<?>, ViewBinder<Object>> BINDERS = new LinkedHashMap<>();
	private static final Map<Class<?>, ViewDataBinder> DATABINDERS = new LinkedHashMap<>();

	public static void binder(Activity activity) {
		binder(activity,activity,new ActivityFind());
	}

	public static void binder(View view){
		binder(view, view, new ViewFind());
	}

	public static void binder(Dialog dialog){
		binder(dialog,dialog,new DialogFind());
	}

	/**
	 * 绑定指定的类的对象的属性
	 * @param target 被绑定的属性的所在类的对象
	 * @param activity 控件所在的Activity
	 */
	public static void binder(Object target,Activity activity){
		binder(target,activity,new ActivityFind());
	}

	/**
	 * 绑定指定类对象的属性
	 * @param target 被绑定的属性的所在类的对象
	 * @param dialog 控件所在Dialog
	 */
	public static void binder(Object target,Dialog dialog){
		binder(target,dialog,new DialogFind());
	}

	/**
	 * 初始化ViewHolder
	 * @param context
	 * @param clz
	 * @return
	 */
	public static ViewDataBinder initViewHodler(Context context,Class clz,BaseAdapter adapter){
		try {
			ViewDataBinder vdb = findVDBForClass(clz);
			if(vdb != null){
				vdb.init(context, adapter);
				return vdb;
			}
			return null;
		}catch(Exception e){
			throw new RuntimeException("Unable to bind data for " + clz.getName(), e);
		}
	}
//	/**
//	 * 绑定数据
//	 * @param viewHolder
//	 */
//	public static void binderData(Object viewHolder,BaseAdapter adapter,View convertView,int position){
//		Class<?> targetClass = viewHolder.getClass();
//		try {
//			ViewDataBinder vdb = findVDBForClass(targetClass);
//			if(vdb != null){
//				vdb.bindData( adapter,convertView, position);
//			}
//		}catch(Exception e){
//			throw new RuntimeException("Unable to bind data for " + targetClass.getName(), e);
//		}
//	}
	/**
	 * 绑定指定类对象的属性
	 * @param target 被绑定的属性的所在类的对象
	 * @param view 控件所在View
	 */
	public static void binder(Object target,View view){
		binder(target,view,new ViewFind());
	}

	private static void binder(Object target, Object source, AbstractFind finder) {
		Class<?> targetClass = target.getClass();
		try {
			ViewBinder<Object> viewBinder = findViewBinderForClass(targetClass);
			if (viewBinder != null) {
				if (debug)
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

	private static ViewDataBinder findVDBForClass(Class<?> cls)
			throws IllegalAccessException, InstantiationException {
		ViewDataBinder viewBinder = DATABINDERS.get(cls);
		if (viewBinder != null) {
			if (debug)
				Log.d(TAG, "ProIOC: 从缓存中获取到.");
			return viewBinder;
		}

		String clsName = cls.getName();
		if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
			if (debug)
				Log.w(TAG, "ProIOC: 当前类不支持注入");
			return null;
		}
		try {
			Class<?> viewBindingClass = Class.forName(clsName + "$$ViewDataBinder");
			viewBinder = (ViewDataBinder) viewBindingClass.newInstance();
			if (debug)
				Log.d(TAG, "ProIOC: 成功加载ViewBinder.");
		} catch (ClassNotFoundException e) {
			if (debug)
				Log.w(TAG, "实例化失败，重新搜寻 " + cls.getSuperclass().getName());
			viewBinder = findVDBForClass(cls.getSuperclass());
		}
		DATABINDERS.put(cls, viewBinder);
		return viewBinder;
	}


}
