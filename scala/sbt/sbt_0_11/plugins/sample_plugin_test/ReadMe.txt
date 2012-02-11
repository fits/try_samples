[ReadMe]

sbt の自作プラグインの動作テスト

■ 手動配置

	(1) sample_plugin をビルド
	(2) (1) で作成した sample_plugin-0.1-SNAPSHOT.jar を
		project/lib に手動で配置

	これで sbt を実行するとプラグインがロードされるので、
	sample-task や sample-name を実施。

