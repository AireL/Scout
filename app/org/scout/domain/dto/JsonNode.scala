package org.scout.domain.dto

import play.api.libs.json.Json

case class JsonNode(id: String, displayName: String, params: Map[String, String], children: List[JsonNode], parent: Option[String])
case object JsonNode {
  implicit val format = Json.format[JsonNode]
  val extensible = "Extensible"
}