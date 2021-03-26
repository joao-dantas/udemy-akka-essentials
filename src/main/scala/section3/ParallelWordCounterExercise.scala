package section3

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import section3.ParallelWordCounterExercise.WordCounterMaster.{Initialize, WordCounterReply, WordCounterTask}

object ParallelWordCounterExercise extends App {

  val actorSystem = ActorSystem("parallelWordCounterActorSystem");

  val wordCounterMasterActor = actorSystem.actorOf(Props[WordCounterMaster], "wordCounterMaster")

  wordCounterMasterActor ! Initialize(3)

  wordCounterMasterActor ! "In your example it does not make a difference"
  wordCounterMasterActor ! "So be sure to escape certain characters as needed, like e.g."
  wordCounterMasterActor ! "You can add a guard, i.e. an if and a boolean expression after the pattern:"
  wordCounterMasterActor ! "If you look at the Java implementation you see that the parameter to String#split "
  wordCounterMasterActor ! "Not the answer you're looking for? Browse other questions tagged string scala split or ask your own question."
  wordCounterMasterActor ! "some additional information can be found in the documentation https://docs.scala-lang.org/tour/pattern-matching.html , they didn't fit in my case bu"
  wordCounterMasterActor ! "As a non-answer to the question's spirit, which asked how to incorporate predicates into a match clause, in this case the predicate can be factored out before the match:"

  class WordCounterMaster extends Actor {

    override def receive: Receive = {
      case Initialize(nChildren) => {
        val childrenRefs = for (i <- 1 to nChildren) yield context actorOf(Props[WordCounterWorker], s"wcw_$i")
        context become withChildren(childrenRefs, 0)
      }
    }

    def withChildren(childrenRefs: Seq[ActorRef], currentIndex: Int): Receive = {
      case text: String => {
        childrenRefs(currentIndex) ! WordCounterTask(text)
        currentIndex match {
          case x if x == childrenRefs.size - 1 => context become withChildren(childrenRefs, 0)
          case _ => context become withChildren(childrenRefs, currentIndex + 1 % childrenRefs.size)
        }
      }
      case WordCounterReply(count) => println(s"Counted $count words!")
    }
  }

  object WordCounterMaster {

    case class Initialize(nChildren: Int)

    case class WordCounterTask(text: String)

    case class WordCounterReply(count: Int)

  }

  class WordCounterWorker extends Actor {
    override def receive: Receive = {
      case WordCounterTask(text) => {
        println(s"${context.self.path} counting words of : $text")
        sender() ! WordCounterReply(text.split(" ").size)
      }
    }
  }

}
