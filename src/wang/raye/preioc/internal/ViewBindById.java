package wang.raye.preioc.internal;
/**
 * �����˰󶨵����ԵĲ�����field���Լ��ؼ���id
 * @author Raye
 *
 */
public final class ViewBindById {
	 private final int id;
	 private FieldViewBindTypeAndName field;
	 
	 public ViewBindById(int id) {
		 this.id = id;
	 }
	 
	 protected void setField(FieldViewBindTypeAndName field){
		 this.field = field;
	 }

	public int getId() {
		return id;
	}

	public FieldViewBindTypeAndName getField() {
		return field;
	}
	 
	 
}
