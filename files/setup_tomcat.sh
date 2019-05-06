TOMCAT_DIR="/usr/local/tomcat"

echoc 34 "moving webapps to tomcat"
mv *.war $TOMCAT_DIR/webapps/ >/dev/null

echoc 34 "starting tomcat"
$TOMCAT_DIR/bin/catalina.sh run > tomcat.log 2>&1 &

echoc 34 "tomcat setup done"
