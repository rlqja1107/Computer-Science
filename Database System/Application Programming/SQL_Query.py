


login_query=("SELECT Id,Password,SSN,Payment_day,Super_ID FROM streaming_subscriber WHERE Id=%s and Password=%s")




realtime_get_query=("SELECT Title,Over_19,GROUP_CONCAT(Name),is_Group,Solo_Group FROM soundtrack,sing,artist WHERE "\
+"Title=S_Title AND Sing_time=S_Sing_Time AND S_Name=Name AND S_Debut_Date=Debut_Date GROUP BY Title,Sing_time ORDER BY Today_streaming DESC LIMIT 10")



signup_query=("INSERT INTO streaming_subscriber VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)")


is_id_exist="SELECT Id FROM streaming_subscriber WHERE Id=%s"


show_playlist_name="SELECT Playlist_Name FROM playlist_name WHERE U_Id=%s"



is_playlist_name_exist="SELECT Playlist_Name FROM playlist_name WHERE Playlist_Name=%s AND U_Id=%s"




show_playlist_muisc="SELECT U_Id,Title,Over_19,Sing_time,Genre,is_Group,Artist_Name,Solo_Group FROM soundtrack_artist_2,playlist WHERE Title=Play_Title AND Sing_time=Play_Sing_Time"\
+" AND Playlist_name=%s AND U_id=%s "


show_music_detail="SELECT * FROM soundtrack_artist_2 WHERE Title=%s AND Sing_time=%s"

show_music_compose="SELECT * FROM soundtrack_compose_write WHERE Title=%s AND Sing_time=%s"


plus_own_soundtrack=("UPDATE playlist SET listen_count=listen_count+1 WHERE U_id=%s AND Play_Title=%s AND Play_Sing_Time=%s AND Playlist_name=%s")


plus_today_soundtrack=("UPDATE soundtrack SET Today_streaming=Today_streaming+1 WHERE Title=%s AND Sing_time=%s")


get_ranking_top10=("SELECT Title,Over_19,Sing_time,is_Group,Artist_Name,Solo_Group FROM playlist,soundtrack_artist_2 WHERE U_id=%s AND Playlist_name=%s AND Play_Title=Title "\
+"AND Play_Sing_Time=Sing_time ORDER BY listen_count DESC LIMIT 10")


plus_play_count_0=("UPDATE play_count SET under_tens=under_tens+1 WHERE C_Title=%s AND C_Sing_Time=%s")
plus_play_count_10=("UPDATE play_count SET Tens=Tens+1 WHERE C_Title=%s AND C_Sing_Time=%s")
plus_play_count_20=("UPDATE play_count SET Twentys=Twentys+1 WHERE C_Title=%s AND C_Sing_Time=%s")
plus_play_count_30=("UPDATE play_count SET Thirtys=Thirtys+1 WHERE C_Title=%s AND C_Sing_Time=%s")
plus_play_count_40=("UPDATE play_count SET Fourtys=Fourtys+1 WHERE C_Title=%s AND C_Sing_Time=%s")
plus_play_count_50=("UPDATE play_count SET upper_fiftys=upper_fiftys+1 WHERE C_Title=%s AND C_Sing_Time=%s")


insert_playlist_name=("INSERT INTO playlist_name VALUES(%s,%s)")



search_music_by_name="SELECT Title,Artist_Name,Over_19,Solo_Group,Sing_time,is_Group FROM soundtrack_artist_2 WHERE Title=%s"


search_music_by_composer_name="SELECT scw.Title,GROUP_CONCAT(DISTINCT A.Name),Over_19,A.Solo_Group,scw.Sing_time,scw.is_Group FROM soundtrack_compose_write AS scw,sing,artist AS A WHERE scw.Name=%s AND "\
+"scw.Title=S_Title AND scw.Sing_time=S_Sing_Time AND S_Name=A.Name AND S_Debut_Date=A.Debut_Date GROUP BY scw.Title,scw.Sing_time"


search_music_by_artist_name="SELECT Title,Name,Sing_time,Over_19,is_Group,GROUP_CONCAT(Name),Solo_Group FROM soundtrack,sing,artist WHERE Title=S_Title AND Sing_time="\
+"S_Sing_Time AND S_Name=Name AND S_Debut_Date=Debut_Date AND Name=%s GROUP BY Title,Sing_time"

search_music_by_artist_group="SELECT Title,Sing_time,Solo_Group,Over_19,is_Group,GROUP_CONCAT(Name) FROM soundtrack,sing,artist WHERE Title=S_Title AND Sing_time="\
+"S_Sing_Time AND S_Name=Name AND S_Debut_Date=Debut_Date AND Solo_Group=%s AND is_Group=%s GROUP BY Title,Sing_time"


insert_playlist_music=("INSERT INTO playlist VALUES(%s,%s,%s,%s,%s)")


get_user_paymentday="SELECT Payment_day FROM streaming_subscriber WHERE Id=%s"
search_n_ranking_0=("SELECT Title,Artist_Name,Over_19,Solo_Group,Sing_time,is_Group FROM play_count,soundtrack_artist_2 WHERE C_Title=Title AND C_Sing_Time=Sing_time ORDER BY under_tens DESC LIMIT %s")
search_n_ranking_10=("SELECT Title,Artist_Name,Over_19,Solo_Group,Sing_time,is_Group FROM play_count,soundtrack_artist_2 WHERE C_Title=Title AND C_Sing_Time=Sing_time ORDER BY Tens DESC LIMIT %s")
search_n_ranking_20=("SELECT Title,Artist_Name,Over_19,Solo_Group,Sing_time,is_Group FROM play_count,soundtrack_artist_2 WHERE C_Title=Title AND C_Sing_Time=Sing_time ORDER BY Twentys DESC LIMIT %s")
search_n_ranking_30=("SELECT Title,Artist_Name,Over_19,Solo_Group,Sing_time,is_Group FROM play_count,soundtrack_artist_2 WHERE C_Title=Title AND C_Sing_Time=Sing_time ORDER BY Thirtys DESC LIMIT %s")
search_n_ranking_40=("SELECT Title,Artist_Name,Over_19,Solo_Group,Sing_time,is_Group FROM play_count,soundtrack_artist_2 WHERE C_Title=Title AND C_Sing_Time=Sing_time ORDER BY Fourtys DESC LIMIT %s")
search_n_ranking_50=("SELECT Title,Artist_Name,Over_19,Solo_Group,Sing_time,is_Group FROM play_count,soundtrack_artist_2 WHERE C_Title=Title AND C_Sing_Time=Sing_time ORDER BY upper_fiftys DESC LIMIT %s")



show_statistics_ratio=("SELECT under_tens,Tens,Twentys,Thirtys,Fourtys,upper_fiftys FROM play_count WHERE C_Title=%s AND C_Sing_time=%s")


update_user_subscriber="UPDATE streaming_subscriber SET Subscribe_term=Subscribe_term+1 WHERE Id=%s"
update_user_payment="UPDATE streaming_subscriber SET Payment_day=%s WHERE Id=%s "
update_user_password=("UPDATE streaming_subscriber SET Password=%s WHERE Id=%s")
update_user_email=("UPDATE streaming_subscriber SET Email=%s WHERE Id=%s")
update_user_phone=("UPDATE streaming_subscriber SET Phone=%s WHERE Id=%s")
update_user_address=("UPDATE streaming_subscriber SET Address=%s WHERE Id=%s")
update_user_name=("UPDATE streaming_subscriber SET Name=%s WHERE Id=%s")


delete_id="DELETE FROM streaming_subscriber WHERE Id=%s"

get_artist_index="SELECT Name,Solo_Group,Sex,Debut_date FROM artist WHERE Name=%s"

delete_artist="DELETE FROM artist WHERE Name=%s AND Debut_Date=%s"

insert_artist="INSERT INTO artist(Debut_date,Sex,Solo_Group,Name) VALUES(%s,%s,%s,%s)"

get_soundtrack_index="SELECT Title,Sing_time,Over_19,is_Group,Artist_Name,Solo_Group FROM soundtrack_artist_2 WHERE Title=%s"

delete_soundtrack="DELETE FROM soundtrack WHERE Title=%s AND Sing_time=%s"

insert_soundtrack="INSERT INTO soundtrack(Title,Over_19,Sing_time,Genre,is_Group) VALUES(%s,%s,%s,%s,%s)"


delete_composer="DELETE FROM composer_writer WHERE Name=%s AND Birth=%s"

insert_composer="INSERT INTO composer_writer(Name,Type,Birth) VALUES(%s,%s,%s)"


insert_into_sing="INSERT INTO sing VALUES(%s,%s,%s,%s)"

get_composer_index="SELECT Name,Type,Birth FROM composer_writer WHERE Name=%s"

get_composer_index_2="SELECT Name,Type,Birth FROM composer_writer WHERE Name=%s AND Birth=%s"


insert_compose_write="INSERT INTO compose_write VALUES(%s,%s,%s,%s)"

show_all_streaming="SELECT Id,Name,Payment_day,Subscribe_term FROM streaming_subscriber WHERE Super_ID=%s"

delete_user="DELETE FROM streaming_subscriber WHERE Id=%s"

extend_payment="UPDATE streaming_subscriber SET Payment_day=%s,Subscribe_term=Subscribe_term+%s WHERE Id=%s"

get_payment_day="SELECT Payment_day FROM streaming_subscriber WHERE Id=%s"

insert_play_count_table="INSERT INTO play_count(C_Title,C_Sing_Time) VALUES(%s,%s)"

show_all_artist="SELECT ALL Name,Solo_Group,Debut_Date,Sex FROM artist WHERE Name !='이름없음' GROUP BY Solo_Group,Name"

show_all_soundtrack="SELECT Title,Sing_time,Over_19,Artist_Name,is_Group,Solo_Group FROM soundtrack_artist_2"

show_all_composer="SELECT Name,Type FROM composer_writer WHERE Name!='x'"

get_group_name="SELECT Name,Debut_Date FROM artist WHERE Solo_Group=%s"

is_already_put_music="SELECT U_id FROM playlist WHERE Playlist_name=%s AND U_id=%s AND Play_Title=%s AND Play_Sing_Time=%s"

delete_playlist="DELETE FROM playlist WHERE Playlist_name=%s AND U_id=%s"

delete_playlist_name="DELETE FROM playlist_name WHERE Playlist_Name=%s AND U_Id=%s"
