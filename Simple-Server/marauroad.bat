set LOCAL_CLASSPATH=marauroa-simple-server.jar;lib\h2.jar;lib\jsqlparser.jar;lib\log4j.jar;lib\marauroa.jar;lib\mysql-connector-java-5.1.13-bin.jar;lib\org-openide-util-lookup.jar;
java -cp "%LOCAL_CLASSPATH%" marauroa.server.marauroad -c server.ini -l