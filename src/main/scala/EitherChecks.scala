object EitherChecks {
  def main(args: Array[String]): Unit = {
    val badResult: Either[String, Long] = Left("that was a bad error")
    val goodResult: Either[String, Long] = Right(42)

    //DON'T DO THIS: no need to pre-check
    if(badResult.isLeft)
      println(badResult.left.get)

    badResult.left.foreach(println) // simplified version of above

    goodResult.left.foreach(println) //won't execute


    //what about Right ?
    goodResult.right.foreach(println)
    goodResult.foreach(println) //literally the same as above

    // ^^^^^^^^^^^^^^
    // in Scala 2.12 Either is Right-biased,
    // which means you don't need to specify `.right` if
    // you want to map / foreach over it
  }
}
