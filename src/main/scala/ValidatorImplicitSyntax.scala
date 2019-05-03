object PredefinedValidators {
  implicit val positiveIntValidator: Validator[Int] =
    (t: Int) => Either.cond(t > 0, t, "Int is not positive")
}

object ValidatorImplicitSyntax {
  //
  // 1) default implicit parameters are almost never needed
  //



  implicit class IntValidatorSyntax(val value: Int) {
    def validate(implicit t: Validator[Int] = PredefinedValidators.positiveIntValidator): Either[String, Int] = t.validate(value)

    //                                        ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    //                                        shouldn't be here
    //
    // syntax class shouldn't have preferences :)
    // instead import your implicits and syntax sugar where needed


    // import PredefinedValidators.intValidator
    // import ValidatorImplicitSyntax.IntValidatorSyntax
    // 4.validate
  }





  //
  // 2) redundant implicit classes
  //

  implicit class StringValidatorSyntax(val value: String) {
    def validate(implicit t: Validator[String]): Either[String, String] = t.validate(value)
    // syntax class typically contains no actual logic,
    // it just helps you make code more concise
  }


  implicit class PersonValidatorSyntax(val value: Person) {
    //                                            ^^^^^^
    // no need to duplicate it for each type in your app
    def validate(implicit t: Validator[Person]): Either[String, Person] = t.validate(value)
  }

}

object ValidatorData {
  // effectively replaces all of the above
  // implicit t: Validator[T] is the "real" logic
  // http://www.lihaoyi.com/post/ImplicitDesignPatternsinScala.html#type-class-implicits
  implicit class ValidatorSyntax[T](val value: T) {
    def validate(implicit t: Validator[T]): Either[String, T] = t.validate(value)
  }


  implicit val negativeIntValidator: Validator[Int] =
    (t: Int) => Either.cond(t < 0, t, "Int is not negative")

  implicit val customBigDecimalValidator: Validator[BigDecimal] =
    (value: BigDecimal) => Either.cond(value.isWhole(), value, "It's not whole")

  implicit def listValidator[B](implicit validator: Validator[B]): Validator[List[B]] = new Validator[List[B]] {
    override def validate(list: List[B]): Either[String, List[B]] = {
      val validationErrors = list.filter(_.validate.isLeft)
      Either.cond(validationErrors.isEmpty, list, validationErrors.mkString(start = "Validation errors: ", sep = ", ", end = ""))
    }
  }

}

object SyntaxTypeClassApp {

  import ValidatorData._

  def main(args: Array[String]): Unit = {
    //let's validate if these bigdecimals are whole numbers
    val whole: BigDecimal = BigDecimal("4")
    val notWhole: BigDecimal = BigDecimal("4.1")


    val wholeList: List[BigDecimal] = List(BigDecimal("1"), BigDecimal("56"), BigDecimal("324"))
    val mixedList: List[BigDecimal] = List(BigDecimal("1"), BigDecimal("0.11"), BigDecimal("123"), BigDecimal("0.12"))

    println(whole.validate)
    println(notWhole.validate)

    println(wholeList.validate)
    println(mixedList.validate)
  }
}
