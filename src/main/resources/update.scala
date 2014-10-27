import org.json4s.JsonAST.JValue

/**
 * Created by Paul on 10/25/2014.
 */
def update(entity: JValue): JValue = {
  val tuple: (String, Int) = "newInt" -> 12
  entity \\ "TestState" = tuple
}
