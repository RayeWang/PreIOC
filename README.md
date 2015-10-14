#PreIOC Ԥ����ע���ܣ������Android��ʹ��ע�����ܵ��£�������Ҳ���õ���������Ŀ��ʹ��ע�⽵�������ˣ�����Ⱥ��123965382��
### [ʹ���ĵ�](http://git.oschina.net/raywang2014/PreIOC/wikis/home)

Ŀǰ�����BindArray�������飩��BindById���󶨿ؼ�����BindData��������,����������������BindDimen����dimenֵ����BindString�����ַ�������OnCheckedChanged��OnCheckedChanged��������OnClick��OnClick��������OnItemClick��AdapterView��OnItemClick��������OnTouch��OnTouch��������Щ���õ�ע�⣬�������Ҫ���Ժ���������<br/>
[ʹ��˵��](http://git.oschina.net/raywang2014/PreIOC/wikis/home)��Ŀǰ˵�����������У���Ŀ�а���һ��Android Studio��demo��Ŀ����������˾������ע���ʹ�����ӣ��Ҽǵ�Ӧ����ȫ����<br/>
### ����ָ��<br/>
#### ͨ����ԭ����Xutil���Լ�PreIOC���а�200��TextView������OnClick������ʱ���жԱ�<br/>
�����<br/>
![�������ʱ](http://git.oschina.net/uploads/images/2015/1008/143534_98a82dd1_108170.png "�������ʱ")<br/>
���������Note��ǿ�棩<br/>
![���������Note��ǿ�棩](http://git.oschina.net/uploads/images/2015/1008/143705_afeff921_108170.png "���������Note��ǿ�棩")<br/>
### ���׽̳�<br/>
[����PreIOC.jar](http://git.oschina.net/raywang2014/PreIOC/raw/master/Annotation/app/libs/PreIOC-0.0.1.jar)<br/>
#### Android Studio<br/>
�����غõ�PreIOC.jar����libs�ļ����£������Ӧ�ã������ֶ��޸�build.gradle��Ҳ����ͨ���Ҽ�ֱ�����Ӧ�ã�AS1.3��<br/>
#### Eclipse<br/>
�����غõ�PreIOC.jar����libs�ļ����£��һ���Ŀ��ѡ��Properties->Java Compiler-><br/>
Ȼ����������<br/>
![Eclipse����](http://git.oschina.net/uploads/images/2015/1008/150802_0f88345f_108170.png "Eclipse����")<br/>
�ٵ�����½���<br/>
![Eclipse����](http://git.oschina.net/uploads/images/2015/1008/150911_eddcb114_108170.png "Eclipse����")<br/>
���úú���add jar�����PreIOC.jar<br/>
![���PreIOC.jar](http://git.oschina.net/uploads/images/2015/1008/151001_7e69114d_108170.png "���PreIOC.jar")<br/>
���ok��Ȼ��apply��OK��<br/>
ʾ�����룬��ΪAnnotation��Ŀ�е�MainActivity<br/>
```
package wang.raye.annotation;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import wang.raye.preioc.PreIOC;
import wang.raye.preioc.annotation.BindArray;
import wang.raye.preioc.annotation.BindById;
import wang.raye.preioc.annotation.BindDimen;
import wang.raye.preioc.annotation.BindString;
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

    @BindArray(R.array.stringArray)
    String[] strArray;

    @BindArray(R.array.intArray)
    int[] intArray;
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

    @OnClick({R.id.bindDimen,R.id.stringArray,R.id.intArray})
    public void get(View view){
        switch (view.getId()){
            case R.id.bindDimen:
                Toast.makeText(this,"size is " + size,Toast.LENGTH_SHORT).show();
                break;
            case R.id.stringArray:
                Toast.makeText(this,strArray[1],Toast.LENGTH_SHORT).show();
                break;
            case R.id.intArray:
                Toast.makeText(this,"int array index 1 is :" + intArray[1],Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

```