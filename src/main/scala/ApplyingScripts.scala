import nl.typeset.sonofjson._


/**
 * Created by Paul on 10/25/2014.
 */
object ApplyingScripts extends App {

  var json: String = """{
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

  private val parsed = parse(json)
  private val states = parsed.selectDynamic("states")
  private val jObject = states.asInstanceOf[JObject]
  private val smallNameToCanonical = createStateNameToFullMap(jObject.elements.keySet.toSeq)
  smallNameToCanonical.foreach(sTc => {
    json = json.replace(sTc._2, sTc._1)
  })

  private val newParsed = parse(json)
  println(newParsed.selectDynamic("states").selectDynamic("TestState"))


  def createStateNameToFullMap(fullStateNames: Seq[String]): Map[String, String] = {
    fullStateNames.map(x => {
      val split: Array[String] = x.split(".")
      (split(split.length - 1), x)
    }).toMap
  }
//    .selectDynamic("improbable.generated.test.TestState")

}
