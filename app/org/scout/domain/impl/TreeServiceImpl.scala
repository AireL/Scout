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

class TreeServiceImpl(system: ActorSystem, nodes: List[Node])(implicit ex: ExecutionContext) extends TreeService {
  private val rootNode = new {
    override val children = nodes
  } with Node(Identity("Root"), DisplayName("Root"), Map()) with HasChildren with BranchNode
  
  implicit val timeout: Timeout = Timeout(1500, TimeUnit.MILLISECONDS)
  val rootActor = system.actorOf(NodeActor.props(rootNode, None), "Root")
  
  // TEST DATA
  val c1 = NodeFactory.extensibleNode(DisplayName("C1"), Map("A" -> "A value", "B" -> "B Value"))
  val c2 = NodeFactory.extensibleNode(DisplayName("C2"), Map("A" -> "A value", "B" -> "B Value"))
  val c11 = c1.node(DisplayName("C1-1"), Map("A" -> "A value", "B" -> "B Value"))

  addChild(rootNode.id, c1)
    .map(_ => addChild(rootNode.id, c2))
    .map(_ => addChild(c1.id, c11))
    .flatMap(_ => root)
    .map(println)
  
  private def getAndReturn(msg: Message) : Future[JsonNode] = (rootActor ? (msg)).map(_.asInstanceOf[GetSuccess].node)
  
  def root : Future[JsonNode] = getAndReturn(Get(Identity("Root")))
  def get(id: Identity) : Future[JsonNode] = getAndReturn(Get(id))
  def updateName(id: Identity, name: DisplayName => DisplayName) : Future[JsonNode] = getAndReturn(UpdateDisplayName(id, name))
  def updateParams(id: Identity, params: Map[String, String] => Map[String, String]) : Future[JsonNode] = getAndReturn(UpdateParams(id, params))
  def moveNode(id: Identity, newParent: Identity) : Future[JsonNode] = getAndReturn(UpdateParent(id, newParent, rootActor))
  def addChild(id: Identity, newChild: Node) : Future[JsonNode] = getAndReturn(AddChild(id, newChild))
  def removeNode(id: Identity) : Future[JsonNode] = getAndReturn(RemoveNode(id))
}