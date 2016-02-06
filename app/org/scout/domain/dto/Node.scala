package org.scout.domain.dto

import java.util.UUID

trait AbstractNode {
  protected type NodeType <: AbstractNode
}
trait LeafNode extends AbstractNode {
  override final protected type NodeType = LeafNode
}
trait BranchNode extends AbstractNode {
  override final protected type NodeType = Node
}
case class Node(id: Identity, displayName: DisplayName, parameters: Map[String, String]) extends AbstractNode

trait HasParent {
  self: AbstractNode =>
  def parent: Node
}
trait HasChildren {
  self: AbstractNode =>
  def children: List[NodeType]
}
trait TypedNode {
  self: AbstractNode =>
  def configType: Name
}
case class DisplayName(val value: String) extends AnyVal
case class Identity(val value: String) extends AnyVal 
object Identity {
  def apply() : Identity = Identity(UUID.randomUUID().toString)
}

trait Configurable {
  self: AbstractNode =>
  def configuration: Map[Name, Config]
}

case class Config(fields : List[Field])
case class Name(val value: String) extends AnyVal
case class Field(key: Key, value: Value)
case class Key(val value: String) extends AnyVal
case class Value(val value: String) extends AnyVal