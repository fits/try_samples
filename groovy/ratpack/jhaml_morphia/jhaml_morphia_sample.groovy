
import groovy.text.SimpleTemplateEngine

import com.google.code.morphia.Morphia
import com.mongodb.Mongo
import com.cadrlife.jhaml.JHaml
import com.google.code.morphia.annotations.*
import org.bson.types.ObjectId

@Entity(value = "users", noClassnameStored = true)
class User {
	@Id ObjectId id
	String name
}

def renderHaml = {template, params->
	def hamlText = new JHaml().parse(new File("templates/${template}").text)
	new SimpleTemplateEngine().createTemplate(hamlText).make(params).toString()
}

def db = new Morphia().createDatastore(new Mongo("localhost"), "book_review")

get("/") {
	def users = db.find(User.class).order("name")

	renderHaml "index.haml", ["users": users]
}


get("/users") {
	def users = db.find(User.class).order("name")

	renderHaml "user.haml", ["users": users]
}

post("/users") {
	db.save(new User(name: params.name))

	response.sendRedirect("users")
}
