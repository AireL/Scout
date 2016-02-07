package org.scout.domain.impl

import akka.actor.ActorSystem
import org.scout.domain.dto._
import org.scout.domain.TreeService
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import org.scout.domain.actors.NodeActor
import akka.pattern.ask
import org.scout.domain.actors._
import java.util.concurrent.TimeUnit
import akka.util.Timeout
import org.scout.domain.NodeFactory
import org.scout.services.Repository
import org.scout.services.impl.RepositoryActor

class TreeServiceImpl(system: ActorSystem, repo: Repository, nodes: List[JsonNode])(implicit ex: ExecutionContext) extends TreeService {
  private val rootNode = NodeFactory.extensibleNode(DisplayName("Root"), Map[String,String]())
  
  implicit val timeout: Timeout = Timeout(1500, TimeUnit.MILLISECONDS)
  val repoActor = system.actorOf(RepositoryActor.props(repo), "Repository")
  val rootActor = system.actorOf(NodeActor.props(rootNode, None, repoActor), "Root")
  
  // TEST DATA
  val c1 = NodeFactory.extensibleNode(DisplayName("C1"), Map("A" -> "A value", "B" -> "B Value"))
  val c2 = NodeFactory.extensibleNode(DisplayName("C2"), Map("A" -> "A value", "B" -> "B Value"))
  val c11 = NodeFactory.node(DisplayName("C1-1"), Map("A" -> "A value", "B" -> "B Value"), parent = Some(Identity(c2.id)))

  addChild(Identity(rootNode.id), c1)
    .flatMap(_ => addChild(Identity(rootNode.id), c2))
    .flatMap(_ => addChild(Identity(c1.id), c11))
    .flatMap(_ => root)
    .recover {case ex: Exception => ex.printStackTrace()}
    .map(println)
  
  private def getAndReturn(msg: Message) : Future[JsonNode] = (rootActor ? (msg)).flatMap {
      case msg: GetSuccess => Future.successful(msg.node)
      case msg: CannotAddChildToLeaf => Future.failed(FailedToAddNode(msg))
      case other: Message => Future.failed(new RuntimeException(s"Failed to get a valid response sending $msg, got $other instead"))
    }
  
  def root : Future[JsonNode] = getAndReturn(Get(Identity(rootNode.id)))
  def get(id: Identity) : Future[JsonNode] = getAndReturn(Get(id))
  def updateName(id: Identity, name: DisplayName => DisplayName) : Future[JsonNode] = getAndReturn(UpdateDisplayName(id, name))
  def updateParams(id: Identity, params: Map[String, String] => Map[String, String]) : Future[JsonNode] = getAndReturn(UpdateParams(id, params))
  def moveNode(id: Identity, newParent: Identity) : Future[JsonNode] = getAndReturn(UpdateParent(id, newParent, rootActor))
  def addChild(id: Identity, newChild: JsonNode) : Future[JsonNode] = getAndReturn(AddChild(id, newChild))
  def removeNode(id: Identity) : Future[JsonNode] = getAndReturn(RemoveNode(id))
  
  def registerConfig(id: Identity, name: Name, config: Config) : Unit = rootActor ! RegisterConfig(id, name, config)
  def getConfig(id: Identity, name: Name) : Future[ExpectedConfig] = (rootActor ? (GetConfig(id, name))).map(_.asInstanceOf[ExpectedConfig])
}

case class FailedToAddNode(msg: CannotAddChildToLeaf) extends RuntimeException