[ReadMe]

■ 事前準備

	・bin ディレクトリに FSharp-2.0.0.0\v4.0\bin\gac ※ 内の
		ファイルを配置

	・F# の v4.0\bin と Azure SDK の bin ディレクトリを
		環境変数 PATH に設定

	※ F# CTP 解凍先

■ ビルド

	> deploy_local.bat

■ Compute Emulator 上での実行

	このディレクトリの親ディレクトリで以下を実行

	> csrun /run:output;sample_fs_aspnet\ServiceConfiguration.cscfg

