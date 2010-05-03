[ReadMe]

□ ビルド方法

	(1) キーファイル作成

		>sn -k testkey.snk

	(2) ビルド

		>csc /t:library *.cs /keyfile:testkey.snk

	(3) COM 登録

		>regasm /codebase TestSample.dll
