package org.scout.domain

import org.scout.domain.dto._

trait NodeFactory {
  def extensibleNode(name: DisplayName, params: Map[String, String], children: List[JsonNode] = List(), parent: Option[Identity]= None) : JsonNode 
  def node(name: DisplayName, params: Map[String, String], children: List[JsonNode] = List(), parent: Option[Identity] = None) : JsonNode
}

object NodeFactory extends NodeFactory {
  def extensibleNode(name: DisplayName, params: Map[String, String], children: List[JsonNode] = List(), parent: Option[Identity]= None) : JsonNode = 
    JsonNode(Identity.random.value, name.value, params + (JsonNode.extensible -> true.toString), children, parent.map(_.value))
  def node(name: DisplayName, params: Map[String, String], children: List[JsonNode] = List(), parent: Option[Identity] = None) : JsonNode = 
    JsonNode(Identity.random.value, name.value, params + (JsonNode.extensible -> false.toString), children, parent.map(_.value))
}