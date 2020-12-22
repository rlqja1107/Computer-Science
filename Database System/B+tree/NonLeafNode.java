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
			if(where!=null)
				return searchKey_Node(key,where);
			else return LeafNode.first;
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

		if(!cur.root) {
			NonLeafNode next=cur.nextNode;
			NonLeafNode previous=cur.previousNode;
			NonLeafNode parent=cur.parentNode;
			int size=parent.data.size();
			//회전방식 이용(next 노드에서 가져와도 underflow가 발생하지 않을 때)
			if(next!=null&&isNodeCanBring(cur,next)) {
				for(int i=0;i<size;i++) {
					if(parent.data.get(i).leftNode==cur) {
						NonLeafElement temp=next.data.remove(0);
						NonLeafElement parent_node=parent.data.get(i);
						cur.data.add(new NonLeafElement(parent_node.getKey(),cur.rightNode));
						cur.rightNode=temp.leftNode;

						changeParentNode(temp.leftNode,cur);
//						if(cur.rightNode.getClass().getName().equals("LeafNode")) {
//							LeafNode temp_node=(LeafNode)cur.rightNode;
//							LeafNode.check_keychange(temp_node.ldata.get(0).getKey(), cur, cur.rightNode);
//						}
						parent_node.setKey(temp.getKey());
						if(cur.rightNode.getClass().getName().equals("LeafNode")) 
							cur.data.get(cur.data.size()-1).setKey(((LeafNode)cur.rightNode).ldata.get(0).getKey());

						break;
					}
				}
			}
			//부모노드 하나 데려와서 merge
			else if(next!=null&&next.parentNode==parent) {
				for(int i=0;i<size;i++) {
					if(parent.data.get(i).leftNode==cur) {
						next.data.add(0,new NonLeafElement(parent.data.get(i).getKey(),cur.rightNode));
						changeParentNode(cur.rightNode,next);
						while(cur.data.size()>0) {
							changeParentNode(cur.data.get(cur.data.size()-1).leftNode,next);
							next.data.add(0,cur.data.remove(cur.data.size()-1));
						}
						parent.data.remove(i);
						if(cur.previousNode!=null) {
							cur.previousNode.nextNode=next;
							next.previousNode=cur.previousNode;
						}
						if(parent.root&&parent.data.size()==0) {
							next.parentNode=null;
							next.root=true;
							NonLeafNode.rootNode=next;
							parent=null;
						}
						if(parent!=null&&isUnderflow(parent)) {
							merge(parent);
						}
						break;
					}
				}
			}
			//해당 레벨의 끝노드에서 걸림
			//회전방식으로 전노드의 데이터 하나 가져오기
			else if(previous!=null&&isNodeCanBring(cur,previous)) {
				NonLeafElement last=parent.data.get(parent.data.size()-1);
				cur.data.add(0,new NonLeafElement(last.getKey(),previous.rightNode));
				changeParentNode(previous.rightNode,cur);
				previous.rightNode=previous.data.get(previous.data.size()-1).leftNode;
				last.setKey(previous.data.remove(previous.data.size()-1).getKey());
				//child가 leafNode면 key변경
				if(cur.rightNode.getClass().getName().equals("LeafNode")) {
					//1은 minimum 노드의 수가 1일 경우 대비
					if(cur.data.size()==1) 
						cur.data.get(0).setKey(((LeafNode)cur.rightNode).ldata.get(0).getKey());

					else cur.data.get(0).setKey(((LeafNode)cur.data.get(1).leftNode).ldata.get(0).getKey());
				}
			}
			//부모노드의 element하나 가져와서 merge
			else if(previous!=null&&previous.parentNode==cur.parentNode) {
				cur.data.add(0,new NonLeafElement(parent.data.get(size-1).getKey(),previous.rightNode));
				changeParentNode(previous.rightNode,cur);
				while(previous.data.size()>0) {
					changeParentNode(previous.data.get(previous.data.size()-1).leftNode,cur);
					cur.data.add(0,previous.data.remove(previous.data.size()-1));
				}
				parent.data.remove(parent.data.size()-1);
				if(previous.previousNode!=null) {
					previous.previousNode.nextNode=cur;
					cur.previousNode=previous.previousNode;
				}
				if(parent.root&&parent.data.size()==0) {
					cur.root=true;
					parent=null;
					cur.parentNode=null;
					NonLeafNode.rootNode=cur;
				}
				if(parent!=null&&isUnderflow(parent)) 
					merge(parent);
				
			}
		}
	}
	private static void changeParentNode(NonLeafNode child,NonLeafNode parent) {
		if(child.getClass().getName().equals("LeafNode")) 
			((LeafNode)child).parentNode=parent;
		
		else 
			child.parentNode=parent;
		}
	
	public static NonLeafNode read(Scanner scan,NonLeafNode root,NonLeafNode node,int count,int root_size,ArrayList<NonLeafNode> parent_list,
			boolean first,NonLeafNode right) {
		String leaf=scan.next();
		if(leaf.equals("N")) {
			NonLeafNode newnode=new NonLeafNode(NonLeafNode.capacity,false);
			int size=Integer.parseInt(scan.next());
			//정보 넣기
			for(int i=0;i<size;i++) {
				newnode.data.add(new NonLeafElement(Integer.parseInt(scan.next()),null));
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
			print.print("N "+this.data.size()+" ");
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
			print.print("L "+node.ldata.size()+" ");
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
	public static boolean isNodeCanBring(NonLeafNode cur,NonLeafNode other) {
		if(other.data.size()-1>=(NonLeafNode.capacity-1)/2&&cur.parentNode==other.parentNode)return true;
		else return false;
	}
	public static boolean isUnderflow(NonLeafNode node) {
		if((NonLeafNode.capacity-1)/2>node.data.size())return true;
		else return false;
	}
}
