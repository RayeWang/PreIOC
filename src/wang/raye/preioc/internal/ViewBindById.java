package wang.raye.preioc.internal;
/**
 * �����˰󶨵����ԵĲ�����field���Լ��ؼ���id
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
