package org.scout.controllers

import play.api.mvc.Action
import play.api.mvc.Controller

class IndexController() extends Controller {
  def index() = Action { request =>
    Ok(org.scout.views.html.index("OK"))
  }
}