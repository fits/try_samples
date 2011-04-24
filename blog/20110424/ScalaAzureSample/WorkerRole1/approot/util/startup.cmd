
cscript "util\unzip.vbs" JRE\jre6.zip . >> log.txt 2>> err.txt

set JRE_HOME=JRE
set CP=lib\grizzly_scala_sample_2.8.1-1.0;lib\grizzly-framework-2.0.1.jar;lib\grizzly-http-2.0.1.jar;lib\grizzly-http-server-2.0.1.jar;lib\scala-library.jar

start %JRE_HOME%\bin\java -cp %CP% fits.sample.Sample
