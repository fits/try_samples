
import com.cadrlife.jhaml.*

def renderHaml = {template, params->
	new JHaml().parse(new File("templates/${template}").text)
}

get("/") {
	
	
	
	
	renderHaml "index.haml", [:]
}

