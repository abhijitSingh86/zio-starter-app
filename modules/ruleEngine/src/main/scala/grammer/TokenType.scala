package grammer

sealed trait TokenType {}

object TokenType {

  final case object Num  extends TokenType
  final case object Text extends TokenType
  final case object Bool extends TokenType
  final case object True extends TokenType
  final case object False extends TokenType
  final case object Identifier extends TokenType
  final case object Nil extends TokenType


  final case object If   extends TokenType
  final case object Else extends TokenType
  final case object And extends TokenType
  final case object Or extends TokenType

  final case object Map    extends TokenType
  final case object Filter extends TokenType
  final case object FilterOr extends TokenType

  final case object Equal extends TokenType
  final case object NotEqual extends TokenType
  final case object Bang extends TokenType
  final case object LessThan extends TokenType
  final case object LessThanEqual extends TokenType
  final case object GreaterThan extends TokenType
  final case object GreaterThanEqual extends TokenType

  final case object Fun extends TokenType
  final case object LeftParan extends TokenType
  final case object RightParan extends TokenType
  final case object LeftBrace extends TokenType
  final case object RightBrace extends TokenType
  final case object Comma extends TokenType
  final case object Dot extends TokenType

  final case object Star extends TokenType
  final case object Minus extends TokenType
  final case object Plus extends TokenType

  final case object Return extends TokenType
  final case object Eof extends TokenType

  def getToken(token:String):Option[TokenType] = {
    val token:TokenType = ???
    token match {
      case Num => ???
      case Text => ???
      case Bool => ???
      case If => ???
      case Else => ???
      case Map => ???
      case Filter => ???
      case FilterOr => ???
      case Equal => ???
      case NotEqual => ???
      case LessThan => ???
      case LessThanEqual => ???
      case GreaterThan => ???
      case GreaterThanEqual => ???
      case Fun => ???
      case LeftParan => ???
      case RightParan => ???
      case LeftBrace => ???
      case RightBrace => ???
      case Comma => ???
      case Dot => ???
      case Star => ???
      case Minus => ???
      case Return => ???
    }
  }


}
