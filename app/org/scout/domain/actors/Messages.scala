package org.scout.domain.actors

import org.scout.domain.dto._
import akka.actor.ActorRef

case object Ok

case class UpdateParams(override val id: Identity, updateParams: Map[String, String] => Map[String, String]) extends Message
case class UpdateDisplayName(override val id: Identity, updateDisplayName: DisplayName => DisplayName) extends Message
case class UpdateParent(override val id: Identity, parentId: Identity, rootRef: ActorRef) extends Message
case class AddChild(override val id: Identity, child: JsonNode) extends Message
case class CannotAddChildToLeaf(override val id: Identity) extends Message
case class RemoveChild(override val id: Identity, child: Identity) extends Message
case class RemoveNode(override val id: Identity) extends Message
case class AddChildActor(override val id: Identity, child: Identity, childRef: ActorRef) extends Message
case class UpdateSuccess(override val id: Identity, updatedNode: JsonNode) extends Message
case class RegisterConfig(override val id: Identity, name: Name, config: Config) extends Message
case class GetConfig(override val id: Identity, name: Name) extends Message
case class ExpectedConfig(override val id: Identity, config: Option[(Name, Config)]) extends Message
case class Get(override val id: Identity) extends Message
case class GetSuccess(override val id: Identity, node: JsonNode) extends Message

trait Message {
  def id: Identity
}