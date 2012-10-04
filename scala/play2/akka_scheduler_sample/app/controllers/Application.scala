package controllers

import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.libs.concurrent._

import akka.util.duration._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def addJob = Action { req =>
    // 1秒後に非同期処理を実行
    Akka.system.scheduler.scheduleOnce(0 seconds) {
        println("start sleep : " + req)
        Thread.sleep(5000)
        println("stop sleep : " + req)
    }

    Ok("added job")
  }

}