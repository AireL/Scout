package org.scout.domain.actors

import akka.actor.Actor
import org.scout.domain.dto._
import akka.actor.ActorRef
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Try

object NodeActor {
  def props(node: JsonNode, parent: Option[(Identity, ActorRef)])(implicit ex: ExecutionContext): Props = {
    Props(new NodeActor(Identity(node.id), DisplayName(node.displayName), node.params, parent, node.children))
  }
  protected final val done : ActorRef => Future[ActorRef] = Future.successful(_)
}

class NodeActor private (val id: Identity, var displayName: DisplayName, var params: Map[String, String],
                         var parent: Option[(Identity, ActorRef)], childNodes: List[JsonNode])(implicit ex: ExecutionContext) extends Actor {
  
  implicit val timeout: Timeout = Timeout(1500, TimeUnit.MILLISECONDS)
  val extensible = JsonNode.extensible
  var children = childNodes.map(cn => (cn.id -> context.actorOf(NodeActor.props(cn, Some(id, self)), cn.id))).toMap
  var config : Map[Name, Config] = Map()
  def isExtensible : Boolean = params.get(extensible).flatMap(i => Try(i.toBoolean).toOption).getOrElse(false)
  
  private def toJson: Future[JsonNode] = {
    import scalaz._
    import Scalaz._

    children.map(child => child._2 ? Get(Identity(child._1))).toList.sequenceU.map(queries =>
      JsonNode(this.id.value, displayName.value, params, queries.flatMap {
        case GetSuccess(id, node) => List(node)
        case other                => Nil
      }, parent.map(_._1.value)))
  }

  def receive = {
    case RemoveNode(id) =>
      parent.foreach(p => p._2 forward RemoveChild(p._1, this.id))
      context.stop(self)
    case AddChildActor(id, child, ref) => 
      children = children + (child.value -> ref)
      sender() ! self
    case RegisterConfig(id, name, conf) if id == this.id =>
      config = config + (name -> conf)
    case GetConfig(id, name) if id == this.id =>
      sender() ! ExpectedConfig(id, config.get(name).map(name -> _))
    case AddChild(id, node) if id == this.id && !isExtensible =>
      sender() ! CannotAddChildToLeaf(id)
    case msg: Message if msg.id == id => (check andThen sendUpdate)(msg)
    case msg: Message => children.values.foreach(_ forward msg)
  }

  private val sendUpdate : Future[ActorRef] => Unit = _.map { origin =>
    toJson.map(origin ! GetSuccess(id, _))
      .recover { case ex: Exception => ex.printStackTrace() }
  }

  private def check: PartialFunction[Message, Future[ActorRef]] = {
    case UpdateParams(id, func) => 
      params = func(params)
      NodeActor.done(sender())
    case UpdateDisplayName(id, func) => 
      displayName = func(displayName)
      NodeActor.done(sender())
    case UpdateParent(id, parent, root) => 
      val origin = sender()
      (root ? AddChildActor(parent, id, self))
        .map(ref => this.parent = Some(parent -> ref.asInstanceOf[ActorRef]))
        .map(_ => this.parent.foreach(p => p._2 ! RemoveChild(p._1, id)))
        .map(_ => this.parent.foreach(vl => vl._2 !(Get(vl._1), origin)))
        .map(_ => origin)
    case AddChild(id, node) => 
      children = children + (node.id -> context.actorOf(NodeActor.props(node, Some(this.id, self))))
      NodeActor.done(sender())
    case RemoveChild(id, child) => 
      children = children.filterNot(_._1 == id)
      NodeActor.done(sender())
    case Get(id) => NodeActor.done(sender())
  }
}