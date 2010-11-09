baseDir = System.getProperty("base.dir")

//イベント処理の定義
eventCreatedArtefact = {type, name ->

	storyFileBody = """
scenario "$name", {
	when "", {
	}

	then "", {
	}
}
"""
	if (type == "Domain Class") {
		new File("${baseDir}/spec/${name}Scenario.story").withWriter {
			it.write(storyFileBody);
		}
	}
}
