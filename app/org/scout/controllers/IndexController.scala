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
  def index() = GET(
    Ok(org.scout.views.html.layout(org.scout.views.html.navbar(), org.scout.views.html.footer()))
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
