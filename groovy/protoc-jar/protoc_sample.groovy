@Grab('com.github.os72:protoc-jar:3.0.0')
import com.github.os72.protocjar.Protoc

Protoc.runProtoc(["--java_out=${args[0]}", args[1]] as String[])
