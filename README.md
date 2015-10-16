#PreIOC 预编译注解框架，解决在Android端使用注解性能低下，妈妈再也不用担心我在项目中使用注解降低性能了（交流群：123965382）
### [使用文档](http://git.oschina.net/raywang2014/PreIOC/wikis/home)

目前框架有BindArray（绑定数组），BindById（绑定控件），BindData（绑定数据,适用于适配器），BindDimen（绑定dimen值），BindString（绑定字符串），OnCheckedChanged（OnCheckedChanged监听），OnClick（OnClick监听），OnItemClick（AdapterView的OnItemClick监听），OnTouch（OnTouch监听）这些常用的注解，如果有需要，以后会继续完善<br/>
[使用说明](http://git.oschina.net/raywang2014/PreIOC/wikis/home)，目前说明还在完善中，项目中包含一个Android Studio的demo项目，里面包含了绝大多数注解的使用例子（我记得应该是全部）<br/>
### Maven及Gradle相关配置
#### Maven
```
<dependency>
	<groupId>wang.raye.preioc</groupId>
  	<artifactId>preioccore</artifactId>
	<version>1.0.2</version>
</dependency>
```
#### Gradle
```
compile 'wang.raye.preioc:preioccore:1.0.2'
```
### 性能指数<br/>
#### 通过和原生、Xutil、以及PreIOC进行绑定200个TextView和设置OnClick监听耗时进行对比<br/>
虚拟机<br/>
![虚拟机耗时](http://git.oschina.net/uploads/images/2015/1008/143534_98a82dd1_108170.png "虚拟机耗时")<br/>
真机（红米Note增强版）<br/>
![真机（红米Note增强版）](http://git.oschina.net/uploads/images/2015/1008/143705_afeff921_108170.png "真机（红米Note增强版）")<br/>
### 简易教程<br/>
[下载PreIOC.jar](http://git.oschina.net/raywang2014/PreIOC/raw/master/Annotation/app/libs/PreIOC-0.0.1.jar)<br/>
#### Android Studio<br/>
##### 方法一
在build.gradle 中配置 
```
compile 'wang.raye.preioc:preioccore:1.0.2'
```
##### 方法二

将下载好的PreIOC.jar放在libs文件夹下或通过maven引用，并添加应用，可以手动修改build.gradle，也可以通过右键直接添加应用（AS1.3）<br/>
#### Eclipse<br/>
将下载好的PreIOC.jar放在libs文件夹下，右击项目，选择Properties->Java Compiler-><br/>
然后如下设置<br/>
![Eclipse设置](http://git.oschina.net/uploads/images/2015/1008/150802_0f88345f_108170.png "Eclipse设置")<br/>
再点击如下界面<br/>
![Eclipse设置](http://git.oschina.net/uploads/images/2015/1008/150911_eddcb114_108170.png "Eclipse设置")<br/>
设置好后点击add jar，添加PreIOC.jar<br/>
![添加PreIOC.jar](http://git.oschina.net/uploads/images/2015/1008/151001_7e69114d_108170.png "添加PreIOC.jar")<br/>
点击ok，然后apply就OK了<br/>
示例代码，此为Annotation项目中的MainActivity<br/>
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