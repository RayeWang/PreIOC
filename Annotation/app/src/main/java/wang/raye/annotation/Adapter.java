package wang.raye.annotation;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import wang.raye.preioc.PreIOC;
import wang.raye.preioc.annotation.BindById;
import wang.raye.preioc.annotation.BindData;
import wang.raye.preioc.annotation.OnClick;

public class Adapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<Bean> beans;


	public Adapter(Context context){
		this.inflater = LayoutInflater.from(context);
		beans = new ArrayList<Bean>();
		for(int i = 0;i < 100;i++){
			beans.add(new Bean("name:"+i));
		}
	}
	@Override
	public int getCount() {
		if(beans != null){
			return beans.size();
		}
		return 0;
	}

	@Override
	public Bean getItem(int position) {
		return beans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder view;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.item, parent,false);
			view = new ViewHolder(convertView);
			convertView.setTag(view);
		}else{
			view = (ViewHolder) convertView.getTag();
		}
		PreIOC.binderData(view,this,position);
	
		return convertView;
	}

	protected String format(int position){
		return "format:"+position;
	}
	static class ViewHolder{
		@BindById(R.id.name)
		@BindData(value = "name")
		TextView name;

		@BindById(R.id.format)
		@BindData(format = "format")
		TextView format;
		
		ViewHolder(View convertView){
			PreIOC.binder(this,convertView);
		}

	}
}
