//������ �Ǵ� ���� ����Ű�� �갡 leaf�� ���� �ְ�, non-leaf�� ���� �ִ�.
public class NonLeafElement {
	private int key;
	 NonLeafNode leftNode=null;
	NonLeafElement(int key,NonLeafNode node){
		this.key=key;
		this.leftNode=node;
	}
	public void setLeftNode(NonLeafNode node) {
		this.leftNode= node;
	}
	public NonLeafNode getLeftNode() {
		return this.leftNode;
	}
	public void setKey(int key) {
		this.key=key;
	}
	
	public int getKey() {
		return this.key;
	}
}
