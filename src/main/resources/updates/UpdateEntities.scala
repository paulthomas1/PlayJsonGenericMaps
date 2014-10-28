import com.github.pathikrit.dijon._
import improbable.updates.{MigrateUp, UpdateAllEntities}

import scala.reflect.runtime.{universe=>ru}

/**
 * Created by Paul on 10/25/2014.
 */
class UpdateEntities extends UpdateAllEntities {

  def updateAllEntities(entity: SomeJson): SomeJson = {
    val simpleClassSymbol = ru.typeOf[UpdateEntities].member(ru.TermName("update"))
    println(simpleClassSymbol.isMethod)
    println(simpleClassSymbol.annotations(0).scalaArgs(0))
    entity
  }

  @MigrateUp(20141027202000L)
  def update(entity: SomeJson): SomeJson = {
    entity.states.TestState.oldInt = 2
    entity.states.NewState = `{}`
    entity.states.NewState.fant = 12
    entity
  }

  @MigrateUp(20141127202000L)
  def updateSomeMore(entity: SomeJson): SomeJson = {
    entity.states.TestState.timeInt = 2
    entity
  }

}