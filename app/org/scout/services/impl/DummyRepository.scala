package org.scout.services.impl

import org.scout.services.Repository
import org.scout.services.StoredDto
import scala.concurrent.Future
import java.util.concurrent.ConcurrentHashMap

class DummyRepository() extends Repository {
  var map = new ConcurrentHashMap[String, StoredDto]()
  def get(id: String) : Future[StoredDto] = Option(map.get(id)).map(Future.successful).getOrElse(Future.failed(NoSuchEntry(id)))
  def save(dto: StoredDto) : Future[Unit] = Future.successful(map.put(dto.id, dto))
  def remove(id: String) : Future[Unit] = Future.successful(map.remove(id))
}