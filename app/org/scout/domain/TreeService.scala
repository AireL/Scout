package org.scout.domain

import org.scout.domain.dto._
import scala.concurrent.Future
import org.scout.domain.actors.ExpectedConfig

trait TreeService {
  def root : Future[JsonNode]
  def get(id: Identity) : Future[JsonNode]
  def updateName(id: Identity, name: DisplayName => DisplayName) : Future[JsonNode]
  def updateParams(id: Identity, params: Map[String, String] => Map[String, String]) : Future[JsonNode]
  def moveNode(id: Identity, newParent: Identity) : Future[JsonNode]
  def addChild(id: Identity, newChild: JsonNode) : Future[JsonNode]
  def removeNode(id: Identity) : Future[JsonNode]
  
  def registerConfig(id: Identity, name: Name, config: Config) : Unit
  def getConfig(id: Identity, name: Name) : Future[ExpectedConfig]
}