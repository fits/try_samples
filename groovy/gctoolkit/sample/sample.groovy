@Grab('com.microsoft.gctoolkit:gctoolkit-vertx:2.0.4')
import com.microsoft.gctoolkit.GCToolKit
import com.microsoft.gctoolkit.io.SingleGCLogFile
import java.nio.file.Path

def logFile = new SingleGCLogFile(Path.of(args[0]))
def toolkit = new GCToolKit()

def jvm = toolkit.analyze(logFile)

println jvm.dump()
