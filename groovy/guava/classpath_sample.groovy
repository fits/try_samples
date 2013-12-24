package sample

@Grab('com.google.guava:guava:16.0-rc1')
import com.google.common.reflect.ClassPath

class SampleData {
}

/* ディレクトリもしくは JAR ファイル内をスキャンするだけなので
 * このスクリプト内で定義しているクラスは取得できない
 */
ClassPath.from(getClass().classLoader).getTopLevelClasses('sample').each {
	println "package: ${it.packageName}, simpleName: ${it.simpleName}, name: ${it.name}"
}

/*
 * com.google.common パッケージ直下のクラスは存在しない
 */
ClassPath.from(getClass().classLoader).getTopLevelClasses('com.google.common').each {
	println "package: ${it.packageName}, simpleName: ${it.simpleName}, name: ${it.name}"
}

/*
 * com.google.common のサブパッケージ内のクラスも出力
 */
ClassPath.from(getClass().classLoader).getTopLevelClassesRecursive('com.google.common').each {
	println "package: ${it.packageName}, simpleName: ${it.simpleName}, name: ${it.name}"
}

