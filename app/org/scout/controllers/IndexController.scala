package org.scout.controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import org.scout.controllers.utilities.ControllerToolkit
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class IndexController()(implicit override val executionContext: ExecutionContext) extends Controller with ControllerToolkit {

  def index() = FutureGET(Future.successful(Ok(org.scout.views.html.index("OK"))))
}