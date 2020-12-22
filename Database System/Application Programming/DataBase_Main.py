#!/usr/bin/env python
# coding: utf-8

# In[24]:


import pymysql as pms
import SQL_Query as query
import User_Main as u_main


# In[19]:


connection=pms.connect(host="127.0.0.1",port=3306,user="root",password="martin!1107",db="soundtrack")


# In[20]:


cursor=connection.cursor()


# In[21]:


print("-----------------데이터베이스 음원 스트리밍에 오신 것을 환영합니다-----------------")


# In[22]:


print("로그인이 필요합니다.")


# In[ ]:

def show_signup():
    print("회원가입란")
    user_id=input("ID : ")
    result= cursor.execute(query.is_id_exist,(user_id))
    while True:
        if result==0:
            print("ID 사용가능")
            break
        else:
            print()
            print("종료하려면 Q를 입력해주세요")
            user_id=input("ID를 다시 입력해주세요 : ")
            if user_id.upper() == 'Q':
                return
            result= cursor.execute(query.is_id_exist,(user_id))
            if result==0:
                print("ID 사용가능")
                break
            
    password=input("Password : ")
    phone = input("Phone : ")
    address = input("Address : ")
    ssn =input("Social Security Number(앞자리) : ")
    Name=input("Name : ")
    email=input("Email : ")
    
    
        
        
    cursor.execute(query.signup_query,(user_id,phone,address,ssn,password,Name,0,email,'2015-10-10','root'))
    connection.commit()
    print()
    print("회원가입이 완료되었습니다.")
    print()
           
        

def show_login_window(id,password):    
    cursor.execute(query.login_query,(id,password))
    result=cursor.fetchone()
    return result

while True:
    print()
    is_id_exist=input("""아이디가 존재하나요?(Y/N)\n"N" 입력시 회원가입란이 나옵니다. \n종료하실려면 Q를 입력해주세요\nInput : """)
    if is_id_exist.upper() == "Y":
        id=input("Id를 입력해주세요 : ")
        password=input("password를 입력해주세요 : ")
        result=show_login_window(id,password)
        if result is not None:
            u_main.show_user_main(id,result[2],cursor,result[3],connection,result[4])
        else:
            print()
            print("ID 또는 Password가 존재하지 않습니다..")
            print()
    elif is_id_exist.upper() == "N":
        print()
        show_signup()
    elif is_id_exist.upper()=='Q' :
        break
    else:
        print("Y,N,Q 중 하나를 입력해주세요 ")
else:
    connection.close()
    


    

