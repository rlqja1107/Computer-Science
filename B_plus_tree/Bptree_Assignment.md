# B+tree Implementation  
## Algorithm Summary   
### Create  
**Create** file 할때는 노드의 **size**만을 저장시키고 **read** file을 할 때 size를 읽어들여 알맞게 구조를 형성시키도록했다.  
### Read  
**read** file은 삽입, 삭제, 검색시 계속 이용하게 되는데, **index.dat**에 저장시킬 때는 다음의 구조를 이용하여 저장시키고 불러오게 했다. 각 줄을 설명하면, `Leaf 3 1 3 2 123 6 1234`와 같이 저장되어있는데 **Leaf**는 NonLeaf인지 Leaf인지 구별시키고, 다음 **3**은 노드의 저장되어있는 size를 의미한다. 이후에는 **key,value** 순서로 저장되게 했다.  
NonLeaf의 **관계 연결**은 parent의 key size만큼 index.dat의 줄을 읽어들인다. e.x) key size가 3이면 다음 아래 3줄이 해당 줄의 child node를 담은 줄이다. 3줄을 읽을 때 **ArrayList**에 NonLeafNode 포인터 저장시켜 해당 노드들의 size만큼 아래의 줄을 읽어들여 관계를 형성해준다.(next node and parent node)   
### Insert  
**Insert**시에 해당 key범위에 들어있는 LeafNode를 찾아가게 한다. 찾아갈 때는 Root노드부터 아래 방향으로 **child** node의 key들을 비교하여 빠르게 찾아가게 했다. Leaf Node를 찾았으면 오름차순에 맞게 Key를 넣는다. 만약 Leaf 노드가 **overflow**시에는 반으로 나누어서 **split**시켰다. split을 시켜도 자신 노드의 sibling 노드는 어떤 노드인지 유지시킨다. 원래는 key가 서로를 가리키게 해야되지만, 구현시에는 key를 서로 가리키게 안하고 sibling 노드가 어떤 것인지를 가리키게 했다. 비교적으로 큰 key가 들어있는 node에서 가장 작은 key를 parent node에 key만을 넣는다. 이때도 parent node는 **오름차순**을 유지하게 한다. 만약 parent node(**NonLeafNode**)도 overflow되면 leaf node와 같은 방식으로 **split**하게 한다. 대신 다른 점은 leaf node는 가장 작은 key를 삭제하지 않고 key 값을 parent에 올리지만 NonLeaf node는 한 노드가 나누어졌을 때 비교적 큰 노드에서 가장 작은 key를 삭제하고 parent로 key를 올려보낸다. 이때 가장 level이 높은 NonLeafNode를 **Root Node**라고 한다. 코드에서 **static** 변수를 이용하여 root node의 포인터를 저장시켰다.  
### Delete  
**Delete**시에는 **Insert**할때와 같이 key가 포함되는 LeafNode를 Root 노드를 통해 찾아간다. 해당 키가 존재하면 삭제한다. 여기서 중요한 것은 노드의 크기는 n/2이상 n-1(n은 capacity)이하로 유지시켜야한다. 삭제 시 underflow가 된다면 sibling 노드로부터 가져올 수 있으면 key를 가져온다. 즉, sibling 노드가 underflow되지 않게 유지시킨다는 뜻이다. 만약 가져올 수 없으면 NonLeaf의 경우 **부모**노드로부터 key값을 가져온다. key를 가져와서 **underflow**가 되지 않게 한다. 대신 sibling 노드에서 **가장 작은** key를 부모로 올리게 된다. **Root**노드의 key를 가져갔을 때, size가 0개가 되면, root의 아래에 있는 노드를 서로 합쳐 Root node로 만들게 한다. Leaf node의 경우 silbing node와 **merge**한다. 합쳐지게 되었을 때, overflow가 되지 않는 sibling node가 있으면 합친다. Merge하게 되면 parent 노드의 size가 줄어들게 되는데 **underflow**가 되었을 때 이전에 설명한 방식을 작동하게 한다.  
### Single Key Search and Range Search  
key**하나**를 찾을 때는 Root에서부터 아래 방향으로 내려간다. 찾으려는 key보다 더 큰 key를 발견하면 해당 key의 왼쪽 노드로 찾아가고 찾으려는 key가 해당 노드에서 가장 크면 가장 오른쪽의 노드로 내려간다. 이렇게 찾아가면 key가 존재할 가능성이 큰 leaf node에 찾아가게 되는데, 여기서 for문으로 일일이 찾으면 된다.  
**Range** search의 경우 start key로 **single key search**하고 해당 노드의 start key부터 시작해서 end key가 나올 때까지 **sibling** node로 넘어가면서 찾으면 된다. sibling노드들은 서로 연결되어있기 때문에 찾아가는데 어렵지 않다.    
## Detailed description of your codes (for each function)  
### bptree.class  
#### private static void readDeleteCsv(String deletefile)
**BufferedReader**를 이용하여 **deletefile**에 있는 key를 B plus tree에서 삭제한다. delete 함수는 이후에 설명할 것이다.  
#### private static void readInputCsv(String input_file_name)  
**BufferedReader**를 이용하여 **input_file_name**에 있는 key와 value를 B plus tree에 넣는다. Insert 함수는 이후에 설명 할것이다.  
#### private static void create(String filename,int size)  
**PrintWriter**를 이용하여 **filename**에 해당하는 파일을 생성한다. `bptree -c index.dat size` 명령어에서 사용되는 함수다. 이때 size만을 index.dat에서 생성해준다.  
#### public static void save(String filename)  
**filename**에 해당하는 파일에 tree의 내용을 저장하는 함수다. 저장할 떄는 Root에서부터 시작한다. 해당 level 층의 가장 왼쪽 노드부터 옆 노드로 옮겨가면서 node의 key를 기록한다. 맨 오른쪽 노드까지 기록한다. 이 기능을 수행하는 코드가 `root.print`를 통해 가장 왼쪽 노드부터 가장 오른쪽 노드까지 기록한다. 이 코드는 NonLeafNode class에서 설명할 것이다.  
``` Java  
if(root.getClass().getName().equals("NonLeafNode")) {
				first=root.data.get(0).leftNode;
				}
				root.print(pw);
```    
내부 코드 중 일부다. first에는 해당 level노드의 childe 노드 중 가장 왼쪽에 있는 노드를 가리킨다. 만약 leaf노드에 도달하면 first는 이전 root와 같은 노드를 가리키게 될 것이다. 따라서 같은 노드가 된다면 break를 하여 while를 빠져나오게 했다. 만약 아니면 root를 first로 가리키게하여 다시 그 level의 노드를 왼쪽 노드부터 가장 오른쪽 노드까지 출력하게 한다.  
#### public static void printAll()  
이는 추가적인 기능을 했다. 만약 B+tree의 모든 노드와 key와 구성을 알고 싶으면 수행하게 했다. Leaf노드 먼저 모두 출력하게 했는데, 이는 `node=(LeafNode)node.nextNode`를 이용했다. 만약 다음 노드가 없으면(node==null) NonLeafNode를 출력하게 했다. flag를 둬서 NonLeafNode중에서 구조상 가장 왼쪽이고 가장 아래에 있는 노드부터 출력하게 시작했다. `parent=parent.nextNode`를 이용하여 오른쪽 sibling 노드로 넘어가게 했다. parent 변수에 가장 왼쪽에 있던 노드의 parent를 지정하게 하여 다시 다음 level의 노드를 왼쪽부터 출력하게 했다.  
#### private static NonLeafNode read(String filename)   
**index.dat**에 저장되어있는 정보를 이용하여 B plus node로 만드는 함수다. **Scanner**를 이용하여 한 단어씩 읽어가며 불러왔다. 시작이 **NonLeaf**로 시작하지 않으면(Root node가 없음) LeafNode만 있는 B plus tree이기 때문에 Leaf Node만을 연결했다. 한 줄을 보면 `Leaf 2 1 1023 2 312`의 형식으로 되어있다. 형식 이해는 [Algorithm Summary](##Algorithm-Summary)를 보면 알 수 있다. size만큼 key value를 저장시키고 sibling관계를 다음 코드를 통해 정의했다. 이전 sibling 노드도 알기 위해 previous로 놓아서 지정했다.  
``` Java  
newnode.nextNode=node;
node.previousNode=newnode;
newnode=node;
```  
NonLeaf node가 있는 경우 먼저 **root**노드를 형성시킨다.(index.dat 맨 위에 있는 정보가 Root 노드정보) [NonLeafNode.read()](####public-static-NonLeafNode-read(Scanner-scan,NonLeafNode-root,NonLeafNode-node,int-count,int-root_size,ArrayList<NonLeafNode>-parent_list,boolean-first,NonLeafNode-right))함수를 이용하면 해당 노드의 **Child**를 모두 읽어들이고 관계를 연결시키는 함수다. ArrayList의 list는 해당 level의 NonLeafNode의 포인터를 이용하여 size만큼 index.dat의 줄을 읽어들여 parent-child 관계를 형성하게 한다. list2에는 parent의 child NonLeafNode 포인터 정보를 담아서 다음 for문 때 parent-child관계 형성을 위한 list다. **NonLeafNode.read**의 return은 해당 level의 **가장 오른쪽** 노드를 가리키게 했는데 이는 다른 parent node의 child노드의 **가장 왼쪽**에 있는 노드와 연결시키기 위함이다. **first**라는 flag를 주어서 첫 parent노드의 child 중 가장 오른쪽 노드 포인터를 얻기 위함이다. LeafNode의 줄이 나오면 null를 return하게 하여 while문을 나온다. 이전과 같은 방식으로 LeafNode를 읽어들인다. 이때 list2에는 leaf node 윗 level의 parent node 포인터가 들어있어서 parent-child 관계를 형성시킨다. Root를 먼저 실행시킨 것과 같이 가장 왼쪽의 노드를 먼저 읽어들여 LeafNode의 first노드를 지정시키고 while문을 통해 관계를 형성시킨다.  
 ### NonLeafNode.class 
 #### 필드 변수  
 **int capacity**는 node의 최대 size를 의미한다.  
 **NonLeafNode nextNode**는 오른쪽 sibling node를 가리킨다.   
 **NonLeafNode previousNode**는 왼쪽 sibling node를 가리킨다.  
 **NonLeafNode parentNode**는 부모노드를 가리킨다.  
 **boolean root**는 자신이 root인지 아닌지를 가리킨다.  
 **static NonLeafNode rootNode**는 rootnode의 포인터를 가리킨다.  
 **ArrayList<NonLeafElement> data**는 key, leftnode 관련된 data들을 담은 list다.  
 #### public static NonLeafNode searchKey_Node(int key,NonLeafNode root)  
 이 함수는 key에 해당하는 LeafNode를 찾아가는 함수다. NonLeafNode라면 아래로 계속 찾아가고 LeafNode면 그 node를 return하게 했다. **where** 변수에는 key 범위에 해당하는 child 노드를 가리키게 된다. 해당하는 노드의 key가 검색하려는 key보다 **큰** 경우 그 key의 왼쪽노드를 **where**가 가리키게 한다. 검색하려는 key가 가장 큰 경우 가장 오른쪽 노드를 where가 가리키게 한다. 재귀함수를 이용하여 찾아가게 한다.  
 #### public static void rangeSearch(int start,int end, NonLeafNode root)  
**범위 검색**시에 사용하는 함수다. 위의 함수인 `search_Key_Node()`함수를 이용하여 **start key**에 해당하는 leaf node를 찾은 후 end key보다 큰 key가 나올 때까지 계속검색한다. 다음 노드부터의 검색은 [nextRangeSearch()](####private-static-void-nextRangeSearch(int-start,int-end,LeafNode-node))함수를 이용했다. 이 함수의 설명은 아래와 같다.  
#### private static void nextRangeSearch(int start,int end,LeafNode node)  
만약 end key보다 큰 key가 나온다면 return으로 끝내고, 노드 끝까지 검색했는데도 큰 key가 안나오면 재귀함수를 이용하여 다음 노드를 검색하게 한다.  
#### public static int search_singlekey(int key,NonLeafNode root)  
위의 함수 `searchKey_Node()`함수를 이용하면 더 쉬울 수도 있겠지만, `searchKey_Node`와 비슷한 방식으로 다시 구현했다. 차이점은 검색하다가 LeafNode가 나왔을 때는 for문으로 직접 key를 검색하는 코드가 추가되었다.  
#### public void split()  
NonLeafNode에서 **overflow**가 되어서 **split** 되었을 때 실행되는 함수다. **newnode**가 분열되어 오른쪽으로 새로 생긴 노드를 의미한다. 현재 노드와 parent관계 및 next,previous 관계 형성 후, while문을 통해 절반의 key값들을 옮긴다. 이때 현재 노드의 rightmost child 정보를 새로 생긴 노드의 leftmost key의 leftnode로 연결해준다. 그리고 newnode의 rightmost child를 현재 노드의 rightmost child로 연결해준다. 만약 분열하는 노드가 root노드라면 새로운 root 노드를 형성해준다. 아닐 경우, [inner_insert_key()](####public-<P-extends-NonLeafNode>-void-inner_insert_key(int-key,P-node,P-first))를 통해 parent 노드에 분열된 노드의 leftmost key정보를 넣어준다.  
#### public <P extends NonLeafNode> void inner_insert_key(int key,P node,P first)  
이 함수는 NonLeafNode에 새로운 key를 넣어주는 함수다. 만약 key를 넣었을 때 **overflow**가 되면 다시 **split**을 시켜준다.  
#### public static void merge(NonLeafNode cur)  
이 함수는 **underflow**가 발생하게 된다면 sibling 노드와 **merge**하는 함수다. 먼저 nextnode와 merge했을 때 **overflow**가 발생하지 않고, sibling node라면 하나로 합쳐서 오른쪽 노드로 다 옮겨간다. 옮겨 가고나서 부모 노드 중 원래 nextnode의 leftmost key값을 삭제하게 되는데, size가 0이 되면 nextf를 root node로 지정한다. 그리고 next-previous를 설정해준다.   
**merge**했을 때 overflow가 발생하게 된다면 회전방식을 이용한다. 이는 underflow된 노드가 부모노드의 key를 하나 가져오고, nextnode의 leftmost key를 부모노드에 하나 올린다. [LeafNode.check_keychange](####public-static-void-check_keychange(int-check_key,NonLeafNode-parent,NonLeafNode-del_node))함수를 이용하여 부모 key를 바꿔준다.  
위와 같은 방식이지만 다른 점은 nextNode가 아니라 **previous** node를 통해 merge하는 것이다. merge 했을 때 **overflow**가 되면 회전방식을 이용하고, overflow가 안되면 현재 **underflow**된 노드에 합쳐지게 한다.  
#### public static NonLeafNode read(Scanner scan,NonLeafNode root,NonLeafNode node,int count,int root_size,ArrayList<NonLeafNode> parent_list,boolean first,NonLeafNode right)  
이 함수는 **index.dat**의 정보를 B plus tree 만들 때 사용하는 함수다. 이 함수를 이용하면 가장 왼쪽에 있는 노드부터 가장 오른쪽 노드까지 읽어들이고 **관계**를 형성해주는 함수다. **count** 매개변수를 통해서 지정된 갯수만큼(부모노드 **size+1**) NonLeafNode를 읽어들인다. 재귀함수를 통해서 지정된 갯수만큼 NonLeafNode를 생성시킨다.  
#### public <P extends NonLeafNode>void print(PrintWriter print)  
생성된 B plus tree구조를 **index.dat**에 기록하는 함수다. 먼저 NonLeaf인지 Leaf인지 확인하고 다음에 node의 key size를 적는다. NonLeaf의 경우 key를 나열하면 되고, Leaf의 경우는 key value 순서로 기록한다.  
#### public static boolean isNodeCanMerge(NonLeafNode cur,NonLeafNode other)  
이 함수는 노드가 merge할 수 있는지 확인하는 함수다. size비교와 parent가 같아야 merge를 할 수 있게 했다.  
#### public static boolean isUnderflow(NonLeafNode node)  
이 함수는 **node**가 underflow인지 확인하는 함수다. underflow면 true를 반환한다.  
### LeafNode
#### 필드 변수  
**ArrayList<[Element](###Element)> ldata** : Key와 Value의 정보를 담은 list  
**public static LeafNode first**는 LeafNode 중 가장 왼쪽의 노드를 의미한다. 
#### public static boolean put(int key,Element el, LeafNode leaf)  
이 함수는 LeafNode에 key가 **정렬**되게 들어가는 함수를 의미한다. 만약 중복된 키가 있을시 **return false**를 통해 더 이상 진행이 안되게 한다. 정상적으로 들어갔을 때만 return **true**를 반환하게 한다.  
#### public static void insert(int key,int value)  
**insert**할 때는 [searchKey_node()]( ####public-static-NonLeafNode-searchKey_Node(int-key,NonLeafNode-root))를 이용하여 key에 해당하는 LeafNode로 찾아가게 한다. 만약 **root** 노드의 data가 없으면 leaf노드에  key를 추가시킨다. [overflow()](####public-static-boolean-isOverflow(LeafNode-curr_node))하게 된다면 **split**하게 한다. 여기서는 LeafNode의 split까지 모두 구현시켜놓았다. **newNode**가 분열되어 오른쪽으로 생겨난 노드다. **next-previous** 관계를 형성시킨 후, 부모 노드에도 분열된 노드의 leftmost key를 넣어주어야 하기 때문에 [inner_insert_key()][####public-<P-extends-NonLeafNode>-void-inner_insert_key(int-key,P-node,P-first)]를 이용한다.  
#### private static int delete_in_node(int key,LeafNode node)  
이 함수는 node에서 해당 key를 삭제해주는 함수다. 삭제 후, 만약 부모노드의 key가 바뀌어야 한다면 [change_split_key](#### public-static-void-check_keychange(int-check_key,NonLeafNode-parent,NonLeafNode-del_node))를 이용한다. 만약 없을 경우에는 **Integer.MAX_VALUE**를 반환하여 없는 것을 알려준다.  
#### public static void delete(int key)  
우선 key가 포함된 Leaf Node를 [searchKey_Node](####-public-static-NonLeafNode-searchKey_Node(int-key,NonLeafNode-root)) 이용하여 찾아간다. [delete_in_node()](####private-static-int-delete_in_node(int-key,LeafNode-node))를 이용하여 key를 LeafNode에서 삭제시켜준다. 삭제했을 때 **underflow**가 발생하면 next node가 underflow 발생하지 않는다면 next node에서 하나의 key를 가져온다. previous node도 마찬가지다. 단, next이든 previous node이든 parent가 같아야 한다. 만약 못가져올 경우는 [merge()](####public-static-void-merge(LeafNode-previous,LeafNode-next,LeafNode-cur,int-delete_value))를 이용하여 합치게 한다.  
**underflow**가 발생하지 않더라도 노드의 leftmost key가 삭제될 수도 있기 때문에 [check_keychange()](####public-static-void-check_keychange(int-check_key,NonLeafNode-parent,NonLeafNode-del_node))을 통해 확인하고 parent 노드의 key를 바꿔준다.
#### public static boolean isOverflow(LeafNode curr_node) 
해당 노드가 overflow인지 확인하는 함수다. 만약 node 데이터 size가 **capacity**-1보다 커지게 된다면 **true**를 반환하게 한다.  
#### public static void merge(LeafNode previous,LeafNode next,LeafNode cur,int delete_value)  
합치려고 할떄는 먼저 nextnode를 확인한다. [isCanMerge](####private-static-boolean-isCanMerge(LeafNode-cur,LeafNode-other))으로 merge할 수 있는지 확인한다. 합칠 수 있다면 next 노드에 모두 합치고, 합치기 전의 nextnode의 leftmost key를 부모노드에 반영해주어야 한다. **remove_key**에 key 정보가 담겨있다. 부모노드에서 지우려고 할때는 [remove_split_key](####public-static-void-remove_split_key(int-first_key,int-remove_key,NonLeafNode-parent))를 이용하여 부모노드에서 지워준다.  
next node에서 합쳐질 수 없다면 previous node에서도 확인하고 합친다.  
#### public static void remove_split_key(int first_key,int remove_key,NonLeafNode parent)  
이 함수는 **remove_key**와 일치한 부모 노드 key가 있는지 확인하고 있다면 **first_key***와 비교하고 지우는 함수다. 만약 first_key와 다르다면 **이전 key**를 first_key로 바꿔준다. key size가 1이라면 그 즉시 바꿔주도록 한다. 제거한 후, parent가 **underflow**라면 [merge()](####public-static-void-merge(LeafNode-previous,LeafNode-next,LeafNode-cur,int-delete_value))를 통해 부모 노드도 합쳐주도록 한다.
####  public static void check_keychange(int check_key,NonLeafNode parent,NonLeafNode del_node)  
지우려고 했던 node가 부모노드 key의 leftchild node면 check_key와 확인하고 다르다면 check_key로 바꿔주는 함수다. 
#### public static LeafNode read(Scanner scan,int count,int node_size,NonLeafNode parent,NonLeafNode node,boolean first,boolean first_node,LeafNode right)  
이 함수는 count 갯수+1 만큼 새로운 LeafNode를 만들어낸다. 만약 처음 만들어지는 leafnode라면 **LeafNode.first**로 지정하고 next-previous, parent 관계를 형성한다. 재귀함수를 통해 count+1개 만큼 LeafNode를 **index.dat**의 정보를 이용하여 생성하는 함수다.  
#### private static boolean isCanMerge(LeafNode cur,LeafNode other)  
이 함수는 LeafNode 끼리 합치려고 할때 overflow가 되는지 확인하고(capacity)보다 작아야한다.  
#### public static boolean isUnderflow(LeafNode cur_node)  
이 함수는 node가 underflow인지 확인하는 함수다. n-1/2보다 data size가 작아지게 된다면 **true**를  return 한다.  
#### public static boolean isOverflow(LeafNode curr_node)   
이 함수는 node의 data size가 n-1보다 커지게 된다면 **true**를 return 하는 함수다.   
### Element.class  
이 클래스는 LeafNode에 저장되는 key와 value를 묶어주는 클래스다. 필드로 key와 value가 존재한다.  
### NonLeafElement.class  
이 클래스는 NonLeafNode에 들어가는 key정보와 관련된 정보를 담은 클래스다. 필드로는 key와 left노드가 존재한다. leftnode는 아래 그림과 같다.  
![image](picture.png)
## Instructions for compiling source  
1. bptree.exe를 다운받고 input.csv와 delete.csv(insert할 파일과 delete할 파일)을 같은 위치에 둔다.  
   **주의**: inpust.csv는 첫 열에는 key가 담겨 있고 두 번째 열에는 value가 들어가있어야 한다.  
   delete.csv에는 한 열에 지울 key가 담겨있어야 한다. 
![image](first.png)
2. cmd에서 해당 폴더 위치를 찾아간다.  
![image](second.png)  
3. command line에 **bptree -c index.dat 5**와 같은 명령어를 넣어서 실행한다.  
* 파일 생성은 bptree -c index.dat 5(5는 사이즈,index.dat는 생성파일 이름)  
![image](third.png)  
* insert할 때, **bptree -i "데이터파일이름" "input할 csv"** 여기서 중복키는 허용되지 않는다는 메세지를 넣었고, 별 다른 메세지가 없으면 성공적으로 **insert**된 것이다.  
 ![image](fourth.png)  
* SingleKey Search 할 때는 **bptree -s "데이터파일이름" "검색할 key"**
![image](fifth.png)  
* RangeSearch 할 때는 **bptree -r "데이터파일이름" "start key" "end key"**
![image](sixth.png)  
* delete할 떄는  **bptree -d "데이터 파일이름" "삭제할 파일.csv"**  
삭제 시 키가 존재하지 않으면 출력되게끔 했다.  
![image](tenth.png)

* 만약 **모든 노드**들을 leafnode에서부터 root까지 다 확인하고 싶다면 **bptree -p "데이터파일이름"**하면
![image](eight.png)
* 아직까지 **오류**를 발견하지 못했지만, 오류가 생긴다면 index.dat 파일에 손상이가 가기 때문에 **create**부터 다시해서 **insert**를 해야한다.  

