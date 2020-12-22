# Computer Science Content  
## 운영체제  
### Report  
[Wiki](https://github.com/rlqja1107/Computer-Science/wiki)
## 데이터베이스시스템  
### B+ tree  
* **Code**  
[Code](https://github.com/rlqja1107/DatabaseSystem/tree/master/B%2Btree)  
* Wiki(Report)  
[Report](https://github.com/rlqja1107/DatabaseSystem/wiki/B-tree-Implementation)
### 객체지향 프로그래밍  
* **Code**  
[Code](https://github.com/rlqja1107/Computer-Science/tree/master/Object-Oriented%20Programming)  
각 프로젝트마다의 자세한 설명은 **pdf**로 첨부해놓았다.  
### 컴퓨터 네트워크  
#### Assignment 1  
기본적인 FTP 프로토콜 명령어의 구현. Server와 Client 간의 명령어 교환 및 데이터 교환  
**단**, 이 때는 Data 전송 중에 Packet Loss가 발생하지 않다는 가정 하에서 구현  
**예시**   
* PUT  
* LIST  
* CD  
* GET  

[Code_Report](https://github.com/rlqja1107/Computer-Science/tree/master/Computer-Network/FTP_Principal_Function)  
#### Assignment 2  
Assignment1에서 구현한 내용을 바탕으로 SR Protocol를 자바를 이용하여 구현한다. SR Protocol를 구현하면서 3가지의 상황을 가정하여  
프로그래밍을 했다. CMD에서 보낼 패킷의 n 번째를 고의로 **Event**를 발생시켜 SR Protocol를 구현하게 했다. 예를 들어, **Bit Error d3**을 입력하면  
3번째로 보내는 패킷에 고의로 bit error를 발생시켜 Server에서 정상적으로 받아들이지 않고, 처리를 하게 한다.  
**3가지 상황**  
* **Bit Error**  
Bit Error는 SR Protocol 상황 중 패킷 메세지에 에러가 발생했다. 따라서, Receiver(Server)는 해당 패킷의 메세지 내용을 볼 수 없다. 따라서, Server  에서는 잘 받지 못했다는 ack message를 Client에게 보내어, 재전송을 유도한다.  
* **Timeout**  
Timeout은 SR Protocol 상황 중 ack 메세지가 오지 않아 timeout event를 발생시킨다. 따라서, 다시 Server에게 timeout이 발생한 패킷을 재전송하도록 한다.   
* **Packet Loss**  
Packet Loss는 SR Protocol 상황에서 네트워크 상에서 packet이 사라진 상황을 가정한다. Packet Loss 발생시에 Server도 packet을 받지 못하여 ack message를 보내지 못한다. 따라서, Packet Loss 발생시 timeout을 발생시켜 재전송을 유도하도록 한다.  
SR Protocol 구현시 sequence number, buffer 등의 자세한 문제는 레포트를 참고하면 된다.  
[Report of SR Protocol]()


