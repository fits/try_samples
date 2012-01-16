package controllers

import play.api._
import play.api.mvc._

import models._

object Users extends Controller {

  def show(id: String) = Action {
	val user = User("test", "test@aaa.co.jp")

    Ok(views.html.users.show(user))
  }
  
}