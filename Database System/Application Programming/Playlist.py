#!/usr/bin/env python
# coding: utf-8

# In[6]:


#import import_ipynb
import SQL_Query as query
import datetime
import Search_Music as sm
cursor_playlist=None
playlist=None
user_age=None
playlist_payment=None
is_next=None

# In[24]:




# In[22]:


class switch_playlist:
    def __init__(self,number,Id,connection,playlist,cursor):
        self.cursor=cursor
        self.connection=connection
        self.func_name="case_"+str(number).upper() if hasattr(self,"case_"+str(number)) else "case_default"
        self.id=Id
        self.playlist=playlist
        self.number=number
        self.case=getattr(self,self.func_name)
        return self.case()
    def case_D(self):
        delete_playlist(self.id,self.connection,self.cursor)
    def case_Q(self):
        print()
        global is_next
        is_next=4
        
    #플레이리스트 추가
    def case_0(self):
        make_playlist(self.id,self.connection,cursor_playlist)
        global is_next
        is_next=3
        
    def case_default(self):
        global is_next
        if int(self.number)>len(self.playlist) or int(self.number)<=0:
            print()
            print("다른 번호를 입력해주세요. ")
        else:
                show_detail(int(self.number),self.id,self.playlist,self.connection)
        is_next=2
        

def delete_playlist(Id,connection,cursor):     
    count=cursor.execute(query.show_playlist_name,(Id))
    playlist=cursor.fetchall()
    if count ==0:
        print('플레이리스트가 없습니다.')
        return
    print("-----------------플레이리스트 목록-----------------")
    for i in range(0,len(playlist)):
        print(str(i+1)+". "+playlist[i][0])  
    print()
    num=input("삭제할 Playlist 번호 선택")
    if num.isalpha():
        print()
        print('번호 입력바랍니다.')
    elif int(num)>=1 and int(num)<=len(playlist):       
        cursor.execute(query.delete_playlist,(playlist[int(num)-1][0],Id))
        connection.commit()
        cursor.execute(query.delete_playlist_name,(playlist[int(num)-1][0],Id))
        connection.commit()
        print()
        print('삭제되었습니다.')
    else:
        print('범위 안의 숫자를 입력바랍니다.')
        delete_playlist(Id,connection,cursor)

def make_playlist(Id,connection,cursor):
    while True:
        print()
        name=input("Playlist 이름을 만들어주세요. \n종료 : Q \n Input : ").upper()
        if name == 'Q':      
            break
        cursor.execute(query.is_playlist_name_exist,(name,Id))
        result=cursor.fetchone()
        
        if result is None:
            cursor.execute(query.insert_playlist_name,(name,Id))
            connection.commit()
            print("성공적으로 "+name+" Playlist를 생성했습니다.")
            break
        else:
            print("해당 Playlist 이름이 존재합니다.")


# In[7]:


#플레이리스트 나오고 노래 상세 정보 얻기
def show_detail(number,Id,playlist,connection):
    print()
    print("------------"+playlist[number-1][0]+"의 플레이리스트 음원 목록------------------")
    count=cursor_playlist.execute(query.show_playlist_muisc,(playlist[number-1][0],Id))
    result=cursor_playlist.fetchall()
    if count==0:
        print()
        print("현재 플레이리스트에 노래가 없습니다.")
        return
    print()
    for i in range(0,len(result)):
        is_over='(19)' if result[i][2]==1 else ''
        is_group=result[i][7]+"(그룹)" if result[i][5]==1 else result[i][6]
        print(str(i+1)+". "+result[i][1]+is_over+" - "+is_group)
    while True:
        print()
        user_input=input("음원 선택(번호) \n 뒤로가기 : Q\n 가장 많이 듣는 10개 list 보기 : 0 \n Input : ")
        if user_input.upper() =='Q':
            break
        elif user_input == '0':
            show_ranking_top10(Id,playlist[number-1][0],connection)
        elif int(user_input)>=1 and int(user_input)<=len(result):   
            show_music_detail_(result[int(user_input)-1][1],result[int(user_input)-1][3],Id,playlist[number-1][0],connection)
        else:
            print("다른 번호 입력바랍니다")


# In[16]:


def show_ranking_top10(user_id,playlist_name,connection):
    cursor_playlist.execute(query.get_ranking_top10,(user_id,playlist_name))
    result=cursor_playlist.fetchall()
    if result is None:
            print("현재 노래가 없습니다.")
            return
    print()
    print("-----------------Playlist 중 가장 많이 듣는 Top 10------------------------")
    for i in range(0,len(result)):
        is_over='(19)' if result[i][1]==1 else ''
        is_group=result[i][5]+"(그룹)" if result[i][3]==1 else result[i][4]
        print(str(i+1)+". "+result[i][0]+is_over+" - "+is_group)
       
    
    while True:
        print()
        user_input=input("음원 듣기(번호) \n 뒤로가기 :Q \n Input : ")
        if user_input.upper() =='Q':
            break
        elif int(user_input)>=1 and int(user_input)<=len(result):
            show_music_detail_(result[int(user_input)-1][0],result[int(user_input)-1][2],user_id,playlist_name,connection)
        else:
            print("범위 안의 숫자를 입력하세요")


# In[15]:


def show_music_detail_(music_title,sing_time,Id,playlist_name,connection):
    count=cursor_playlist.execute(query.show_music_detail,(music_title,sing_time))
    result=cursor_playlist.fetchall()
    if count == 0:
        return
    is_group=result[0][5]+"(그룹)" if result[0][6]==1 else result[0][4]
    is_over=is_over='(19)' if result[0][2]==1 else ''
    sm.compare_day(playlist_payment)
    print()
    print('음원명 : '+result[0][0]+is_over)
    print('아티스트 : '+is_group)
    print('노래 시간 : '+result[0][1])
    print('장르 : '+result[0][3])
    count=cursor_playlist.execute(query.show_music_compose,(music_title,sing_time))
    result=cursor_playlist.fetchall()       
    for i in range(0,len(result)):
        composer='정보 없음' if result[i][2]=='x' else result[i][2]
        compose_type='No type' if result[i][4]=='x' else result[i][4]
        print(compose_type+" : "+ composer)     
    #노래 들은 것으로 생각
    increase_streaming(music_title,sing_time,Id,playlist_name,connection)
    sm.show_statistics_ratio(cursor_playlist,music_title,sing_time)
    print("노래 횟수 증가")



# In[23]:


def increase_streaming(music_title,music_sing_time,Id,playlist_name,connection):
    cursor_playlist.execute(query.plus_today_soundtrack,(music_title,music_sing_time))
    connection.commit()
    cursor_playlist.execute(query.plus_own_soundtrack,(Id,music_title,music_sing_time,playlist_name))
    connection.commit()
    if user_age<10:
        cursor_playlist.execute(query.plus_play_count_0,(music_title,music_sing_time))
    elif user_age>=10 and user_age<20:
        cursor_playlist.execute(query.plus_play_count_10,(music_title,music_sing_time))   
    elif user_age>=20 and user_age<30:
        cursor_playlist.execute(query.plus_play_count_20,(music_title,music_sing_time))
    elif user_age>=30 and user_age<40:
        cursor_playlist.execute(query.plus_play_count_30,(music_title,music_sing_time))
    elif user_age>=40 and user_age<50:
        cursor_playlist.execute(query.plus_play_count_40,(music_title,music_sing_time))
    else:
        cursor_playlist.execute(query.plus_play_count_50,(music_title,music_sing_time))
    connection.commit()

def show_playlist(cursor,Id,age,user_payment,connection):
        global user_age
        user_age=age
        global cursor_playlist
        cursor_playlist=cursor
        global playlist_payment
        playlist_payment=user_payment
        cursor.execute(query.show_playlist_name,(Id))
        playlist=cursor.fetchall()
        print("-----------------플레이리스트 목록-----------------")
        for i in range(0,len(playlist)):
            print(str(i+1)+". "+playlist[i][0])
        global is_next
        is_next=3
        print()
        number=input("D. 플레이리스트 삭제\n0. 플레이리스트 추가\nQ. 뒤로가기\n 플레이리스트 목록 보기(번호 입력)\n Input : ").upper()
        number=number.upper()
        switch_playlist(number,Id,connection,playlist,cursor)
       
        
        if is_next ==3 or is_next==2:
            show_playlist(cursor,Id,age,user_payment,connection)
        else :
            return
        
