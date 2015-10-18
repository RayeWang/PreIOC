package wang.raye.preiocdemo;

import android.content.Context;
import android.widget.TextView;

import java.util.ArrayList;

import wang.raye.preioc.PreAdapter;
import wang.raye.preioc.annotation.BindById;
import wang.raye.preioc.annotation.BindData;
import wang.raye.preioc.annotation.BindViewHolder;

/**
 * Created by raye on 15-10-18.
 */

public class Adapter extends PreAdapter<Bean>{

    public Adapter(ArrayList<Bean> datas, Context context) {
        super(datas, context, ViewHolder.class);
    }
    protected String format(int position){
        return "format:"+position;
    }
    @BindViewHolder(R.layout.item)
    static class ViewHolder {
        @BindById(R.id.name)
        @BindData(value = "name")
        TextView name;

        @BindById(R.id.format)
        @BindData(format = "format")
        TextView format;
    }

}