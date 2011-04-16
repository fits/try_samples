@echo off

fsc DefaultPage.fs /target:library /out:bin\Fits.Sample.dll

cd ..
cspack sample_fs_aspnet\ServiceDefinition.csdef /role:WebRole1;sample_fs_aspnet /out:output /rolePropertiesFile:WebRole1;sample_fs_aspnet\properties.txt /copyonly
