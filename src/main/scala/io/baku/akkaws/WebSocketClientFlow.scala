package io.baku.akkaws

import akka.actor.ActorSystem
import akka.Done
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ws._

import scala.concurrent.{ExecutionContext, Future}

class WebSocketClientFlow(implicit val mat: ActorMaterializer, sys: ActorSystem, ec: ExecutionContext) {

  def run() = {

    // Future[Done] is the materialized value of Sink.foreach,
    // emitted when the stream completes
    val incoming: Sink[Message, Future[Done]] =
    Sink.foreach[Message] {
      case message: TextMessage.Strict =>
        println("WS client got: "+message.text)
    }

    // send this as a message over the WebSocket
    //val outgoing = Source.fromIterator(() => Iterator.from(0).map(f => TextMessage("WS CLIENT: "+f.toString)))
    val outgoing =  Source.single(TextMessage("WS CLIENT: hello world!"))

    // flow to use (note: not re-usable!)
    val webSocketFlow = Http().webSocketClientFlow(WebSocketRequest("ws://localhost:8080/ws-echo"))

    // the materialized value is a tuple with
    // upgradeResponse is a Future[WebSocketUpgradeResponse] that
    // completes or fails when the connection succeeds or fails
    // and closed is a Future[Done] with the stream completion from the incoming sink
    val (upgradeResponse, closed) =
    outgoing
      .viaMat(webSocketFlow)(Keep.right) // keep the materialized Future[WebSocketUpgradeResponse]
      .toMat(incoming)(Keep.both) // also keep the Future[Done]
      .run()

    // just like a regular http request we can access response status which is available via upgrade.response.status
    // status code 101 (Switching Protocols) indicates that server support WebSockets
    val connected = upgradeResponse.flatMap { upgrade =>
      if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
        Future.successful(Done)
      } else {
        throw new RuntimeException(s"WS CLIENT: Connection failed: ${upgrade.response.status}")
      }
    }

    connected.onComplete(println)
  }

}