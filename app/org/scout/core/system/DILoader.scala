package org.scout.core.system

import play.api.ApplicationLoader.Context
import play.api._
import play.api.routing.Router
import router.Routes
import com.softwaremill.macwire._
import controllers.Assets
import org.scout.controllers.IndexController

class DILoader extends ApplicationLoader {
  def load(context: Context) = {
    // Configure Logger
    Logger.configure(context.environment)

    (new BuiltInComponentsFromContext(context) with AppComponents).application
  }
}

trait AppComponents extends BuiltInComponents with AppModule {
  lazy val assets: Assets = wire[Assets]
  lazy val prefix: String = "/"
  lazy val router: Router = wire[Routes]
}

trait AppModule {
  // Define your dependencies and controllers
  lazy val indexController = wire[IndexController]
}