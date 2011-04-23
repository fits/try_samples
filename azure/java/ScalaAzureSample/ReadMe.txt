[ReadMe]

■ 準備

	WorkerRole1\approot\JRE ディレクトリ内に jre6.zip ファイルを配置
	WorkerRole1\approot\lib ディレクトリに以下の JAR ファイルを配置

		・grizzly-framework-2.0.1.jar
		・grizzly-http-2.0.1.jar
		・grizzly-http-server-2.0.1.jar
		・scala-library.jar

	WorkerRole1\approot\lib ディレクトリに
	scala\grizzly\http_sample のビルド結果である以下の JAR ファイルを配置

		・http_sample_2.8.1-1.0.jar

■ ローカル用パッケージの作成

	> ant -f package.xml

	Compute Emulator 上での実行

	> emulatorTools\RunInEmulator.cmd

■ クラウド用パッケージの作成

	> ant -f package_cloud.xml

