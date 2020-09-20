import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
public class bptree {
	private static void readDeleteCsv(String deletefile){
		try {
		String path=System.getProperty("user.dir");
			BufferedReader readstream=Files.newBufferedReader(Paths.get(deletefile));
			String line="";
			
			while((line=readstream.readLine())!=null) {
				String []token=line.split(",",-1);
				LeafNode.delete(Integer.parseInt(token[0]));
			}
			readstream.close();
		}
		catch(Exception e) {
			System.out.println("Error");
		}
		
		
	}
	private static void readInputCsv(String input_file_name){
	
		try {
		String path=System.getProperty("user.dir");
			BufferedReader readstream=Files.newBufferedReader(Paths.get(input_file_name));
			String line="";
			while((line=readstream.readLine())!=null) {
				String []token=line.split(",",-1);
				LeafNode.insert(Integer.parseInt(token[0]),Integer.parseInt(token[1]));
			}
			readstream.close();
		}
		catch(Exception e) {
			System.out.println("Error");
		}
		
		
	}

	private static void create(String filename,int size) {
		try {
			String path=System.getProperty("user.dir");
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
		String path=System.getProperty("user.dir");
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
				else {
					root=first;
				}
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
	
	private static NonLeafNode read(String filename){
		NonLeafNode root=null;
		try {
			String path=System.getProperty("user.dir");
			Scanner scan=new Scanner(new BufferedInputStream(new FileInputStream(filename)));
			int capacity=Integer.parseInt(scan.next());
			int node_size;
			if(scan.hasNext()) {
				//leaf인지 nonleafNode인지, leafnode만 있는 경우도 있음
				String mode=scan.next();
				if(mode.equals("NonLeaf")) {
					node_size=Integer.parseInt(scan.next());
					root=new NonLeafNode(capacity,true);
					for(int i=0;i<node_size;i++) {
						root.data.add(new NonLeafElement(Integer.parseInt(scan.next()),null));
					}		
					ArrayList<NonLeafNode> list=new ArrayList<NonLeafNode>();
					NonLeafNode.read(scan,root,null, 0, node_size,list,false,null);
					ArrayList<NonLeafNode> list2=new ArrayList<NonLeafNode>();
					NonLeafNode temp_node=null;
					boolean first=true;
					NonLeafNode temp=null;
					tree:
						while(true){
							list2.clear();
							for(NonLeafNode i:list) {
								if(first) {
									node_size=i.data.size();
									temp_node=NonLeafNode.read(scan, i, null, 0, node_size,list2,false,null);
									if(temp_node==null) {
										list2=list;
										break tree;
									}
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
			//root node 생성

			scan.close();
			
			
		}
		catch(Exception e) {
			System.out.println("Error");
		}
		return root;
		
	}
	
	public static  void main(String []args) {
		NonLeafNode root=null;
		String dataname;
	
		if(args.length>0) {
			switch(args[0]) {
			//파일생성
			case "-c":
				String filename=args[1];
				int size=Integer.parseInt(args[2]);
				try {
				create(filename,size);
				}
				catch(Exception e) {
					
					System.out.println("Error");
				}
				
				break;
			case "-p":
				String file=args[1];
				try {
					read(file);
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
					root=read(dataname);
					readInputCsv(inputname);
					save(dataname);
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
					root=read(dataname);
					NonLeafNode.rangeSearch(start, end, root);
				}
				catch(Exception e) {
					System.out.println("Error");
				}
				
				break;
			case "-s":
				dataname=args[1];
				try {
					root=read(dataname);
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
				System.out.println("Good");
				readDeleteCsv(delete_file);
				save(dataname);
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
