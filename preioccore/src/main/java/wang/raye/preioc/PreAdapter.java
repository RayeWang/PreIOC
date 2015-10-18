package wang.raye.preioc;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by raye on 15-10-18.
 */
public class PreAdapter<T> extends BaseAdapter {
    private ArrayList<T> datas;
    protected ViewDataBinder binder;
    public PreAdapter(ArrayList<T> datas,Context context,Class clz){
        this.datas = datas;
        this.binder = PreIOC.initViewHodler(context,clz,this);
    }

    @Override
    public int getCount() {
        if(datas != null){
            return datas.size();
        }
        return 0;
    }

    @Override
    public T getItem(int position) {

        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return binder.bindData(convertView,position);
    }
}
