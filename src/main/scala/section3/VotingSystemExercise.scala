package section3

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import section3.VotingSystemExercise.Citizen.{VoteStatusReply, VoteStatusRequest}
import section3.VotingSystemExercise.VoteAggregator.AggregateVotes

object VotingSystemExercise extends App {

  val actorSystem = ActorSystem("votingActorSystem");

  case class Vote(candidate: String)

  object Citizen {

    case object VoteStatusRequest

    case class VoteStatusReply(candidate: Option[String])

  }

  class Citizen extends Actor {
    override def receive: Receive = receiver(None)

    def receiver(currentCandidate: Option[String]): Receive = {
      case Vote(candidate: String) => context.become(receiver(Option(candidate)))
      case VoteStatusRequest => sender() ! VoteStatusReply(currentCandidate)
    }
  }

  object VoteAggregator {

    case class AggregateVotes(citizens: Set[ActorRef])

  }

  class VoteAggregator extends Actor {

    override def receive: Receive = awaitingCommand()

    def awaitingCommand(): Receive = {
      case AggregateVotes(citizens: Set[ActorRef]) => {
        citizens.foreach(_ ! VoteStatusRequest)
        context.become(awaitingStatuses(citizens, Map()))
      }
    }

    def awaitingStatuses(stillWaiting: Set[ActorRef], currentStats: Map[String, Int]): Receive = {
      case VoteStatusReply(Some(candidate)) => {
        val newStillWaiting = stillWaiting - sender()
        val currentVotesOfCandidate = currentStats.getOrElse(candidate, 0)
        val newStats = currentStats + (candidate -> (currentVotesOfCandidate + 1))
        if (newStillWaiting.isEmpty) {
          println(s"Final Result: $newStats")
        } else {
          context.become(awaitingStatuses(newStillWaiting, newStats))
        }
      }
    }
  }

  val joao = actorSystem.actorOf(Props[Citizen])
  val mariana = actorSystem.actorOf(Props[Citizen])
  val bastter = actorSystem.actorOf(Props[Citizen])
  val artur = actorSystem.actorOf(Props[Citizen])
  val gui = actorSystem.actorOf(Props[Citizen])

  joao ! Vote("biro")
  mariana ! Vote("trump")
  bastter ! Vote("trump")
  artur ! Vote("biro")
  gui ! Vote("biro")

  val voteAggregator = actorSystem.actorOf(Props[VoteAggregator])
  voteAggregator ! AggregateVotes(Set(joao, mariana, bastter, artur, gui))
}
