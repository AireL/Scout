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

object NodeActor {
  def props(node: AbstractNode, parent: Option[ActorRef])(implicit ex: ExecutionContext): Props = {
    val children : List[AbstractNode] = List(node).collect({ case node: Node with HasChildren => node.children}).flatten
    val parentDetails = parent.map(a => Option(node).collect({ case node: Node with HasParent => node.parent.id }).getOrElse(Identity("Root")) -> a)
    Props(new NodeActor(node.id, node.displayName, node.fullParams, parentDetails, children))
  }
  protected final val done = Future.successful(())
}

class NodeActor private (val id: Identity, var displayName: DisplayName, var params: Map[String, String],
                         var parent: Option[(Identity, ActorRef)], childNodes: List[AbstractNode])(implicit ex: ExecutionContext) extends Actor {
  
  implicit val timeout: Timeout = Timeout(1500, TimeUnit.MILLISECONDS)
  var children = childNodes.map(cn => (cn.id -> context.actorOf(NodeActor.props(cn, Some(self)), cn.id.value))).toMap
  
  private def toJson: Future[JsonNode] = {
    import scalaz._
    import Scalaz._

    children.map(child => child._2 ? Get(child._1)).toList.sequenceU.map(queries =>
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
      children = children + (child -> ref)
      sender() ! self
    case msg: Message if msg.id == id => (check andThen sendUpdate)(msg)
    case msg: Message => children.values.foreach(_ forward msg)
  }

  private val sendUpdate : Future[Unit] => Unit = _.map { _ =>
    val origin = sender()
    toJson.map(origin ! GetSuccess(id, _))
      .recover { case ex: Exception => ex.printStackTrace() }
  }

  private def check: PartialFunction[Message, Future[Unit]] = {
    case UpdateParams(id, func) => 
      params = func(params)
      NodeActor.done
    case UpdateDisplayName(id, func) => 
      displayName = func(displayName)
      NodeActor.done
    case UpdateParent(id, parent, root) => 
      (root ? AddChildActor(parent, id, self))
        .map(ref => this.parent = Some(parent -> ref.asInstanceOf[ActorRef]))
        .map(_ => this.parent.foreach(p => p._2 ! RemoveChild(p._1, id)))
        .map(_ => this.parent.foreach(vl => vl._2 forward Get(vl._1)))
    case AddChild(id, node) => 
      children = children + (node.id -> context.actorOf(NodeActor.props(node, Some(self))))
      NodeActor.done
    case RemoveChild(id, child) => 
      children = children.filterNot(_._1 == id)
      NodeActor.done
    case Get(id) => NodeActor.done
  }
}