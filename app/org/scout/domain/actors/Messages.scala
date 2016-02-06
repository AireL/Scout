package org.scout.domain.actors

import org.scout.domain.dto.Node
import org.scout.domain.dto.Identity
import org.scout.domain.dto.JsonNode
import org.scout.domain.dto.DisplayName
import akka.actor.ActorRef

case object Ok

case class UpdateParams(override val id: Identity, updateParams: Map[String, String] => Map[String, String]) extends Message
case class UpdateDisplayName(override val id: Identity, updateDisplayName: DisplayName => DisplayName) extends Message
case class UpdateParent(override val id: Identity, parentId: Identity, rootRef: ActorRef) extends Message
case class AddChild(override val id: Identity, child: Node) extends Message
case class RemoveChild(override val id: Identity, child: Identity) extends Message
case class RemoveNode(override val id: Identity) extends Message
case class AddChildActor(override val id: Identity, child: Identity, childRef: ActorRef) extends Message
case class UpdateSuccess(override val id: Identity, updatedNode: JsonNode) extends Message
case class Get(override val id: Identity) extends Message
case class GetSuccess(override val id: Identity, node: JsonNode) extends Message

trait Message {
  def id: Identity
}