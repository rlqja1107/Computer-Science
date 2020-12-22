//오른쪽 또는 왼쪽 가리키는 얘가 leaf일 수도 있고, non-leaf일 수도 있다.
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
