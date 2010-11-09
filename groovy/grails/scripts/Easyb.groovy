
Ant.property(environment: "env")
grailsHome = Ant.antProject.properties."env.GRAILS_HOME"

baseDir = System.getProperty("base.dir")
specDir = "${baseDir}/spec"
srcDir = "${baseDir}/grails-app"
destDir = "${specDir}/classes"

target("default": null) {
	clean()
	runEasyb()
}

target(initClasspath: "init classpath") {
    path (id: "easyb.classpath") {
		pathelement(path: destDir)

        fileset(dir: "${baseDir}") {
            include(name: "**/*.jar")
        }

        fileset(dir: "${grailsHome}") {
            include(name: "**/*.jar")
        }
    }
}

target(compile: "Compile Target Script") {
	depends(initClasspath)

	Ant.taskdef(name: "groovyc", classname: "org.codehaus.groovy.grails.compiler.GrailsCompiler") {
		classpath(refid: "easyb.classpath")
	}

	Ant.mkdir(dir: destDir)

	Ant.groovyc(srcdir: srcDir, destdir: destDir, encoding:"UTF-8") {
        classpath(refid: "easyb.classpath")
	}
}

target(runEasyb: "Execute Easyb") {
	depends(compile)

    Ant.taskdef(name: "easyb", classname: "org.disco.easyb.ant.BehaviorRunnerTask") {
		classpath(refid: "easyb.classpath")
    }

    Ant.easyb {
        classpath(refid: "easyb.classpath")
        behaviors(dir: "${specDir}") {
            include(name: "**/*Story.groovy")
            include(name: "**/*.story")
            include(name: "**/*Specification.groovy")
            include(name: "**/*.specification")
        }
    }
}

target(clean: "clean") {
	Ant.delete(dir: destDir)
}
