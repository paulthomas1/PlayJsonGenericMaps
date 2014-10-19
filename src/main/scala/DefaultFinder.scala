import scala.language.experimental.macros
import scala.reflect.macros.Context

object DefaultFinder {
  def default: Any = macro default_impl

  def default_impl(c: Context): c.Expr[Any] = {
    import c.universe._

    c.enclosingUnit.body.collect {
      case a @ Apply(Select(New(classId), nme.CONSTRUCTOR), args) =>
        args.collectFirst {
          case AssignOrNamedArg(Ident(argName), rhs)
            if rhs.exists(_.pos == c.macroApplication.pos) =>
            val tpe = c.typeCheck(tree = a, withMacrosDisabled = true).tpe

            tpe.declarations.collect {
              case m: MethodSymbol if m.isConstructor =>
                m.paramss.headOption.flatMap { params =>
                  params.indexWhere(_.name == argName) match {
                    case -1 => None
                    case i =>
                      import scala.reflect.internal.{
                      Definitions,
                      SymbolTable,
                      StdNames
                      }

                      val u = c.universe.asInstanceOf[
                        Definitions with SymbolTable with StdNames
                        ]

                      val getter = u.nme.defaultGetterName(
                        u.nme.CONSTRUCTOR,
                        i + 1
                      )

                      Some(
                        c.Expr[Any](
                          Select(
                            Ident(tpe.typeSymbol.companionSymbol),
                            newTermName(getter.encoded)
                          )
                        )
                      )
                  }
                }
            }.headOption.flatten
        }.flatten
    }.headOption.flatten.getOrElse(
        c.abort(c.enclosingPosition, "Sorry, you can't use default here!")
      )
  }
}