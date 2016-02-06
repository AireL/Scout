package org.scout.domain

import org.scout.domain.dto._

object ConfiguredNode {
  def apply(name: Name, displayName: DisplayName, values: Map[String, String], curParent: Node) : Option[Node with TypedNode] = {
    def getBaseConfig(n: Node) : Option[Config] = n match {
      case node: Node with Configurable if node.configuration.get(name).isDefined => node.configuration.get(name)
      case node: Node with HasParent => getBaseConfig(node.parent)
      case other => None
    }
    for {
      config <- getBaseConfig(curParent) if valid(config, values)
    } yield {
      new {
        override val parent = curParent
        override val children = List()
        override val configType = name
      } with Node(Identity(), displayName, values) with HasChildren with HasParent with TypedNode
    }
  }
  
  private def valid(config: Config, values: Map[String, String]) : Boolean = {
    config.fields.forall(field => values.get(field.key.value).map(!_.isEmpty).getOrElse(false))
  }  
}