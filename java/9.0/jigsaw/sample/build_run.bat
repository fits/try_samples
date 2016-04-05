@echo off

javac -d mods src/jp.sample/module-info.java src/jp.sample/jp/sample/SampleApp.java

java -mp mods -m jp.sample/jp.sample.SampleApp
