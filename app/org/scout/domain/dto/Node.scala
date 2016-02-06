package org.scout.domain.dto

import java.util.UUID
import org.scout.domain.NodeFactory

case class DisplayName(val value: String) extends AnyVal
case class Identity(val value: String) extends AnyVal 

object Identity {
  def apply() : Identity = Identity(UUID.randomUUID().toString)
}

case class Config(fields : List[Field])
case class Name(val value: String) extends AnyVal
case class Field(key: Key, value: Value)
case class Key(val value: String) extends AnyVal
case class Value(val value: String) extends AnyVal