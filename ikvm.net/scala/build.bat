@echo off

set SCALA_JAR=%SCALA_HOME%\lib\scala-library.jar
set SCALA_COMPILE_JAR=%SCALA_HOME%\lib\scala-compiler.jar;%SCALA_JAR%

rem   コンパイル実行（scalac を使うとバッチが終了してしまうので自前で実施）
java -Dscala.home=%SCALA_HOME% -cp %SCALA_COMPILE_JAR% scala.tools.nsc.Main SampleTest.scala

rem  JARファイル化
jar cfm sample.jar manifest.mf *.class

rem  .NETアセンブリ化
ikvmc sample.jar %SCALA_JAR%
