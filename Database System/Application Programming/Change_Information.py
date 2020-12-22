#!/usr/bin/env python
# coding: utf-8

# In[2]:


import SQL_Query as query
import datetime
cursor_ci=None
ci_id=None



is_off=True

# In[10]:


class switch_change():
    def __init__(self,choose,Id,connection):
        self.func_name="case_"+str(choose) if hasattr(self,"case_"+str(choose)) else "case_09"
        self.id=Id
        self.connection=connection
        self.change_dir=change_dir={1:"password",2:"Email",3:'Phone',4:'Address',5:'Name'}
        self.func_dir=func_dir={1:query.update_user_password,2:query.update_user_email,3:query.update_user_phone,4:query.update_user_address,5:query.update_user_password}
        self.case=getattr(self,self.func_name)
        return self.case()
    #비밀번호 변경
    def case_1(self):
        change_Inform(self.change_dir,self.func_dir,self.id,1,self.connection)
    #이메일 변경
    def case_2(self):
        change_Inform(self.change_dir,self.func_dir,self.id,2,self.connection)
    #핸드폰 번호 변경
    def case_3(self):
        change_Inform(self.change_dir,self.func_dir,self.id,3,self.connection)
    #주소 변경
    def case_4(self):
        change_Inform(self.change_dir,self.func_dir,self.id,4,self.connection)
    #이름 변경
    def case_5(self):
        change_Inform(self.change_dir,self.func_dir,self.id,5,self.connection)
    #결제하기
    def case_6(self):
        payment_now(self.id,self.connection)
     #계정 삭제
    def case_7(self):
        delete_user(self.id,self.connection)
        global is_off
        global ci_id
        is_off=False
        ci_id=None
    def case_8(self):
        global is_off
        is_off=False
    def case_9(self):
        print("다른 번호 입력바랍니다.")
        global is_off
        is_off=True
        


def delete_user(id,connection):
    print()
    check=input("정말로 삭제하시겠습니까?(Y/N)\n Input : ")
    print()
    if check.upper() =='N':
        print('취소되었습니다.')
        return
    else:
        cursor_ci.execute(query.delete_id,(id))
        connection.commit()
        print("삭제 되었습니다.")

def payment_now(Id,connection):
    print()
    pay=input("결제하시겠습니까?(Y/N)\n뒤로가기 : Q\n Input : ")
    if pay == 'Q' or pay=='N':
        return
    if pay.upper() == 'Y':
        print()
        now=datetime.datetime.today()
        now_string=str(now.year)+"-"+str(now.month)+"-"+str(now.day)
        cursor_ci.execute(query.update_user_payment,(now_string,Id))
        cursor_ci.execute(query.update_user_subscriber,(Id))
        connection.commit()
        print()
        print("결제가 완료되었습니다.")



def change_Inform(change_dir,change_func,Id,num,connection):
    word=change_dir[num]
    print()
    change_word=input("변경할 "+word+" 입력\n Input : ")
    cursor_ci.execute(change_func[num],(change_word,Id))
    connection.commit()
    print()
    print("변경되었습니다.")
   

def change_information_main(cursor,Id,connection):
    global cursor_ci
    cursor_ci=cursor
    cursor_ci.execute(query.get_user_paymentday,(Id))
    result=cursor_ci.fetchall()
    today=datetime.datetime.today()
    print()
    if result[0][0] ==datetime.date(2015,10,10):
        print("결제 정보가 없습니다.")
    else:
        after_date=result[0][0]+datetime.timedelta(30)      
        print("전곡 듣기 가능 날짜 : ",result[0][0].year,'-',result[0][0].month,'-',result[0][0].day,' ~ ',after_date.year,'-',after_date.month,'-',after_date.day)
    print()
    
    choose=input("1. 비밀번호 변경\n2. email 변경\n3. 핸드폰 번호 변경\n4. 주소 변경\n5. 이름 변경\n6. 결제하기\n7. 계정삭제\n8. 뒤로가기\n Input : ")
    check=True
    global is_off
    is_off=True
    global ci_id
    ci_id=Id
    switch_change(choose,Id,connection)
    if is_off:
        change_information_main(cursor,Id,connection)
    if ci_id is None:
        return False
    else:
        return True
