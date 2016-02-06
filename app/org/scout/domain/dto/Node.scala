package org.scout.domain.dto

import java.util.UUID

case class Node(id: Identity, displayName: DisplayName, parameters: Map[String, String])

trait HasParent {
  def parent: Node
}
trait HasChildren {
  def children: List[Node]
}
trait TypedNode {
  def configType: Name
}
case class DisplayName(val value: String) extends AnyVal
case class Identity(val value: String) extends AnyVal 
object Identity {
  def apply() : Identity = Identity(UUID.randomUUID().toString)
}

trait Configurable {
  def configuration: Map[Name, Config]
}

case class Config(fields : List[Field])
case class Name(val value: String) extends AnyVal
case class Field(key: Key, value: Value)
case class Key(val value: String) extends AnyVal
case class Value(val value: String) extends AnyVal