package improbable.updates

import java.io.File
import java.util

import com.github.pathikrit.dijon._
import com.googlecode.scalascriptengine.ScalaScriptEngine
import com.twitter.util.Eval

import scala.collection.JavaConverters._
import scala.reflect.runtime.universe._
import scala.reflect.runtime.{currentMirror => cm, universe => ru}

/**
 * Created by Paul on 10/25/2014.
 */
object ApplyingScripts extends App {

  def getCurrentSnapshotJson(): String = """{
          "prefab":{
            "name":"Prefab"
          },
          "behaviours":["improbable.generated.test.Test"],
          "states":{
            "improbable.generated.test.TestState":{
            "testInt":123
            },
            "improbable.generated.test.TestPosition":{
            "value":{
              "x":1.0,
              "y":2.0,
              "z":3.0
              }
            },
            "improbable.generated.test.TestState3":{
              "testEnum":"improbable.generated.test.TestEnum.Value2"
            }
          }
        }"""



  def createShortStateNameToFullMap(json: String): Map[String, String] = {
    val parsed = JSON_PARSER.parseJSON(json)
    val states = parsed.states
    val keySet = states.keySet
    createShortStateNameToFullMap(keySet)
  }

  def replaceFullNameWithSmall(originalJson: String, smallNameToFull: Map[String, String]): String = {
    var newJson = originalJson
    smallNameToCanonical.foreach(sTc => {
      newJson = newJson.replace(sTc._2, sTc._1)
    })
    newJson
  }

  def replaceSmallNameWithFull(originalJson: String, smallNameToFull: Map[String, String]): String = {
    var newJson = originalJson
    smallNameToCanonical.foreach(sTc => {
      newJson = newJson.replace(sTc._1, sTc._2)
    })
    newJson
  }

  var jsonSnapShot = getCurrentSnapshotJson()
  val smallNameToCanonical = createShortStateNameToFullMap(jsonSnapShot: String)
  val smallStateNameJson = replaceFullNameWithSmall(jsonSnapShot, smallNameToCanonical)
  val parsedJson = parse(smallStateNameJson)
  var migratedSnapshot = applyMigrationScripts(parsedJson)
  val newSnapshot = migratedSnapshot.toString()
  val newSnapshotWithFullNames = replaceSmallNameWithFull(newSnapshot, smallNameToCanonical)
  println(newSnapshotWithFullNames)



  def applyMigrationScripts(json: SomeJson): SomeJson = {
    val resource = this.getClass().getClassLoader().getResource("updates/")
    val file = new File(resource.toURI())
    val sse = ScalaScriptEngine.onChangeRefresh(new File(resource.toURI()))
    sse.refresh
    file.listFiles().foreach(f => googleProcessEachFile(f, sse, json))
    json
  }

  def googleProcessEachFile(f: File, sse: ScalaScriptEngine, json: SomeJson): Unit = {
    val shortName = f.getName.split('.')(0)
    val entities = sse.newInstance[UpdateAllEntities](shortName)
    val mirror = cm
    val instanceMirror = mirror.reflect(entities)

    val clazz = MigrateUp(32L)
    val classOfMigration = instanceMirror.symbol
    val typeOfMigration = classOfMigration.toType
    val allMethodsInMigrationScript = typeOfMigration.members.filter(_.isMethod)
    val methodsWithAnnotation = allMethodsInMigrationScript.filter(x => x.annotations.filter(a => {
      a.tree.tpe.typeSymbol == mirror.classSymbol(clazz.getClass)
    }).size > 0)
    methodsWithAnnotation.foreach(m =>
      instanceMirror.reflectMethod(m.asMethod)(json)
    )
    println("=========")
    println(json)
    println("=========")
    json
  }

  def processEachFile(f :File): Unit = {
    val eval = new Eval
    val entities = eval[UpdateAllEntities](f)
    val mirror = ru.runtimeMirror(getClass.getClassLoader())
    val instanceMirror = mirror.reflect(entities)
    val clazz = MigrateUp(32L)
    val jimClass = instanceMirror.symbol
    val jimType = jimClass.toType
    val allMethods: Iterable[Symbol] = jimType.members.filter(_.isMethod)
    val withAnno = allMethods.filter(x => x.annotations.filter(a => {
      a.tree.tpe.typeSymbol == mirror.classSymbol(clazz.getClass);
    }).size > 0)
    withAnno.foreach(m =>
      instanceMirror.reflectMethod(m.asMethod)(jsonSnapShot)
    )
    println("=========")
    println(jsonSnapShot)
    println("=========")
    jsonSnapShot
  }

  def createShortStateNameToFullMap(fullStateNames: util.Set[String]): Map[String, String] = {
    fullStateNames.asScala.map(x => {
      val split: Array[String] = x.split('.')
      (split(split.length - 1), x)
    }).toMap
  }

}