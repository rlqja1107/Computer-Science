import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
public class bptree {
	static int count=0;
	static int data_count=0;
	static String dataname="";
	private static void readDeleteCsv(String deletefile){
		try {
			BufferedReader readstream=Files.newBufferedReader(Paths.get(deletefile));
			String line="";

			while((line=readstream.readLine())!=null) {
				String []token=line.split(",",-1);
				LeafNode.delete(Integer.parseInt(token[0]));
				data_count++;
			}
			readstream.close();
		}
		catch(Exception e) {
			System.out.println("Error");
		}
	}
	private static void readInputCsv(String input_file_name){

		try {
			BufferedReader readstream=Files.newBufferedReader(Paths.get(input_file_name));
			String line="";
			while((line=readstream.readLine())!=null) {
				String []token=line.split(",",-1);
				LeafNode.insert(Integer.parseInt(token[0]),Integer.parseInt(token[1]));
				data_count++;
			}
			readstream.close();
		}
		catch(Exception e) {
			System.out.println("Error");
		}
	}
	public static void create(String filename,int size) {
		try {
			PrintWriter pwriter=new PrintWriter(new BufferedOutputStream(new FileOutputStream(filename)));
			pwriter.print(size+"\n");
			System.out.println("Create Success");
			pwriter.close();
		}
		catch(Exception e) {
			System.out.println("Error");
		}
	}
	public static void save(String filename) {
		try {
			NonLeafNode root=NonLeafNode.rootNode;
			PrintWriter pw=new PrintWriter(new BufferedOutputStream(new FileOutputStream(filename)));
			pw.print(NonLeafNode.capacity+"\n");
			NonLeafNode first=root.data.get(0).leftNode;
			while(root!=null) {
				if(root.getClass().getName().equals("NonLeafNode")) {
					first=root.data.get(0).leftNode;
				}
				root.print(pw);
				if(root==first) {
					break;
				}
				else root=first;
			}
			pw.close();
		}
		catch(Exception e) {
			System.out.println("Error");
		}
	}
	public static void printAll() {
		LeafNode node=LeafNode.first;
		NonLeafNode parent=node.parentNode;
		System.out.println("Show All the Node");
		while(node!=null) {
			for(Element i:node.ldata) {
				System.out.print("Leaf : "+i.getKey()+" ");
			}
			System.out.println();
			node=(LeafNode)node.nextNode;
			if(node==null) {
				System.out.println("ParentNode Start");
				NonLeafNode first=null;
				boolean flag=true;
				while(parent!=null) {
					if(flag) {
						first=parent;
						flag=false;
					}
					System.out.println("NonLeafNode : ");
					for(NonLeafElement i:parent.data) {
						System.out.print(i.getKey()+" ");
					}
					System.out.println();
					parent=parent.nextNode;
					if(parent==null) {
						parent=first.parentNode;
						System.out.println("----------------Next Paraent Node----------------");
						flag=true;
					}
				}
			}
		}
		System.out.println("----------------RootNode---------------- ");
		for(int i=0;i<NonLeafNode.rootNode.data.size();i++) {
			System.out.print(NonLeafNode.rootNode.data.get(i).getKey()+" ");
		}
	}

	private static void read(String filename){
		NonLeafNode root=null;
		try {
			String path=System.getProperty("user.dir");
			Scanner scan=new Scanner(new BufferedInputStream(new FileInputStream(filename)));
			int capacity=Integer.parseInt(scan.next());
			int node_size;
			if(scan.hasNext()) {
				//leaf인지 nonleafNode인지, leafnode만 있는 경우도 있음
				String mode=scan.next();
				if(mode.equals("N")) {
					node_size=Integer.parseInt(scan.next());
					root=new NonLeafNode(capacity,true);
					for(int i=0;i<node_size;i++) {
						root.data.add(new NonLeafElement(Integer.parseInt(scan.next()),null));
					}		
					ArrayList<NonLeafNode> list=new ArrayList<NonLeafNode>();
					NonLeafNode.read(scan,root,null, 0, node_size,list,false,null);
					ArrayList<NonLeafNode> list2;
					NonLeafNode temp_node=null;
					boolean first=true;
					NonLeafNode temp=null;
					tree:
						while(true){
							list2=new ArrayList<NonLeafNode>();
							for(NonLeafNode i:list) {
								if(first) {
									node_size=i.data.size();
									temp_node=NonLeafNode.read(scan, i, null, 0, node_size,list2,false,null);
									if(temp_node==null) {
										list2=list;
										break tree; }
									first=false;
								}
								else {
									node_size=i.data.size();
									temp=NonLeafNode.read(scan, i, null, 0, node_size,list2,true,temp_node);
									if(temp==null) 
										break tree;
									else temp_node=temp;
								}
							}
							first=true;
							list=list2;
						}
					LeafNode leaf=null;
					first=true;

					for(NonLeafNode i:list2) {
						node_size=i.data.size();
						if(first) {
							leaf=LeafNode.read(scan, 0, node_size,i,null,true,false,null);
							first=false;
						}
						else {
							LeafNode ln=LeafNode.read(scan, 0, node_size,i,null,false,true,leaf);
							leaf=ln;
						}
					}
				}
				else {
					int leaf_size=Integer.parseInt(scan.next());
					LeafNode newnode=new LeafNode(capacity);
					NonLeafNode rootNode=new NonLeafNode(capacity,true);
					newnode.parentNode=rootNode;
					for(int i=0;i<leaf_size;i++) {
						newnode.ldata.add(new Element(Integer.parseInt(scan.next()),Integer.parseInt(scan.next())));
					}
					LeafNode.first=newnode;
					while(scan.hasNext()) {
						scan.next();
						leaf_size=Integer.parseInt(scan.next());
						LeafNode node=new LeafNode(capacity);
						for(int i=0;i<leaf_size;i++) {
							node.ldata.add(new Element(Integer.parseInt(scan.next()),Integer.parseInt(scan.next())));
						}
						newnode.nextNode=node;
						node.previousNode=newnode;
						newnode=node;
					}
				}
			}
			else {
				NonLeafNode parent=new NonLeafNode(capacity,true);
				LeafNode newnode=new LeafNode(capacity);
				NonLeafNode.rootNode=parent;
				newnode.parentNode=parent;
				LeafNode.first=newnode;
			}
			scan.close();
		}
		catch(Exception e) {
			System.out.println("Error");
		}
	}
	public static  void main(String []args) {
		if(args.length>0) {
			switch(args[0]) {
			case "-c":
				dataname=args[1];
				try {
					int size=Integer.parseInt(args[2]);
					create(dataname,size);
				}
				catch(Exception e) {
					System.out.println("Error");
				}
				break;
			case "-p":
				dataname=args[1];
				try {
					read(dataname);
					printAll();
				}
				catch(Exception e) {
					System.out.println("출력 오류");
				}
				break;
			case "-i":
				dataname=args[1]; 
				String inputname=args[2];
				try {
					read(dataname);
					readInputCsv(inputname);
					save(dataname);
					System.out.println("중복 키 갯수 : "+bptree.count+" , 전체 삽입 데이터 갯수 : "+bptree.data_count);
				}
				catch(Exception e) {
					System.out.println("Error");
				}
				break;
			case "-r":
				dataname=args[1];
				int start=Integer.parseInt(args[2]);
				int end=Integer.parseInt(args[3]);
				try {
					read(dataname);
					NonLeafNode.rangeSearch(start, end, NonLeafNode.rootNode);
				}
				catch(Exception e) {
					System.out.println("Error");
				}
				break;
			case "-s":
				dataname=args[1];
				try {
					read(dataname);
					NonLeafNode.search_singlekey(Integer.parseInt(args[2]), NonLeafNode.rootNode);
				}
				catch(Exception e) {
					System.out.println("Error");
				}
				break;
			case "-d":
				dataname=args[1];
				String delete_file=args[2];
				try {
					read(dataname);
					readDeleteCsv(delete_file);
					save(dataname);
					System.out.println("지워진 key 갯수 : "+bptree.count+" , "+"삭제 전체 데이터 갯수 : "+bptree.data_count);
				}
				catch(Exception e) {
					System.out.println("Error");
				}
				break;
			default :
				System.out.println("다시 입력해주세요.");
				break;
			}
		}
	}
}
