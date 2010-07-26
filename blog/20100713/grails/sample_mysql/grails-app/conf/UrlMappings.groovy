class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

	//	"/tables/$id?"(controller: "informations", action: "tables")

		"500"(view:'/error')
	}
}
