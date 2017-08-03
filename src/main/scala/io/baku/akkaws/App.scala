package io.baku.akkaws

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow

object App extends App {

  implicit val actorSystem = ActorSystem("akka-system")
  implicit val flowMaterializer = ActorMaterializer()

  val interface = "localhost"
  val port = 8080

  import akka.http.scaladsl.server.Directives._

  val echoService: Flow[Message, Message, _] = Flow[Message].map {
    case TextMessage.Strict(txt) => TextMessage("App sending ECHO: " + txt)
    case _ => TextMessage("App sending Message type unsupported")
  }

  val route = get {
    pathEndOrSingleSlash {
      complete("Welcome to websocket server")
    }
  } ~
    path("ws-echo") {
      get {
        handleWebSocketMessages(echoService)
      }
    }

  val binding = Http().bindAndHandle(route, interface, port)
  println(s"App sending Server is now online at http://$interface:$port\nPress RETURN to stop...")

  import actorSystem.dispatcher
  new WebSocketClientFlow().run()

}
