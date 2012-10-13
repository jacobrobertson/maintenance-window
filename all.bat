call cls

call set HUDSON_PORT=80
call set HUDSON_HOME=C:\Users\jacob\.jenkins
call set HUDSON_WAR=%HUDSON_HOME%\jenkins.war
call set PLUGIN=maintenance-window

call set SETTINGS_HOME=C:\Users\jacob\workspace\view-job-filters
call set MAVEN_HOME=C:\Program Files (x86)\Maven\apache-maven-2.2.1
call "%MAVEN_HOME%\bin\"mvn.bat clean install eclipse:eclipse -Dmaven.test.skip=true -gs %SETTINGS_HOME%\jenkins-settings.xml -Pjenkins-repos

call rmdir /S /Q "%HUDSON_HOME%\plugins\%PLUGIN%"
call copy target\%PLUGIN%.hpi "%HUDSON_HOME%\plugins"

call "C:\Program Files\Java\jdk1.6.0_26\bin\"java.exe -jar %HUDSON_WAR% --httpPort=%HUDSON_PORT%
