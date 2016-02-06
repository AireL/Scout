package org.scout.core.system

import play.api.ApplicationLoader.Context
import play.api._
import play.api.routing.Router
import router.Routes
import com.softwaremill.macwire._
import controllers.Assets
import org.scout.controllers.IndexController
import org.scout.domain.TreeService
import org.scout.domain.impl.TreeServiceImpl
import org.scout.domain.impl.TreeServiceImpl
import akka.actor.ActorSystem
import scala.concurrent.Future
import org.scout.domain.dto.JsonNode

class DILoader extends ApplicationLoader {
  def load(context: Context) = {
    // Configure Logger
    Logger.configure(context.environment)
    (new BuiltInComponentsFromContext(context) with AppComponents).application
  }
}

trait AppWiring {
  def system: ActorSystem
  def treeService: TreeService
}

trait AppComponents extends BuiltInComponents with AppModule{
  lazy val assets: Assets = wire[Assets]
  lazy val prefix: String = "/"
  lazy val router: Router = wire[Routes]
  applicationLifecycle.addStopHook { () => Future.successful(this.system.shutdown()) }
}

trait AppModule extends AppWiring {
  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global
  // Define your dependencies and controllers
  val system = ActorSystem("Nodes")
  val nodes : List[JsonNode] = List()
  
  val treeService = wire[TreeServiceImpl]
  lazy val indexController = wire[IndexController]
}