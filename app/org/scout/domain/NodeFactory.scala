package org.scout.domain

import org.scout.domain.dto._

trait NodeFactory {
  def extensibleNode(name: DisplayName, params: Map[String, String]) : Node
  def node(name: DisplayName, params: Map[String, String]) : Node
}

object NodeFactory extends NodeFactory {
  def extensibleNode(name: DisplayName, params: Map[String, String]) : Node = new {
    override val children = List()
  } with Node(Identity(), name, params) with HasChildren with BranchNode

  def node(name: DisplayName, params: Map[String, String]) : Node = new {
    override val children = List()
  } with Node(Identity(), name, params) with HasChildren with LeafNode
}