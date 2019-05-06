echoc 33 "starting mysql"
/etc/init.d/mysql start >/dev/null
echoc 33 "sleep for a while ..."
sleep 4
echoc 33 "setup mysql users"
mysql --host=localhost --user=root --password=root << END
CREATE USER 'user1234'@'localhost' IDENTIFIED BY 'password1234';
GRANT ALL PRIVILEGES ON * . * TO 'user1234'@'localhost';
FLUSH PRIVILEGES;
DROP DATABASE IF EXISTS session_manager;
END
sleep 1
mysql --host=localhost --user=user1234 --password=password1234 << END
CREATE database session_manager;
END
echoc 33 "setup mysql done"
