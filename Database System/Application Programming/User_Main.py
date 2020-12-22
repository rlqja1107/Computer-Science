
import datetime
import SQL_Query as query
import Playlist as pl
import Search_Music as sm
import Statistics_Show as ss
import Change_Information as ci



user_age=None
cursor_user=None
realtime_ranking=None
user_payment=None
is_continue=True
connection=None

class switch_user():
    def __init__(self,number,id,connection,is_supervisor,payment,age):
        self.fun_name="case_"+str(number) if hasattr(self,"case_"+str(number)) else "case_7"
        self.connection=connection
        self.id=id
        self.is_supervisor=is_supervisor
        self.age=age
        self.payment=payment
        self.case=getattr(self,self.fun_name)
        return self.case()
#플레이리스트 보기
    def case_1(self):
        pl.show_playlist(cursor_user,self.id,self.age,self.payment,self.connection)
        global is_continue
        is_continue=True
#음악검색
    def case_2(self):
        sm.search_main(cursor_user,self.id,self.age,self.payment,self.connection)
        global is_continue
        is_continue=True
#각 나이별 노래 순위 보기
    def case_3(self):
        ss.statistics_main(cursor_user,self.id,self.connection,self.payment,self.age)
        global is_continue
        is_continue=True
#개인정보 변경
    def case_4(self):
        check=ci.change_information_main(cursor_user,self.id,self.connection)
        global is_continue
        is_continue=check
#뒤로 가기
    def case_5(self):
        global is_continue
        is_continue=False
#다른 번호 입력시
    def case_6(self):
        global is_continue
        if self.is_supervisor is None:
            supervise_mode()
        else:
            print("다른 번호 입력해주세요")
            
        is_continue=True
    def case_7(self):
            print("다른 번호 입력해주세요")
            global is_continue
            is_continue=True
            

#관리자 모드
def supervise_mode():
    print("-----------------관리자 모드----------------------")
    
    choose=input("1. 음원, 아티스트, 작사가 관리\n2. 스트리밍 구독자 관리\n3. 뒤로가기\n Input :")
    if choose is '1':
        manage_overall()
    elif choose is '2':
        manage_streaming()
    elif choose is '3':
        return
    else:
        print("다른 번호를 입력해주세요")
    supervise_mode()

def show_all_streaming():
    cursor_user.execute(query.show_all_streaming,('root'))
    result=cursor_user.fetchall()
    print("-------------------전체 스트리밍 Id------------------------")
    for i in range(0,len(result)):
        print(str(i+1)+". Id: "+result[i][0]+"  Name: "+result[i][1]+"  Payment_day : "+result[i][2].strftime('%Y-%m-%d')+"구독 전체기간 : "+str(result[i][3]))
    return result
def manage_streaming():
    result=show_all_streaming()
    print()
    while True:
        choose=input("1. 삭제\n2. 결제기간 연장\n3. 뒤로가기\n Input : ")
        if choose is '3' or choose.isalpha():
            print()
            break
        elif choose is '1':
            streaming_name=input("스트리밍 ID : ")
            cursor_user.execute(query.delete_user,(streaming_name))
            connection.commit()
            print("삭제 완료")
            print()
        elif choose is '2':
            while True:
                streaming_name=input("뒤로가기 : Q\n스트리밍 ID 입력 : ")
                if streaming_name is 'Q':
                    break
                extend=input("연장 개월 수 : ")
                cursor_user.execute(query.get_payment_day,(streaming_name))
                pay_day=cursor_user.fetchone()
                if pay_day is None:
                    continue
    
                before_time=pay_day[0]
                after_time=before_time+datetime.timedelta(int(extend)*30)
                cursor_user.execute(query.extend_payment,(after_time,extend,streaming_name))
                connection.commit()
                print("연장 완료")
                break
        else:
            print("번호를 입력해주세요")

#음원,아티스트,작사가 관리
def manage_overall():
    print()
    print("추가/삭제 가능")  
    choose=input('1. 아티스트 관리\n2. 음원 관리\n3. 작사가 관리\n4. 뒤로가기\n Input : ')
    choose_dir={1:[query.get_artist_index,query.delete_artist,query.insert_artist],2:[query.get_soundtrack_index,query.delete_soundtrack,\
    query.insert_soundtrack],3:[query.get_composer_index,query.delete_composer,query.insert_composer]}
    if choose.isalpha():
        print("숫자를 입력해주세요")
        return
    elif int(choose) in (1,2,3):
        insert_delete_update(choose_dir,int(choose))  
    elif choose=='4':
        return
    else:
        print("범위 안의 숫자를 입력해주세요")
        
    manage_overall()
            
def insert_delete_update(choose_dir,choose):
    print()
    choose2=input("1. 삭제\n2. 추가\n3. 모두 보기\n4. 뒤로가기\n Input : ")
    choose2_dir={1:"삭제",2:"추가"}
    choose_func={1:delete_database,2:add_database}
    
    if choose2.isalpha():
        print("번호를 입력해주세요")
        insert_delete_update(choose_dir,choose)
    elif choose2=='4':
        return
    elif choose2 in ('1','2'):
        choose_func[int(choose2)](choose_dir,int(choose2),choose)
    elif choose2 is '3':
        show_all_inform(choose)
    else:
        print("다른 번호를 입력해주세요")
        insert_delete_update(choose)
def show_all_inform(choose):
    if choose is 1:
        cursor_user.execute(query.show_all_artist,())
        result=cursor_user.fetchall()
        if result is None:
            print("음악 없음")
            return
        else:
            print("---------------전체 아티스트 목록----------------------")
    
        for i in range(0,len(result)):
            if result[i][1] == "solo_":
                print(str(i+1)+". "+result[i][0]+" - (No Group)"+ " - 성별 : "+result[i][3])
            else:
                print(str(i+1)+". "+result[i][0]+" - ("+result[i][1]+")"+ " - 성별 : "+result[i][3])
        print()
    elif choose is 2:
        cursor_user.execute(query.show_all_soundtrack,())
        result=cursor_user.fetchall()
        if result is None:
            print("아티스트 없음")
            return
        else:
            print("-------------------------전체 음원 목록---------------------------")
        for i in range(0,len(result)):
            is_over="(19)" if result[i][2] == 1 else ""
            is_group=result[i][5] if result[i][4] == 1 else result[i][3]
            print(str(i+1)+". "+result[i][0]+is_over+" - "+"("+is_group+")   Time : "+result[i][1])
        print()
    else:
        cursor_user.execute(query.show_all_composer,())
        result=cursor_user.fetchall()
        if result is None:
            print('작곡가,편곡가 없음')
            return
        else:
            print("--------------------------Composer 목록-----------------------------")
        for i in range(0,len(result)):
            print(str(i+1)+". "+result[i][0]+"("+result[i][1]+")")
        print()
            
            
def request_inform(choose_dir,choose,choose2,choose_word):   
    count=cursor_user.execute(choose_dir[choose][0],(choose_word))
    if count==0:
        print("해당하는 이름(명)이 없습니다.")
        return None
    result=cursor_user.fetchall()
    return result
   
#추가
def add_database(choose_dir,choose2,choose):
    if choose is 1:
        artist_name=input("1. Artist 이름 : ")
        debut_date=input("2. 데뷔 날짜(0000-00-00) : ")
        sex=input("3. 성별(남자/여자) : ")
        group=input("4. 그룹 이름(없을 시 생략) :")
        if group is '':
            group='solo_'
        try:
            cursor_user.execute(choose_dir[choose][choose2],(debut_date,sex,group,artist_name))
            connection.commit()
            print("---성공---")
        except:
            print("이미 존재합니다. 다시 설정바랍니다.")
            add_database(choose_dir,choose2,choose)
    
        
    elif choose is 2:
        soundtrack_name=input("1. 음원명 : ")
        over_19=input("2. 19세이상(Y/N) : ")
        over_19='1' if over_19.upper()=='Y' else '0'
        sing_time=input("3. 노래시간(MM:SS) : ")
        genre=input("4. 장르 : ")
        album_real=(('이름없음',None,0,0),)             
        is_group=input("5-1. 그룹으로 불렀는가?(Y/N) : ").upper()
        group_check=0
        artist_name=[]
        if is_group == 'Y':
            group_check=1
            is_group=input("5-2그룹명 : ")
        elif is_group =='N':
            flag=True
            while flag:
                count=input("5-2. 아티스트 전체 인원(숫자만) : ")
                for i in range(0,int(count)):
                    check=True
                    while check:
                        name=input("5-"+str(3+i)+". 아티스트명 : ")
                        get=cursor_user.execute(query.get_artist_index,(name))
                        if get ==0:
                            find_or_not=print("아티스트명이 존재하지 않습니다. 더 찾으시겠습니까?(Y/N)")
                            if find_or_not is 'N':
                                check=False
                        else:
                            get_result=cursor_user.fetchall()
                            for j in range(0,len(get_result)):
                                print(str(j+1)+". "+get_result[j][0]+"  데뷔날짜 : "+get_result[j][3])
                            what=input("해당 아티스트 번호 입력 : ")
                            artist_name.append(get_result[int(what)-1])
                            check=False
                            if i==int(count)-1:
                                flag=False
        writer_name=input('6. 작곡(생략가능) : ')
        writer_name='x' if writer_name=="" else writer_name
        composer_name=input("7. 작사(생략가능) : ")
        composer_name='x' if composer_name =="" else composer_name
        name={0:writer_name,1:composer_name}
        type_={0:'작곡',1:'작사'}
        save_=input("저장하시겠습니까?(Y/N)\n Input : ").upper()
        if save_!='Y':
            return
        try:
            cursor_user.execute(choose_dir[choose][choose2],(soundtrack_name,over_19,sing_time,genre,group_check))
            connection.commit()
        except:
            print()
            print("이미 DB에 저장되어있습니다.")
            return
        
        for i in range(0,2):
            search_compose(name[i],type_[i],soundtrack_name,sing_time)
        print('DB 저장중 ....')       
        if group_check==0:
            for i in range(0,len(artist_name)):
                cursor_user.execute(query.insert_into_sing,(soundtrack_name,sing_time,artist_name[i][0],artist_name[i][3]))
                connection.commit()
        else:
            cursor_user.execute(query.get_group_name,(is_group))
            result=cursor_user.fetchone()
            cursor_user.execute(query.insert_into_sing,(soundtrack_name,sing_time,result[0],result[1]))
            connection.commit()
        cursor_user.execute(query.insert_play_count_table,(soundtrack_name,sing_time))
        connection.commit()
        print()
        print("성공")
    #작사가 삽입
    elif choose is 3:
        insert_compose()
        
        
def insert_compose():
    compose=input("1. 작사/작곡 선택 : ")
    composer_name=input("2. 성명 : ")
    birth=input("3. 주민번호 앞자리 : ")
    count=cursor_user.execute(query.get_composer_index_2,(composer_name,birth))
    if count is 0:
        cursor_user.execute(query.insert_composer,(composer_name,compose,birth))
        connection.commit()
        print("성공")
        print()
    else:
        check=input("이미 존재합니다. 다시 입력(Y/N)\n Input : ").upper()
        if check == 'Y':
            insert_compose()
    
    
def search_compose(name,type_,title,sing_time):
    if name=='x':
        try:
            cursor_user.execute(query.insert_compose_write,(0,'x',title,sing_time))
            connection.commit()
            return
        except:
            print()
            return              
    print()
    print(type_+" : "+name+"  선택")
    count=cursor_user.execute(query.get_composer_index,(name))
    if count == 0:
        check=input("존재하지 않습니다. 더 검색하겠습니까?(Y/N)\n Input : ").upper()
        if check =='Y':
            name_=input("성명 : ")
            cursor_user.fetchall()
            search_compose(name_,type_,title,sing_time)
            return
        else:
            search_compose('x',type_,title,sing_time)
            return
    result=cursor_user.fetchall()
    for i in range(0,len(result)):
        print(str(i+1)+". 이름 : "+result[i][0]+"  Type : "+result[i][1]+"   Birth : "+result[i][2])
    print()
    choo=input("번호 입력 : ")
    try:
        cursor_user.execute(query.insert_compose_write,(result[int(choo)-1][2],result[int(choo)-1][0],title,sing_time))
        connection.commit()
    except:
        print()
    #삭제
def delete_database(choose_dir,choose2,choose):
    print()
    delete=input("삭제 이름(명)\n 뒤로가기 : Q\n Input : ")
    if delete.upper() =='Q':
        return
    index_list=[]
    #아티스트
    if choose is 1:
        result=request_inform(choose_dir,choose,choose2,delete)
        if result is None:
            delete_database(choose_dir,choose2,choose)
            return
        for i in range(0,len(result)):
            print(str(i+1)+". "+"이름 : "+result[i][0]+"   "+"성별 : "+result[i][2]+"   "+"데뷔 날짜 : "+result[i][3])
            index_list.append(result[i])
        print()
        choose3=int(input("번호 입력 : "))
        print()
        try:
            cursor_user.execute(query.delete_artist,(result[choose3-1][0],result[choose3-1][3]))
            connection.commit()
            print()
            print("성공")
        except:
            print()
            print("음원이 존재하여 제거 불가능 -> 음원 제거 필수")
       
        
    # 음원 
    elif choose is 2:
        result=request_inform(choose_dir,choose,choose2,delete)
        if result is None:
            delete_database(choose_dir,choose2,choose)
            return
        print()
        for i in range(0,len(result)):           
            is_over="(19)" if result[i][2]==1 else ''
            is_group=result[i][5]+"(그룹)" if result[i][3] else result[i][4]
            print(str(i+1)+". "+result[i][0]+is_over+" - "+is_group+" Time : "+result[i][1])
        print()
        choose3=input("번호 입력 : ")
        
        cursor_user.execute(query.delete_soundtrack,(result[int(choose3)-1][0],result[int(choose3)-1][1]))
        connection.commit()
        print()
        print("삭제 성공")
    #작사가              
    elif choose is 3:
        result=request_inform(choose_dir,choose,choose2,delete)
        if result is None:
            delete_database(choose_dir,choose2,choose)
            return
        for i in range(0,len(result)):
            print(str(i+1)+". "+"이름 : "+result[i][0]+"("+result[i][1]+")"+" - 생년월일 : "+result[i][2])
        print()
        choose3=int(input("번호 입력 : "))
        print()
        try:
            cursor_user.execute(query.delete_composer,(result[choose3-1][0],result[choose3-1][2]))
            connection.commit()
            print("성공")
        except:
            print("음원이 존재하여 제거 불가능 -> 음원 제거 필수")        
    
#실시간 차트 보여주기
def show_menu(id,connection,is_supervisor,payment,age):
    if is_supervisor is None:
        num=input("1. 플레이리스트를 보기 \n2. 음원 검색 \n3. 각 나이별 노래 순위 \n4. 개인정보 변경 \n5. 뒤로가기\n6. 관리자 모드\n Input : " )
    else:
        num=input("1. 플레이리스트를 보기 \n2. 음원 검색 \n3. 각 나이별 노래 순위 \n4. 개인정보 변경 \n5. 뒤로가기\n Input : " )
    switch_user(num,id,connection,is_supervisor,payment,age)
    
       


def show_realtime_chart():
    cursor_user.execute(query.realtime_get_query,())
    realtime_ranking=cursor_user.fetchall()
    print("-------------------실시간 차트 순위---------------------")
    for i in range(0,len(realtime_ranking)):
        is_over="(19)" if realtime_ranking[i][1]==1 else ""
        is_group=realtime_ranking[i][4] if realtime_ranking[i][3]==1 else realtime_ranking[i][2]
        print(str(i+1)+". "+realtime_ranking[i][0]+is_over+" - "+is_group)
    print()

# In[8]:


def age_convert(a):
    if a[0]=='0':
        born="20"+a[0:2]
        age=2020-int(born)+1
        return age
    else:
        born="19"+a[0:2]
        age=2020-int(born)+1
        return age
    
def show_user_main(id,age,cursor,payment_day,connection_,is_supervisor):
    Id=id
    global connection
    connection=connection_
    global is_continue
    is_continue=True
    user_age=age_convert(age[0:2])
    global cursor_user
    cursor_user=cursor
    print()
    print("환영합니다! "+id+"님")
   
    while is_continue:
        show_realtime_chart()
        show_menu(id,connection,is_supervisor,payment_day,user_age)
    
