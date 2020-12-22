

import SQL_Query as query
import datetime
import Playlist as pl


search_payment=None
cursor_music=None
user_Id=None
is_continue=None
connection_=None
user_age=None
class switch_search_music:
    def __init__(self,number):
        self.func_name='case_'+str(number) if hasattr(self,'case_'+str(number)) else "case_6"
        self.case=getattr(self,self.func_name)
       
        return self.case()    
    #음원명으로 검색
    def case_1(self):
        search_by_music_name()
        global is_continue
        is_continue=True
    #작곡가,편곡가,작사가로 검색
    def case_2(self):
        search_by_composer()
        global is_continue
        is_continue=True
    #아티스트로 검색
    def case_3(self):
        search_by_artist()
        global is_continue
        is_continue=True
    def case_4(self):
        global is_continue
        is_continue=False
    def case_6(self):
        print("다른 번호 입력해주세요")
        global is_continue
        is_continue=True



def user_listen_music(result,index_of_title,index_of_sing_time):
    while True:
        print()
        choose=input("음원 듣기(번호 선택)\n뒤로가기 : Q\n Input : ")
        if choose.upper()=='Q':
            break
        if int(choose) >=1 and int(choose) <=len(result):
            show_music_detail(result[int(choose)-1][index_of_title],result[int(choose)-1][index_of_sing_time],user_Id,cursor_music)
            break
        else:
            print("범위 안의 음원을 선택해주세요.")

def search_by_artist():
    group=input("그룹명으로 찾겠습니까?(Y/N)\n뒤로가기 : Q\n Input : ").upper()
    if group=='Q':
        return
    if group == 'Y':
        group_name=input("그룹명 : ")
        count=cursor_music.execute(query.search_music_by_artist_group,(group_name,1))
        result=cursor_music.fetchall()
        if count ==0:
            print()
            print("해당 음원이 존재하지 않습니다.")
            search_by_artist()
            return
        else:
            print()
            for i in range(0,len(result)):
                is_over='(19)' if result[i][3]==1 else ''
                print(str(i+1)+". "+result[i][0]+is_over+" - "+result[i][2]+" Time : "+result[i][1])
            user_listen_music(result,0,1)   
    else:       
        artist_name=input("아티스트 이름을 입력하세요.\n 뒤로가기 : Q\n Input : ")
        if artist_name.upper()=='Q':
            return
        count=cursor_music.execute(query.search_music_by_artist_name,(artist_name))
        result=cursor_music.fetchall()
        if count ==0:
            print("해당 음원이 존재하지 않습니다.")
            search_by_artist()
            return
        else:
            for i in range(0,len(result)):
                is_over='(19)' if result[i][3]==1 else ''
                print((str(i+1)+". "+result[i][0]+is_over+" - "+result[i][1]+" Time : "+result[i][2]))                      
            user_listen_music(result,0,2)
    search_by_artist()
    





def search_by_composer():
    composer_name=input("작곡가 또는 작사가의 이름을 입력해주세요.\n 뒤로가기 : Q\n Input : ")
    if composer_name.upper()=='Q':
        return
    count=cursor_music.execute(query.search_music_by_composer_name,(composer_name))
    result=cursor_music.fetchall()
    if count==0:
        print()
        print("해당하는 음원이 없습니다.")
        search_by_composer()
        return
    convert_to_music_title(user_Id,result,0,1,2,3,4,5,cursor_music)
    search_by_composer()
    


def search_by_music_name():
    print()
    music_name=input("음원명을 검색해주세요\n뒤로가기 : Q\n Input : ")
    if music_name.upper() =='Q':
        return
    count=cursor_music.execute(query.search_music_by_name,(music_name))
    if count==0:
        print()
        print("해당하는 음원이 없습니다.")
        search_by_music_name()
        return
    result=cursor_music.fetchall()   
    convert_to_music_title(user_Id,result,0,1,2,3,4,5,cursor_music)
    search_by_music_name()
    
    
           
##def func(i,y,z,name,result,title):
##    if y is not 'solo_':
##        print(str(i+1)+". "+result[i][title]+z+" - ("+name+")")
##    else:
##        print((i+1)+". "+result[i][title]+z+" - ("+y+")")
##    




def convert_to_music_title(Id,result,index_of_title,index_of_artist_name,index_of_19,index_of_solo,index_of_sing_time,index_of_is_group,cursor):
    global cursor_music
    cursor_music=cursor
    print()
    for i in range(0,len(result)):
        is_over="(19)" if result[i][index_of_19] ==1 else ""
        is_group=result[i][index_of_solo] if result[i][index_of_is_group]==1 else result[i][index_of_artist_name]
        print(str(i+1)+". "+result[i][index_of_title]+is_over+" - ("+is_group+") - Time : "+result[i][4])     
    user_listen_music(result,index_of_title,index_of_sing_time)
   
def show_music_detail(music_title,music_sing_time,Id,cursor):
    count=cursor.execute(query.show_music_detail,(music_title,music_sing_time))
    result=cursor.fetchall()
    if count == 0:
        print()
        print("음악이 존재하지 않습니다.")
        return
    artist_name=None
    is_group=result[0][5]+"(그룹)" if result[0][6]==1 else result[0][4]
    is_over="(19)" if result[0][2]==1 else ""
    compare_day(search_payment)
    print()
    print('음원명 : '+result[0][0]+is_over)
    print('아티스트 : '+is_group)
    print('노래 시간 : '+result[0][1])
    print('장르 : '+result[0][3])
    count=cursor.execute(query.show_music_compose,(music_title,music_sing_time))
    result=cursor.fetchall()
    for i in range(0,len(result)):       
        composer='정보 없음' if result[i][2]=='x' else result[i][2]
        compose_type='No type' if result[i][4]=='x' else result[i][4]
        print(compose_type+" : "+ composer)
    # 통계자료
    show_statistics_ratio(cursor,music_title,music_sing_time)
        #노래 들은 것으로 생각
    increase_streaming(music_title,music_sing_time)
    while True:
            print
            put_check=input("플레이리스트에 넣으시겠습니까?(Y/N)\n Input : ")
            if put_check.upper() == 'Y':
                put_playlist_or_not(music_title,music_sing_time,Id)
                break
            elif put_check.upper() =='N':
                break
            else:
                print("Y 또는 N을 입력해주세요")
        
        
def put_playlist_or_not(title,sing_time,Id):
    count=cursor_music.execute(query.show_playlist_name,(Id))
    playlist=cursor_music.fetchall()
    
    
    if count == 0:
        print("플레이리스트가 존재하지 않습니다.")
        choose=input("플레이리스트를 만드시겠습니까?(Y/N)\n Input : ")
        if choose.upper()=='Y':
            pl.make_playlist(Id,connection_,cursor_music)
            put_playlist_or_not(title,sing_time,Id)
        else:
            return
    print("-------------Playlist 목록--------------")
    for i in range(0,len(playlist)):
        print(str(i+1)+". "+playlist[i][0])
    else:
        while True:
            print()
            playlist_number=int(input("어느 Playlist에 넣으시겠습니까?(위의 번호 입력)\n뒤로가기 : -1\n Input : "))
            
            if playlist_number>=1 and playlist_number<=len(playlist):
                check=cursor_music.execute(query.is_already_put_music,(playlist[playlist_number-1][0],Id,title,sing_time))
                result=cursor_music.fetchone()
                if check!=0:
                    print()
                    print("이미 해당 playlist에 담겨있습니다.")
                else:
                    cursor_music.execute(query.insert_playlist_music,(playlist[playlist_number-1][0],Id,0,title,sing_time))
                    connection_.commit()                                                                                                   
                    print()
                    print(playlist[playlist_number-1][0]+" 에 담았습니다.")
                    break
            elif playlist_number==-1:
                break
            else:
                print("다시 입력해주세요")
        
        
def compare_day(payment):
    today=datetime.datetime.today()
    print()
    if payment+datetime.timedelta(30)>datetime.date(today.year,today.month,today.day):
        print(">>>>노래 전체 듣기가능<<<<")
        return True
    else:
        print(">>>>1분 미리듣기만 가능<<<<")
        return False


def increase_streaming(title,sing_time):
    cursor_music.execute(query.plus_today_soundtrack,(title,sing_time))
    connection_.commit()
    if user_age<10:
        cursor_music.execute(query.plus_play_count_0,(title,sing_time))
    elif user_age>=10 and user_age<20:
        cursor_music.execute(query.plus_play_count_10,(title,sing_time))   
    elif user_age>=20 and user_age<30:
        cursor_music.execute(query.plus_play_count_20,(title,sing_time))
    elif user_age>=30 and user_age<40:
        cursor_music.execute(query.plus_play_count_30,(title,sing_time))
    elif user_age>=40 and user_age<50:
        cursor_music.execute(query.plus_play_count_40,(title,sing_time))
    else:
        cursor_music.execute(query.plus_play_count_50,(title,sing_time))
    connection_.commit()

def show_statistics_ratio(cursor,music_title,music_sing_time):
    cursor.execute(query.show_statistics_ratio,(music_title,music_sing_time))
    result=cursor.fetchone()
    under_ten=result[0]
    tens=result[1]
    twentys=result[2]
    thirtys=result[3]
    fourtys=result[4]
    upper_fiftys=result[5]
    total=under_ten+tens+twentys+thirtys+fourtys+upper_fiftys
    if total == 0:
        print("통계치를 내는데 현재 정보가 부족합니다.")
    else:
        print("10대 이하: "+str(round(under_ten/total*100,1))+'%'+' 10대: '+str(round(tens/total*100,1))+'%'+' 20대: '+str(round(twentys/total*100,1))\
             +'%'+' 30대: '+str(round(thirtys/total*100,1))+'%'+' 40대: '+str(round(fourtys/total*100,1))+'%'+' 50대 이상: '+str(round(upper_fiftys/total*100,1))+'%')
        

def search_main(cursor_sm,Id,age,user_payment,connection):
    global search_payment
    search_payment=user_payment
    global user_age
    user_age=age
    global cursor_music
    cursor_music=cursor_sm
    global user_Id
    global connection_
    global is_continue
    connection_=connection
    user_Id=Id
    print('-----------------음원 검색-----------------')
    menu_number=input("1. 음원명으로 검색\n2. 작곡가,편곡가,작사가로 검색\n3. 아티스트로 검색\n4. 뒤로가기 : \n Input: ")
    switch_search_music(menu_number)
    if not is_continue:
        return
    search_main(cursor_sm,Id,age,user_payment,connection)
