Put the MySQL JDBC driver here.

1. Download Connector/J from https://dev.mysql.com/downloads/connector/j/
   (choose "Platform Independent" and download the ZIP).
2. Extract it and copy the jar into this folder.
3. RENAME it to exactly:  mysql-connector-j.jar

The NetBeans project already has lib/mysql-connector-j.jar on its classpath,
so once the file is named correctly the project compiles with no extra setup.

If you would rather keep the original filename, right-click the project in
NetBeans -> Properties -> Libraries -> Add JAR/Folder and select your jar.
