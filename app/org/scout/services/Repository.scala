package org.scout.services

import scala.concurrent.Future

trait Repository {
  def get(id: String) : Future[StoredDto]
  def save(dto: StoredDto) : Future[Unit]
  def remove(id: String) : Future[Unit]
  
  case class NoSuchEntry(id: String) extends RuntimeException
}

case class StoredDto(id: String, content: String)