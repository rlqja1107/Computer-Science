import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class NonLeafNode{
	static int capacity;
	NonLeafNode rightNode;
	NonLeafNode nextNode;
	NonLeafNode previousNode;
	NonLeafNode parentNode=null;
	boolean root;
	static NonLeafNode rootNode;
	ArrayList<NonLeafElement> data;
	NonLeafNode(){
	}

	NonLeafNode(int capa,boolean root){
		this.root=root;
		if(root) NonLeafNode.rootNode=this;
		NonLeafNode.capacity=capa;
		data=new ArrayList<NonLeafElement>();
		this.nextNode=null;
		this.previousNode=null;
	}

	public static void rangeSearch(int start,int end, NonLeafNode root) {
		LeafNode startNode=(LeafNode)searchKey_Node(start,root);
		for(int i=0;i<startNode.ldata.size();i++) {
			if(startNode.ldata.get(i).getKey()>=start&&startNode.ldata.get(i).getKey()<=end) {
				System.out.println(startNode.ldata.get(i).getKey()+","+startNode.ldata.get(i).getValue());
			}
		}
		nextRangeSearch(start,end,(LeafNode)startNode.nextNode);

	}
	private static void nextRangeSearch(int start,int end,LeafNode node) {
		for(int i=0;i<node.ldata.size();i++) {
			if(node.ldata.get(i).getKey()<=end) {
				System.out.println(node.ldata.get(i).getKey()+","+node.ldata.get(i).getValue());
			}
			else return;
		}
		if(node.nextNode!=null) nextRangeSearch(start,end,(LeafNode)node.nextNode);
	}
	//해당 키가 있는 leafnode 반환, print 생략
	public static NonLeafNode searchKey_Node(int key,NonLeafNode root) {
		if(root.getClass().getName().equals("NonLeafNode")) {
			boolean flag=true;
			NonLeafNode where=null;
			int size=root.data.size();
			for(int i=0;i<size;i++) {
				//System.out.print(root.data.get(i).getKey()+",");
				if(flag&&root.data.get(i).getKey()>key) {
					where=root.data.get(i).leftNode;
					flag=false;
				}
				else if(flag&&i==size-1) {
					where=root.rightNode;
				}
			}

			return searchKey_Node(key,where);
		}
		else {
			return (LeafNode)root;
		}
	}
	public static int search_singlekey(int key,NonLeafNode root) {
		if(root.getClass().getName().equals("NonLeafNode") ) {
			NonLeafNode where=null;
			boolean flag=true;
			int size=root.data.size();
			for(int i=0;i<size;i++) {
				System.out.print(root.data.get(i).getKey()+" ");
				if(flag&&root.data.get(i).getKey()>key) {
					where=root.data.get(i).leftNode;
					flag=false;
					break;
				}
				else if(flag&&i==size-1) {
					where=root.rightNode;
				}
			}
			System.out.println();
			return search_singlekey(key,where);
		}
		else {
			LeafNode leaf=(LeafNode)root;
			for(int i=0;i<leaf.ldata.size();i++) {
				if(leaf.ldata.get(i).getKey()==key) {
					System.out.println("value : "+leaf.ldata.get(i).getValue());
					return leaf.ldata.get(i).getValue();
				}
			}
			System.out.println("NOT FOUND");
			//없으면 Integer에서 가장 작은 값을 반환
			return Integer.MIN_VALUE;
		}
	}

	
	//non-leaf node의 split
	//node는 child에서 새로 생긴 노드를 받음
	public void split() {
		NonLeafNode newnode=new NonLeafNode(NonLeafNode.capacity,this.root);
		if(this.nextNode!=null) {
			newnode.nextNode=this.nextNode;
			this.nextNode.previousNode=newnode;
		}
		if(this.parentNode!=null) {
			newnode.parentNode=this.parentNode;
		}
		this.nextNode=newnode;
		newnode.previousNode=this;
		int mid=this.data.size()/2;
		int count=0;
		int remove_size=this.data.size()-mid;
		NonLeafElement temp;
		while(count!=remove_size) {
			temp=this.data.remove(mid);
			temp.leftNode.parentNode=newnode;
			newnode.data.add(temp);
			count++;
		}
		newnode.rightNode=this.rightNode;
		newnode.rightNode.parentNode=newnode;
		this.rightNode=newnode.data.get(0).leftNode;
		this.rightNode.parentNode=this;
		
		if(this.root) {
			NonLeafNode topNode=new NonLeafNode(LeafNode.capacity,true);
			topNode.data.add(new NonLeafElement(newnode.data.remove(0).getKey(),this));
			this.parentNode=topNode;
			newnode.parentNode=topNode;
			//	this.rightNode=newnode.data.get(0).getLeftNode();
			topNode.rightNode=this.nextNode;
			this.root=false;
			newnode.root=false;
		}
		else {
			this.parentNode.<NonLeafNode>inner_insert_key(this.nextNode.data.remove(0).getKey(),this.nextNode,this);
		}
	}


	//key: insert할 키, node는 넣은 다음 노드의 왼쪽 노드
	public <P extends NonLeafNode> void inner_insert_key(int key,P node,P first) {
		if(data.size()==0) { 
			data.add(new NonLeafElement(key,first));
			this.rightNode=node;
		}
		else {
			int size=data.size();
			for(int i=0;i<size;i++) {
				if(data.get(i).getKey()>key){
					NonLeafElement temp=new NonLeafElement(key,this.data.get(i).getLeftNode());
					data.add(i,temp);
					data.get(i+1).setLeftNode(node);
					break;
				}
				else if(size-1==i) {
					NonLeafElement temp=new NonLeafElement(key,this.rightNode);
					data.add(temp);
					this.rightNode=node;
					break;
				}
			}
			if(data.size()>=NonLeafNode.capacity) {
				this.split();
			}
		}
	}
	public static void merge(NonLeafNode cur) {
		//root가 아니면 parent에서 가져오기
		NonLeafNode parent=cur.parentNode;
		NonLeafNode next=cur.nextNode;
		NonLeafNode previous=cur.previousNode;
		int size=parent.data.size();
		if(!cur.root) {
			//next가 있고 합쳤을 때 overflow가 발생하지 않는 선에서 merge작용
			//parent도 서로 같아야함.
			if(next!=null&&isNodeCanMerge(cur,next)) {
				for(int i=0;i<size;i++) {
					if(parent.data.get(i).leftNode==cur) {
						boolean first=true;
						while(cur.data.size()>0) {
							if(first) {
								next.data.add(0,new NonLeafElement(parent.data.get(i).getKey(),cur.rightNode));
								first=false;
							}
							else {
								cur.data.get(cur.data.size()-1).leftNode.parentNode=next;
								next.data.add(0,cur.data.remove(cur.data.size()-1));
							}
						}
						parent.data.remove(i);

						if(parent.data.size()==0) {
							next.root=true;
							NonLeafNode.rootNode=next;
							parent=null;
							next.parentNode=null;
						}
						if(cur.previousNode!=null) {
							cur.previousNode.nextNode=next;
							next.previousNode=cur.previousNode;
						}
						cur=null;
						break;
					}
				}
			}
			//회전방식 이용 merge했을 때 overflow가 발생시
			else if(next!=null&&next.data.size()+cur.data.size()>NonLeafNode.capacity-1&&next.parentNode==cur.parentNode) {
				for(int i=0;i<size;i++) {
					if(parent.data.get(i).leftNode==cur) {
						NonLeafElement temp=next.data.remove(0);
						NonLeafElement parent_node=parent.data.get(i);
						cur.data.add(new NonLeafElement(parent_node.getKey(),cur.rightNode));
						cur.rightNode=temp.leftNode;
						if(cur.rightNode.getClass().getName().equals("NonLeafNode")) {
							LeafNode.check_keychange(cur.rightNode.data.get(0).getKey(),cur,cur.rightNode);
						}
						else {
							LeafNode.check_keychange(((LeafNode)cur.rightNode).ldata.get(0).getKey(),cur,cur.rightNode);
						}
						parent_node.setKey(temp.getKey());
						break;
					}
				}
			}
			//해당 레벨의 끝노드에서 걸림
			//전 노드와 합쳤을 때 overflow가 발생하지 않을 때 ->합침
			else if(previous!=null&&isNodeCanMerge(cur,previous)) {
				NonLeafElement last_node=parent.data.get(parent.data.size()-1);
				boolean first=true;
				while(previous.data.size()>0) {
					if(first) {
						cur.data.add(0,new NonLeafElement(last_node.getKey(),previous.rightNode));
						first=false;
					}
					else {
						previous.data.add(0,cur.data.remove(cur.data.size()-1));
					}
				}
				parent.data.remove(last_node);
				if(parent.data.size()==0) {
					cur.root=true;
					NonLeafNode.rootNode=cur;
					parent=null;
				}
				if(previous.previousNode!=null) {
					previous.previousNode.nextNode=cur;
					cur.previousNode=previous.previousNode;
				}
				previous=null;
			}
			//이전 노드와 합쳐질 경우 overflow가 발생시->하나의 element만 bring하기(회전)
			else if(previous!=null&&previous.parentNode==cur.parentNode) {
				NonLeafElement last=parent.data.get(parent.data.size()-1);
				cur.data.add(new NonLeafElement(last.getKey(),previous.rightNode));
				previous.rightNode=previous.data.get(previous.data.size()-1).leftNode;
				last.setKey(previous.data.remove(previous.data.size()-1).getKey());
			}
		}
		//merge하려고했더니 root다.
		else {
			if(parent.data.size()==0)
				parent=null;
		}
	}
	public static NonLeafNode read(Scanner scan,NonLeafNode root,NonLeafNode node,int count,int root_size,ArrayList<NonLeafNode> parent_list,
			boolean first,NonLeafNode right) {
		
		String leaf=scan.next();
		if(leaf.equals("NonLeaf")) {
			NonLeafNode newnode=new NonLeafNode(NonLeafNode.capacity,false);
			int size=scan.nextInt();
			//정보 넣기
			for(int i=0;i<size;i++) {
				newnode.data.add(new NonLeafElement(scan.nextInt(),null));
			}
			parent_list.add(newnode);
			if(node!=null) {
				node.nextNode=newnode;
				newnode.previousNode=node;
			}
			if(first) {
				newnode.previousNode=right;
				right.nextNode=newnode;
			}
			newnode.parentNode=root;
			if(count==root_size) {
				root.rightNode=newnode;
				return newnode;
			}
			else {
				root.data.get(count).leftNode=newnode;
				count++;
				return NonLeafNode.read(scan,root,newnode,count,root_size,parent_list,false,null);
			}
		}
		else {
			return null;
		}
		
	}
	public <P extends NonLeafNode>void print(PrintWriter print) {
		if(this.getClass().getName().equals("NonLeafNode")) {
			print.print("NonLeaf "+this.data.size()+" ");
			for(int i=0;i<this.data.size();i++) {
				print.print(this.data.get(i).getKey()+" ");
			}
			print.println();
			if(this.nextNode!=null) {
				this.nextNode.print(print);
			}
		}
		else {
			LeafNode node=(LeafNode)this;
			print.print("Leaf "+" "+node.ldata.size()+" ");
			for(int i=0;i<node.ldata.size();i++) {
				print.print(node.ldata.get(i).getKey()+" "+node.ldata.get(i).getValue()+" ");
			}
			print.println();
			if(this.nextNode!=null) {
				this.nextNode.print(print);
			}
		}
	}
	//둘이 합쳐도 overflow가 생기지 않게 조정
	public static boolean isNodeCanMerge(NonLeafNode cur,NonLeafNode other) {
		if(cur.data.size()+other.data.size()<=LeafNode.capacity-2&&cur.parentNode==other.parentNode)return true;
		else return false;
	}
	public static boolean isUnderflow(NonLeafNode node) {
		if((NonLeafNode.capacity-1)/2>node.data.size())return true;
		else return false;
	}
	
	

}
