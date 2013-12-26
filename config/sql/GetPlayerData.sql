SELECT username,email,password,status,money,fake_money,time_insert,player_id from cs_player
    WHERE username = :username where status = 1
