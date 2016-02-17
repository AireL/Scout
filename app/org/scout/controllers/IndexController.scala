package org.scout.controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import org.scout.controllers.utilities.ControllerToolkit
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import play.api.libs.json.Json
import scala.util.Random
import org.scout.domain.TreeService

class IndexController(treeService: TreeService)(
    implicit override val executionContext: ExecutionContext) extends Controller with ControllerToolkit {
  def index(path : String) = GET(
    Ok(org.scout.views.html.templates.layout(org.scout.views.html.templates.navbar(), org.scout.views.html.templates.footer()))
  )

  def home() = GET(
    Ok(org.scout.views.html.home())
  )

  def numbers() = JsonGET((1 to 10).toList.map(_ => Random.nextInt()))

  def table() = GET(
    Ok(org.scout.views.html.table())
  )

  def create() = GET(
    Ok(org.scout.views.html.create())
  )

  def nodeForm() = GET(
    Ok(org.scout.views.html.createNode())
  )

  def addNode() = GET(
    Ok("OK")
  )

  def root() = JsonFutureGET(
    treeService.root
  )
}
