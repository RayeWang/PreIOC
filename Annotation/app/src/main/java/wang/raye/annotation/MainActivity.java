package wang.raye.annotation;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import wang.raye.preioc.PreIOC;
import wang.raye.preioc.annotation.BindById;
import wang.raye.preioc.annotation.BindDimen;
import wang.raye.preioc.annotation.BindString;
import wang.raye.preioc.annotation.BindStringArray;
import wang.raye.preioc.annotation.OnCheckedChanged;
import wang.raye.preioc.annotation.OnClick;
import wang.raye.preioc.annotation.OnItemClick;
import wang.raye.preioc.annotation.OnTouch;

public class MainActivity extends ActionBarActivity {

    @BindById(R.id.listView)
    ListView listView;

    @BindById(R.id.bindString)
    TextView bindString;
    @BindString(R.string.test)
    String name;
    @BindDimen(R.dimen.activity_horizontal_margin)
    int size;

    @BindStringArray(R.array.stringArray)
    String[] strArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreIOC.binder(this);
        listView.setAdapter(new Adapter(this));
        bindString.setText(name);
    }

    @OnClick({R.id.click})
    public void click(View view){
        Toast.makeText(this,"this is click",Toast.LENGTH_SHORT).show();

    }


    @OnTouch({R.id.touch})
     public boolean touch(View view,MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_UP){
            Toast.makeText(this,"this is touch",Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @OnCheckedChanged({R.id.checkbox})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            Toast.makeText(this,"is checked",Toast.LENGTH_SHORT).show();
        }
    }

    @OnItemClick({R.id.listView})
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this,"this position is:"+position+"  id:"+id,Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.bindDimen,R.id.stringArray})
    public void get(View view){
        switch (view.getId()){
            case R.id.bindDimen:
                Toast.makeText(this,"size is " + size,Toast.LENGTH_SHORT).show();
                break;
            case R.id.stringArray:
                Toast.makeText(this,strArray[1],Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
