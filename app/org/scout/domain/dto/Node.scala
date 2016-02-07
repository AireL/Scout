package org.scout.domain.dto

import java.util.UUID
import org.scout.domain.NodeFactory
import play.api.libs.json._

case class DisplayName(val value: String) extends AnyVal
case class Identity(val value: String) extends AnyVal 

object Identity {
  def apply() : Identity = Identity(UUID.randomUUID().toString)
}

sealed trait AbstractConfig {
  def name: Name
  def description: Description
}
object AbstractConfig {
  implicit lazy val fmt = Json.format[AbstractConfig]
  def unapply(ft: AbstractConfig): Option[(String, JsValue)] = {
    val (prod: String, sub) = ft match {
      case c: Config => (Config.toString(), Json.toJson(c)(Config.configFmt))
      case f: Field => (Field.toString(), Json.toJson(f)(Field.fieldFmt))
    }
    Some(prod -> sub)
  }
  def apply(`class`: String, data: JsValue): AbstractConfig = {
    (`class` match {
     case "Field" => Json.fromJson[Field](data)
     case "Config" => Json.fromJson[Config](data)
    }).get
  }
}
case class Name(val value: String) extends AnyVal
case object Name {
  implicit val nameFmt = Json.format[Name]
}
case class Description(val value: String) extends AnyVal
case object Description {
  implicit val descFmt = Json.format[Description]
}
case class Config(name: Name, description: Description, fields : List[AbstractConfig]) extends AbstractConfig
object Config {
  implicit lazy val absFmt = AbstractConfig.fmt
  implicit val configFmt = Json.format[Config]
}
case class Field(name: Name, description: Description, fType: FieldType, onlyAppend: Boolean) extends AbstractConfig
object Field {
  implicit val fieldTypeFmt = FieldType.fieldTypeFmt
  implicit val fieldFmt = Json.format[Field]
}
sealed trait FieldType
object FieldType {
   implicit val lblFmt = Json.format[Label]
   implicit val drpFmt = Json.format[Dropdown]
   implicit val slc1Fmt = Json.format[SelectOne]
   implicit val slcMFmt = Json.format[SelectMulti]
   implicit val chkFmt = Json.format[Checkbox]
   implicit val radFmt = Json.format[Radio]
  
  def unapply(ft: FieldType): Option[(String, JsValue)] = {
    val (prod: Product, sub) = ft match {
      case Text => (Text.toString(), JsString("typed"))
      case DateTime => (Number.toString(), JsString("typed"))
      case File => (Number.toString(), JsString("typed"))
      case Number => (Number.toString(), JsString("typed"))
      case s: SelectOne => (s, Json.toJson(s))
      case s: SelectMulti => (s, Json.toJson(s))
      case d: Dropdown => (Dropdown.toString(), Json.toJson(d))
      case c: Checkbox => (Checkbox.toString(), Json.toJson(c))
      case r: Radio => (Radio.toString(), Json.toJson(r))
    }
    Some(prod.productPrefix -> sub)
  }

  def apply(`class`: String, data: JsValue): FieldType = {
    (`class` match {
     case "SelectOne" => Json.fromJson[SelectOne](data)
     case "Text" => JsSuccess(Text)
     case "Dropdown" => Json.fromJson[Dropdown](data)
     case "SelectMulti" => Json.fromJson[SelectMulti](data)
     case "Checkbox" => Json.fromJson[Checkbox](data)
     case "Radio" => Json.fromJson[Radio](data)
     case "DateTime" => JsSuccess(DateTime)
     case "File" => JsSuccess(File)
     case "Number" => JsSuccess(Number)
    }).get
  }
  
  val fieldTypeFmt = Json.format[FieldType]
}
case class Label(val label: String) extends AnyVal
case object Text extends FieldType
case class Dropdown(labels: List[Label]) extends FieldType
case class SelectOne(labels: List[Label]) extends FieldType
case class SelectMulti(labels: List[Label]) extends FieldType
case class Checkbox(labels: List[Label]) extends FieldType
case class Radio(labels: List[Label]) extends FieldType
case object DateTime extends FieldType
case object File extends FieldType
case object Number extends FieldType

case class Node(id: String, displayName: String, params: Map[String, String], children: List[String], parent: Option[String])
case object Node {
  implicit val format = Json.format[Node]
  val extensible = "extensible"
}