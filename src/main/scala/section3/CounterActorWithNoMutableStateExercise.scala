package section3

import akka.actor.{Actor, ActorSystem, Props}
import section3.CounterActorExercise.CounterActor.{Decrement, Increment, Print}

object CounterActorWithNoMutableStateExercise extends App {

  val actorSystem = ActorSystem("counterActorSystem");

  val counterActor = actorSystem.actorOf(Props(CounterActor()), "counterActor")

  (1 to 10).foreach(_ => counterActor ! Increment)
  (1 to 5).foreach(_ => counterActor ! Decrement)
  counterActor ! Print

  case class CounterActor() extends Actor {
    override def receive: Receive = countReceiver(0)

    def countReceiver(currentCount : Int): Receive = {
      case Increment => context.become(countReceiver(currentCount + 1))
      case Decrement => context.become(countReceiver(currentCount - 1))
      case Print => println(s"counter $currentCount")
    }
  }

  object CounterActor {
    case object Increment
    case object Decrement
    case object Print
  }

}
