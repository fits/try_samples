@echo off

set GROOVY_JAR=%GROOVY_HOME%\embeddable\groovy-all-1.7.4.jar

rem   groovycによるコンパイル実行（groovyc をそのまま使うとバッチが
rem   終了してしまうためコンパイラーを直接実行）
java -Dgroovy.home=%GROOVY_HOME% -classpath %GROOVY_JAR% org.codehaus.groovy.tools.GroovyStarter --main org.codehaus.groovy.tools.FileSystemCompiler --conf %GROOVY_HOME%\conf\groovy-starter.conf ImplInf.groovy

rem  JARファイル化
jar cfm implinf.jar manifest.mf *.class

rem  .NETアセンブリ化
ikvmc implinf.jar %GROOVY_JAR%
