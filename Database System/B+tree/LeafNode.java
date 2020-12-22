import java.util.*;
public class LeafNode extends NonLeafNode{
	ArrayList<Element> ldata;
	public static LeafNode first;
	//flag가 false면 non_leaf를 만들고, true면 안만들고 non-leaf생성 x
	public LeafNode(int capacity){
		LeafNode.capacity=capacity;
		ldata=new ArrayList<Element>();
		nextNode=null;
	}
	//leafnode에 데이터 넣기
	public static boolean put(int key,Element el, LeafNode leaf) {
		int size=leaf.ldata.size();
		if(size!=0) {
			for(int i=0;i<size;i++) {
				if(key==leaf.ldata.get(i).getKey()) {
					bptree.count++;
					return false;
				}
				else if(key<leaf.ldata.get(i).getKey()) {
					leaf.ldata.add(i,el);
					return true;
				}
				else if(i==size-1) {
					leaf.ldata.add(el);
					return true;
				}
			}
		}
		else leaf.ldata.add(el);
		
		return true;
	}
	public static void insert(int key,int value) {
		//찾을 때는 search이용
		LeafNode leaf;
		if(NonLeafNode.rootNode.data.size()==0) 
			leaf=LeafNode.first;
		
		else 
			leaf=(LeafNode)searchKey_Node(key,NonLeafNode.rootNode);
		
		//아직 현재 노드가 다 안채워져있을 경우
		Element new_element=new Element(key,value);
		if(!put(key,new_element,leaf)) return;	

		//노드가 다채워져서 split이 일어나야 할 경우
		if(isOverflow(leaf)) {
			int middle=leaf.ldata.size()/2;
			LeafNode newNode=new LeafNode(LeafNode.capacity);
			//새로운 leaf node의 parentNode 설정
			newNode.parentNode=leaf.parentNode;
			int count=0;
			int remove_size=leaf.ldata.size()-middle;
			//현재 노드에서 새로 노드를 만든 후, 옮겨 붙이기
			while(count!=remove_size) {
				newNode.ldata.add(leaf.ldata.remove(middle));
				count++;
			}
			if(leaf.nextNode!=null) {
				newNode.nextNode=leaf.nextNode;	
				leaf.nextNode.previousNode=newNode;
			}
			leaf.nextNode=newNode;
			newNode.previousNode=leaf;
			leaf.parentNode.<LeafNode>inner_insert_key(newNode.ldata.get(0).getKey(),newNode,leaf);
		}
	}
	private static int delete_in_node(int key,LeafNode node) {
		int size=node.ldata.size();
		for(int i=0;i<size;i++) {
			if(node.ldata.get(i).getKey()==key) {
				if(i==0&&size>1) {
					change_split_key(node.ldata.get(0).getKey(),node.ldata.get(1).getKey(),node.parentNode);
				}
				bptree.count++;
				return node.ldata.remove(i).getKey();
			}
		}
		//없으면 정수 최댓값 return
		return Integer.MAX_VALUE;
	}
	public static void delete(int key){
		LeafNode del_node=(LeafNode) NonLeafNode.searchKey_Node(key, NonLeafNode.rootNode);
		int delete_value=delete_in_node(key,del_node);
		
		if(LeafNode.first.ldata.size()==0&&LeafNode.first.parentNode.data.size()==0) {
			bptree.create(bptree.dataname, LeafNode.capacity);
		}
		
		if(delete_value!=Integer.MAX_VALUE) {
			if(isUnderflow(del_node)&&del_node.parentNode!=null) {
				LeafNode next=((LeafNode)del_node.nextNode);
				LeafNode previous=((LeafNode)del_node.previousNode);
				//nextnode가 underflow 발생하지 않고 sibling인 경우
				if(del_node.nextNode!=null&&next.ldata.size()-1>=LeafNode.capacity/2&&del_node.parentNode==next.parentNode) {
					del_node.ldata.add(next.ldata.remove(0));
					//빠져나간 key로 split_key가 바뀔경우
					check_keychange(next.ldata.get(0).getKey(),next.parentNode,next);
					//혹시 들어온 key로 인해 split_key가 바뀔경우
					check_keychange(del_node.ldata.get(0).getKey(),del_node.parentNode,del_node);	
				}
				//previous node가 underflow 발생하지 않을 때
				else if(del_node.previousNode!=null&&previous.ldata.size()-1>=LeafNode.capacity/2&&del_node.parentNode==previous.parentNode) {
					del_node.ldata.add(0,previous.ldata.remove(previous.ldata.size()-1));
					check_keychange(previous.ldata.get(0).getKey(),previous.parentNode,previous);
					check_keychange(del_node.ldata.get(0).getKey(),del_node.parentNode,del_node);
				}
				else {
					merge(previous,next,del_node,delete_value);
				}
			}
			
			}
		}
	public static void check_keychange(int check_key,NonLeafNode parent,NonLeafNode del_node) {
		int size=parent.data.size();
		for(int i=0;i<size;i++) {
			if(parent.data.get(i).leftNode==del_node) {
				if(i!=0&&parent.data.get(i-1).getKey()!=check_key) 
					parent.data.get(i-1).setKey(check_key);
				break;
			}
			else if(i==size-1) {
				if(parent.data.get(size-1).getKey()!=check_key)
					parent.data.get(size-1).setKey(check_key);
			}
		}
	}
	public static void merge(LeafNode previous,LeafNode next,LeafNode cur,int delete_value) {
		//nextNode가 있는경우
		if(next!=null&&cur.parentNode==next.parentNode) {
			int remove_key=0;
			if(next.ldata.size()>0) 
				remove_key=next.ldata.get(0).getKey();
			
			else remove_key=delete_value;
			
			while(cur.ldata.size()>0) {
				next.ldata.add(0,cur.ldata.remove(cur.ldata.size()-1));
			}
			if(cur.previousNode!=null) {
				cur.previousNode.nextNode=next;
				next.previousNode=cur.previousNode;
			}
			else LeafNode.first=next;
			remove_split_key(next.ldata.get(0).getKey(),remove_key,next.parentNode);
		}

		//child에서 마지막 노드인경우
		else if(previous!=null&&cur.parentNode==previous.parentNode) {
			int remove_key=0;
			if(cur.ldata.size()>0) {
				remove_key=cur.ldata.get(0).getKey();
			}
			else {
				remove_key=delete_value;
			}
			while(previous.ldata.size()>0) {
				cur.ldata.add(0,previous.ldata.remove(previous.ldata.size()-1));
			}
			if(previous.previousNode!=null) {
				previous.previousNode.nextNode=cur;
				cur.previousNode=previous.previousNode;
			}
			else {
				LeafNode.first=cur;
			}
			remove_split_key(cur.ldata.get(0).getKey(),remove_key,cur.parentNode);
		}
	}

	public static void remove_split_key(int first_key,int remove_key,NonLeafNode parent) {
		int size=parent.data.size();
		for(int i=0;i<size;i++) {
			if(remove_key==parent.data.get(i).getKey()) {
				if(i!=0&&first_key!=parent.data.get(i-1).getKey()) {
					parent.data.get(i-1).setKey(first_key);
					parent.data.remove(i);
				}
				else if(size==1) {
					//parent.data.get(0).setKey(first_key);
					if(parent.root) parent.data.remove(0);
				}
				else {
					parent.data.remove(i);
				}
				break;
			}
		}
		//underflow 발생시 non-leaf에서 merage취함
		if(parent.data.size()<(NonLeafNode.capacity-1)/2) {
			NonLeafNode.merge(parent);
		}
	}

	public static void change_split_key(int remove_key,int change_key,NonLeafNode parent) {
		int size=parent.data.size();
		for(int i=0;i<size;i++) {
			if(remove_key==parent.data.get(i).getKey()) {
				parent.data.get(i).setKey(change_key);
				break;
			}
		}
	}
	public static LeafNode read(Scanner scan,int count,int node_size,NonLeafNode parent,NonLeafNode node,boolean first,boolean first_node,LeafNode right) {
		LeafNode newnode=new LeafNode(NonLeafNode.capacity);
		int size=Integer.parseInt(scan.next());
		for(int i=0;i<size;i++) {
			Element el=new Element(Integer.parseInt(scan.next()),Integer.parseInt(scan.next()));
			newnode.ldata.add(el);
		}
		if(first) 
			LeafNode.first=newnode;
		
		if(first_node) {
			right.nextNode=newnode;
			newnode.previousNode=right;
		}
		newnode.parentNode=parent;

		if(node!=null) {
			node.nextNode=newnode;
			newnode.previousNode=node;
		}
		if(count==node_size) {
			parent.rightNode=newnode;
			if(scan.hasNext()) {
				scan.next();
			}
			return newnode;
		}
		else {
			parent.data.get(count).leftNode=newnode;
			count++;
			scan.next();
			return LeafNode.read(scan, count, node_size, parent, newnode,false,false,null);
		}
	}
	public static boolean isUnderflow(LeafNode cur_node) {
		if((LeafNode.capacity-1)/2>cur_node.ldata.size())return true;
		else return false;
	}
	public static boolean isOverflow(LeafNode curr_node) {
		if(LeafNode.capacity<=curr_node.ldata.size()) return true;
		else return false;
	}	
}
