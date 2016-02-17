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
import play.api.libs.json._
import org.scout.services.impl.UpdateInRepository
import org.scout.services.impl.RemoveFromRepository

object NodeActor {
  def props(node: JsonNode, parent: Option[(Identity, ActorRef)], repoActor: ActorRef)(implicit ex: ExecutionContext): Props = {
    Props(new NodeActor(Identity(node.id), DisplayName(node.displayName), node.params, parent, node.children, repoActor))
  }
  protected final val done : ActorRef => Future[ActorRef] = Future.successful(_)
}

class NodeActor private (val id: Identity, var displayName: DisplayName, var params: Map[String, String], var parent: Option[(Identity, ActorRef)],
                         childNodes: List[JsonNode], repo: ActorRef)(implicit ex: ExecutionContext) extends Actor {
  
  implicit val timeout: Timeout = Timeout(1500, TimeUnit.MILLISECONDS)
  val extensible = JsonNode.extensible
  var children = childNodes.map(cn => (Identity(cn.id) -> context.actorOf(NodeActor.props(cn, Some(id, self), repo), cn.id))).toMap
  var config : Map[Name, Config] = Map()
  def isExtensible : Boolean = params.get(extensible).flatMap(i => Try(i.toBoolean).toOption).getOrElse(false)
  
  private def snapshot : Snapshot = {
    Snapshot(id, displayName, params, children.map(_._1).toList, config, parent.map(_._1))
  }
  
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
      (repo ? RemoveFromRepository(id))
        .map(_ => context.stop(self))
    case AddChildActor(id, child, ref) => 
      children = children + (child -> ref)
      sender() ! self
    case RegisterConfig(id, conf) if id == this.id =>
      config = config + (conf.name -> conf)
    case msg @ GetConfig(_, name) =>
      config.get(name).map(cn => sender() ! ExpectedConfig(id,Some(cn)))
        .orElse(parent.map(_._2 forward msg))
        .getOrElse(sender() ! ExpectedConfig(id, None))
    case AddChild(id, node) if id == this.id && !isExtensible =>
      sender() ! CannotAddChildToLeaf(id)
    case msg: Message if msg.id == id => (check andThen sendUpdate)(msg)
    case msg: Message => children.values.foreach(_ forward msg)
  }

  private val sendUpdate : Future[ActorRef] => Unit = _.map { origin =>
    toJson.map(origin ! GetSuccess(id, _))
      .recover { case ex: Exception => ex.printStackTrace() }
      .map(_ => repo ! UpdateInRepository(id, snapshot.toJson))
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
      children = children + (Identity(node.id) -> context.actorOf(NodeActor.props(node, Some(this.id, self), repo)))
      NodeActor.done(sender())
    case RemoveChild(id, child) => 
      children = children.filterNot(_._1 == id)
      NodeActor.done(sender())
    case Get(id) => 
      val origin = sender()
      NodeActor.done(origin)
  }
}

case class Snapshot(id: Identity, displayName: DisplayName, params: Map[String, String], children: List[Identity], config: Map[Name, Config], parent: Option[Identity]) {
  def toJson : String = Json.toJson(this)(Snapshot.format).toString
}
case object Snapshot {
  implicit lazy val nameFmt = Name.nameFmt
  implicit val identityFmt = Json.format[Identity]
  implicit val displayNameFmt = Json.format[DisplayName]
  implicit val jsonWrites = new Writes[Map[Name, Config]] {
    def writes(o: Map[Name, Config]): JsValue = {
      val keyAsString = o.map { kv => (kv._1.value -> Json.toJson(kv._2)(configFmt).toString) } // Convert to Map[String,Int] which it can convert
      Json.toJson(keyAsString)
    }
  }
  implicit val jsonReads = new Reads[Map[Name, Config]] {
    def reads(js: JsValue): JsResult[Map[Name, Config]] = 
      JsSuccess((js.as[Map[String, String]]).map(kv => 
        (Name(kv._1), Json.fromJson(Json.parse(kv._2))(configFmt).get)))
  }
  implicit lazy val configFmt = Config.configFmt
  implicit val format = Json.format[Snapshot]
  val extensible = "extensible"
}