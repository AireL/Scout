package org.scout.services.impl

import akka.actor.Actor
import org.scout.services.Repository
import scala.concurrent.ExecutionContext
import org.scout.domain.dto.Identity
import org.scout.domain.actors.Ok
import play.api.libs.json.Json
import org.scout.domain.actors.Snapshot
import org.scout.domain.actors.Snapshot._
import org.scout.services.StoredDto
import akka.actor.Props

class RepositoryActor private(repo: Repository)(implicit ex: ExecutionContext) extends Actor {
  
  def receive = {
    case GetFromRepository(id) =>
      val origin = sender()
      repo.get(id.value)
        .map(dto => origin ! Json.fromJson[Snapshot](Json.parse(dto.content)))
    case RemoveFromRepository(id) =>
      val origin = sender()
      repo.remove(id.value)
        .map(_ => origin ! Ok)
    case UpdateInRepository(id, json) =>
      val origin = sender()
      repo.save(StoredDto(id.value, json))
        .map(_ => origin ! Ok)
  }
}

object RepositoryActor {
  def props(repo: Repository)(implicit ex: ExecutionContext) : Props = Props(new RepositoryActor(repo))
}

case class GetFromRepository(id: Identity)
case class RemoveFromRepository(id: Identity)
case class UpdateInRepository(id: Identity, json: String)
