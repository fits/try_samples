
import groovy.text.SimpleTemplateEngine

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml

import com.google.code.morphia.Morphia
import com.mongodb.Mongo
import com.cadrlife.jhaml.JHaml
import com.google.code.morphia.annotations.*
import org.bson.types.ObjectId
import com.bleedingwolf.ratpack.RatpackServlet

//文字化けへの対応
RatpackServlet.metaClass.convertOutputToByteArray = {String output ->
	output.getBytes("UTF-8")
}

//HTMLエスケープ
String.metaClass.escape = {
	escapeHtml(delegate)
}

//Morphia モデルクラスの定義
@Entity(value = "users", noClassnameStored = true)
class User {
	@Id ObjectId id
	String name
}

class Comment {
	String content = ""
	Date createdDate = new Date()
	@Reference User user = null
}

@Entity(value = "books", noClassnameStored = true)
class Book {
	@Id ObjectId id = null
	String title
	String isbn
	@Embedded List<Comment> comments = []
}

//JHaml を使ったテンプレート処理
def renderHaml = {template, params->
	def hamlText = new JHaml().parse(new File("templates/${template}").text)
	new SimpleTemplateEngine().createTemplate(hamlText).make(params).toString()
}

//MongoDB への接続設定
def db = new Morphia().createDatastore(new Mongo("localhost"), "book_review")

//ページ処理
get("/") {
	def books = db.find(Book.class).order("title")
	def users = db.find(User.class).order("name")

	renderHaml "index.haml", ["books": books, "users": users]
}

get("/books") {
	def books = db.find(Book.class).order("title")

	renderHaml "book.haml", ["books": books]
}

post("/books") {
	db.save(new Book(title: params.title, isbn: params.isbn))

	response.sendRedirect("books")

	//NullPointerException 発生の回避策
	""
}

post("/comments") {
	def book = db.get(Book.class, new ObjectId(params.book))
	def user = db.get(User.class, new ObjectId(params.user))

	book.comments.add(new Comment(content: params.content, user: user))
	db.save(book)

	response.sendRedirect(".")

	//NullPointerException 発生の回避策
	""
}

get("/users") {
	def users = db.find(User.class).order("name")

	renderHaml "user.haml", ["users": users]
}

post("/users") {
	db.save(new User(name: params.name))

	response.sendRedirect("users")

	//NullPointerException 発生の回避策
	""
}
