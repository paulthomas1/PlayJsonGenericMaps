package improbable.updates

import com.github.pathikrit.dijon._

import scala.annotation.{StaticAnnotation, Annotation}
import java.lang.annotation.Annotation

/**
 * Copyright (c) 2014 All Right Reserved, Improbable Worlds Ltd.
 * Date: 27/10/2014
 * Summary: 
 */
trait UpdateAllEntities {

  def updateAllEntities(entity: SomeJson): SomeJson

}


case class MigrateUp(date: Long) extends StaticAnnotation
