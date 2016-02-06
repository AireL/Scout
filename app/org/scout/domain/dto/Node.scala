package org.scout.domain.dto

import java.util.UUID
import org.scout.domain.NodeFactory

trait AbstractNode {
  def id: Identity
  def displayName: DisplayName
  def parameters: Map[String, String]
  def fullParams: Map[String, String]
  protected type NodeType <: AbstractNode
}
trait LeafNode extends AbstractNode {
  override final protected type NodeType = LeafNode
}
trait BranchNode extends AbstractNode {
  override final protected type NodeType = Node
}
case class Node(override val id: Identity, override val displayName: DisplayName, override val parameters: Map[String, String]) 
    extends AbstractNode with NodeFactory {
  def fullParams: Map[String, String] = parameters ++ additionalParameters
  protected def additionalParameters : Map[String, String] = Map()
  def extensibleNode(name: DisplayName, params: Map[String, String]) : Node = new {
    override val children = List()
    override val parent = this
  } with Node(Identity(), name, params) with HasChildren with BranchNode with HasParent

  def node(name: DisplayName, params: Map[String, String]) : Node = new {
    override val children = List()
    override val parent = this
  } with Node(Identity(), name, params) with HasChildren with LeafNode with HasParent
}

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
  var configuration: Map[Name, Config]
}

case class Config(fields : List[Field])
case class Name(val value: String) extends AnyVal
case class Field(key: Key, value: Value)
case class Key(val value: String) extends AnyVal
case class Value(val value: String) extends AnyVal