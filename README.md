#ProIOC 预编译注解框架，解决在Android端使用注解性能低下，妈妈再也不用担心我在项目中使用注解降低性能了
目前框架有BindArray（绑定数组），BindById（绑定控件），BindData（绑定数据,适用于适配器），BindDimen（绑定dimen值），BindString（绑定字符串），OnCheckedChanged（OnCheckedChanged监听），OnClick（OnClick监听），OnItemClick（AdapterView的OnItemClick监听），OnTouch（OnTouch监听）这些常用的注解，如果有需要，以后会继续完善<br/>
[使用说明](http://git.oschina.net/raywang2014/PreIOC/wikis/home)，目前说明还在完善中，项目中包含一个Android Studio的demo项目，里面包含了绝大多数注解的使用例子（我记得应该是全部）<br/>
### 性能指数<br/>
#### 通过和原生、Xutil、以及PreIOC进行绑定200个TextView和设置监听耗时进行对比<br/>
虚拟机<br/>
![虚拟机耗时](http://git.oschina.net/uploads/images/2015/1008/143534_98a82dd1_108170.png "虚拟机耗时")<br/>
真机（红米Note增强版）<br/>
![真机（红米Note增强版）](http://git.oschina.net/uploads/images/2015/1008/143705_afeff921_108170.png "真机（红米Note增强版）")