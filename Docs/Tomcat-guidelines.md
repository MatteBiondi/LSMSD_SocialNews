# Tomcat

1) Download binary release from <https://tomcat.apache.org/download-90.cgi>
2) Unzip archive into *tomcat_base* directory
3) Set JRE_HOME environment variable as path to JAVA >= 8 base directory
4) Configure admin user adding following lines to **_tomcat_base_**/apache-tomcat-9.0.71/conf/tomcat-users.xml
```xml
<role rolename="admin-gui"/>
<user username="admin" password="admin" roles="manager-gui, admin-gui"/>

<role rolename="manager-script"/>
<user username="tomcat" password="tomcat" roles="manager-script"/>
```  
5) (optional) Add **_tomcat_base_**/apache-tomcat-9.0.71/bin to path and set **CATALINA_HOME** environment variable 
   as *tomcat_base* 

Dashboard can be accessed via <http://localhost:8080>
