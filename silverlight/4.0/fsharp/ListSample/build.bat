rem build ListSample

fsc -a --noframework --out:app\ListSample.dll -r:"C:\FSharp-2.0.0.0\Silverlight\2.0\bin\FSharp.Core.dll" -r:"C:\Program Files (x86)\Reference Assemblies\Microsoft\Framework\Silverlight\v4.0\System.dll" -r:"C:\Program Files (x86)\Reference Assemblies\Microsoft\Framework\Silverlight\v4.0\System.Windows.dll" MainPage.fs

copy C:\FSharp-2.0.0.0\Silverlight\2.0\bin\FSharp.Core.dll app\
