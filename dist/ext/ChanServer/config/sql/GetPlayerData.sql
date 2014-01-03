SELECT username,email,password,status,money,fake_money,time_insert,player_id,cs_level,cs_appellation from cs_player
    WHERE username = :username and status =1
