import scala.reflect.api.JavaUniverse

/**
 * Created by Paul on 10/19/2014.
 */
object ReflectionDefaultParam extends App{

  def getDefaultsFor[A](implicit t: reflect.ClassTag[A]): List[((AnyRef with JavaUniverse#SymbolApi)#NameType,Option[Any])] = {
    import reflect.runtime.{universe => ru, currentMirror => cm}

    val clazz  = cm.classSymbol(t.runtimeClass)
    val mod    = clazz.companion.asModule
    val im     = cm.reflect(cm.reflectModule(mod).instance)
    val ts     = im.symbol.typeSignature
    val mApply = ts.member(ru.TermName("apply")).asMethod
    val syms   = mApply.paramLists.flatten
    val map: List[((AnyRef with JavaUniverse#SymbolApi)#NameType, Option[Any])] = syms.zipWithIndex.map { case (p, i) =>
      val member = ts.member(ru.TermName(s"apply$$default$$${i + 1}"))
      val name: (AnyRef with JavaUniverse#SymbolApi)#NameType = p.name
      if (member.isMethod) {
        val mDef = member.asMethod
        (name, Some(im.reflectMethod(mDef)()))
      } else {
        (name, None)
      }
    }
    map
  }

  case class Foo(bar: Int = 33, baz: Int, bob: Boolean = true)

  val f = getDefaultsFor[Foo]
  println(f)
}
