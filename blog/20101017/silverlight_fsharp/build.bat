rem build ListSample

set FSHARP_SL_LIB=C:\FSharp-2.0.0.0\Silverlight\2.0\bin
set SL_LIB=C:\Program Files (x86)\Reference Assemblies\Microsoft\Framework\Silverlight\v4.0

fsc -a --noframework --out:app\ListSample.dll -r:"%FSHARP_SL_LIB%\FSharp.Core.dll" -r:"%SL_LIB%\System.dll" -r:"%SL_LIB%\System.Windows.dll" src\ListSampleApp.fs

copy %FSHARP_SL_LIB%\FSharp.Core.dll app\
