@echo off

set JAVA_OPTS=-Xmx128m -javaagent:%JBOSSAOP_LIB%\jboss-aop-jdk50.jar -Djboss.aop.path=. -Djboss.aop.verbose=false

set classpath=dest;%classpath%

groovy %*
