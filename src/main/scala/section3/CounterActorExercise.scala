package section3

import akka.actor.{Actor, ActorSystem, Props}
import section3.CounterActorExercise.CounterActor.{Decrement, Increment, Print}

object CounterActorExercise extends App {

  val actorSystem = ActorSystem("counterActorSystem");

  val counterActor = actorSystem.actorOf(Props(CounterActor()), "counterActor")

  (1 to 10).foreach(_ => counterActor ! Increment)
  (1 to 5).foreach(_ => counterActor ! Decrement)
  counterActor ! Print

  case class CounterActor() extends Actor {

    var counter = 0

    override def receive: Receive = {
      case Increment => counter += 1
      case Decrement => counter -= 1
      case Print => println(s"Counter $counter")
    }
  }

  object CounterActor {

    case object Increment

    case object Decrement

    case object Print

  }

}
