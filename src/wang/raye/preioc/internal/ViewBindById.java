package wang.raye.preioc.internal;
/**
 * 保存了绑定的属性的参数（field）以及控件的id
 * @author Ray
 *
 */
public final class ViewBindById {
	 private final int id;
	 private FieldViewBindTypeAndName field;
	 
	 protected ViewBindById(int id) {
		 this.id = id;
	 }
	 
	 protected void setField(FieldViewBindTypeAndName field){
		 this.field = field;
	 }
}
