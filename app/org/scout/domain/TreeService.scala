package org.scout.domain

import org.scout.domain.dto._
import scala.concurrent.Future

trait TreeService {
  def root : Future[JsonNode]
  def get(id: Identity) : Future[JsonNode]
  def updateName(id: Identity, name: DisplayName => DisplayName) : Future[JsonNode]
  def updateParams(id: Identity, params: Map[String, String] => Map[String, String]) : Future[JsonNode]
  def moveNode(id: Identity, newParent: Identity) : Future[JsonNode]
  def addChild(id: Identity, newChild: Node) : Future[JsonNode]
  def removeNode(id: Identity) : Future[JsonNode]
}