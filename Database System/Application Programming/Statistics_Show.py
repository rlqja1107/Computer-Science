#!/usr/bin/env python
# coding: utf-8

# In[2]:


import SQL_Query as query
import Search_Music as sm


# In[3]:





# In[ ]:
def each_age_show_statistics(cursor,age,n,Id,payment,connection,age_):
    result=None
    if age==0:
        cursor.execute(query.search_n_ranking_0,(n))
        result=cursor.fetchall()
    elif age==1:
        cursor.execute(query.search_n_ranking_10,(n))
        result=cursor.fetchall()
    elif age==2:
        cursor.execute(query.search_n_ranking_20,(n))
        result=cursor.fetchall()
    elif age==3:
        cursor.execute(query.search_n_ranking_30,(n))
        result=cursor.fetchall()
    elif age==4:
        cursor.execute(query.search_n_ranking_40,(n))
        result=cursor.fetchall()
    elif age==5:
        cursor.execute(query.search_n_ranking_50,(n))
        result=cursor.fetchall()
    if result is None:
        print()
        print("현재 정보가 부족합니다")
        return
    sm.user_age=age_
    sm.user_Id=Id
    sm.connection_=connection
    sm.search_payment=payment
    sm.convert_to_music_title(Id,result,0,1,2,3,4,5,cursor)

def statistics_main(cursor,Id,connection,payment,age):
    print()
    age_number=input("0. 10대이하 선호 노래\n1. 10대 선호 노래 \n2. 20대 선호 노래\n3. 30대 선호 노래\n4. 40대 이상 선호 노래\n5. 50대이상 선호 노래 : 50\n뒤로가기 :-1 \n Input : ")

    if age_number=='-1':
        return
    elif age_number.isdigit() ==False:
        print("숫자 입력해주세요")
        statistics_main(cursor,Id,connection)
        return
    elif int(age_number) not in (0,1,2,3,4,5):
        print("알맞은 숫자를 써주세요")
        return
    age_dir={0:'10대 이하',1:'10대',2:'20대',3:'30대',4:'40대',5:'50대 이상'}
    print()
    n=input(age_dir[int(age_number)]+"가 선호하는 노래 상위 n개 보기\n Input : ")
    each_age_show_statistics(cursor,int(age_number),int(n),Id,payment,connection,age)
    statistics_main(cursor,Id,connection,payment,age)
    

