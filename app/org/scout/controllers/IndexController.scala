package org.scout.controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import org.scout.controllers.utilities.ControllerToolkit
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class IndexController()(implicit override val executionContext: ExecutionContext) extends Controller with ControllerToolkit {
  def index() = GET(
    Ok(org.scout.views.html.layout(org.scout.views.html.navbar(), org.scout.views.html.footer()))
  )
}