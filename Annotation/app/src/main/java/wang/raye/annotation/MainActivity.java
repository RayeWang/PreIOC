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
import android.widget.Toast;

import wang.raye.preioc.PreIOC;
import wang.raye.preioc.annotation.BindById;
import wang.raye.preioc.annotation.BindString;
import wang.raye.preioc.annotation.OnCheckedChanged;
import wang.raye.preioc.annotation.OnClick;
import wang.raye.preioc.annotation.OnItemClick;
import wang.raye.preioc.annotation.OnTouch;

public class MainActivity extends ActionBarActivity {

    @BindById(R.id.listView)
    ListView listView;

    @BindById(R.id.bindString)
    Button button;
    @BindString(R.string.test)
    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreIOC.binder(this);
        listView.setAdapter(new Adapter(this));
        button.setText(name);
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

    @OnClick({R.id.bindString})
    public void get(View view){
        Toast.makeText(this,"getDimensionPixelOffset is "+getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin)
                +"    getDimension "+getResources().getDimension(R.dimen.activity_horizontal_margin),Toast.LENGTH_SHORT).show();
    }
}
