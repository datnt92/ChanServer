SELECT username,email,password,status,money,fake_money,time_insert,player_id from cs_player
    WHERE username = :username 
and password = :password where status = 1
