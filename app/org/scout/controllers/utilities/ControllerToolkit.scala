package org.scout.controllers.utilities

import play.api.mvc.Controller
import play.api.mvc.Request
import scala.concurrent.Future
import play.api.mvc.AnyContent
import play.api.mvc.Result
import play.api.mvc.Action
import play.api.libs.json.Format
import play.api.libs.json.Json
import scalaz._
import Scalaz._
import scala.concurrent.ExecutionContext

trait ControllerToolkit {
  self: Controller =>
    implicit def executionContext: ExecutionContext
    def handle(func: Kleisli[Future, Request[AnyContent], Result]) = Action.async(func.run)
    private val Clear : Kleisli[Future, Request[AnyContent], Unit] = Kleisli(_ => Future.successful(()))
    def Parse[T : Format] : Kleisli[Future, Request[AnyContent], T] = Kleisli(req =>
      req.body.asJson
        .flatMap(_.asOpt[T])
          .map(Future.successful)
          .getOrElse(Future.failed(IllegalParseAttempt(req.body.toString))))
          
    def Resolve[T : Format] : Kleisli[Future, T, Result] = Kleisli(dto => Future.successful(Ok(Json.toJson(dto).toString())))
    
    def JsonFuturePOST[T : Format, S: Format](func: T => Future[S]) : Action[AnyContent] = 
      handle(Parse[T] andThen Kleisli(func) andThen Resolve[S])
    def JsonPOST[T : Format, S: Format](func: T => S) : Action[AnyContent] = 
      handle(Parse[T] andThen Kleisli(a => Future.successful(func(a))) andThen Resolve[S])
    def FuturePOST[T : Format](func: T => Future[Result]) : Action[AnyContent] = 
      handle(Parse[T] andThen Kleisli(func))
    def POST[T : Format](func: T => Result) : Action[AnyContent] = 
      handle(Parse[T] andThen Kleisli(a => Future.successful(func(a))))
      
    def JsonFutureGET[S: Format](func: => Future[S]) : Action[AnyContent] =
      handle(Clear andThen Kleisli(_ => func) andThen Resolve[S])
    def JsonGET[S: Format](func: => S) : Action[AnyContent] =
      handle(Clear andThen Kleisli(_ => Future.successful(func)) andThen Resolve[S])
    def FutureGET(func: => Future[Result]) : Action[AnyContent] =
      handle(Clear andThen Kleisli(_ => func))
    def GET(func: => Result) : Action[AnyContent] =
      handle(Clear andThen Kleisli(_ => Future.successful(func)))
}

case class IllegalParseAttempt(body: String) extends RuntimeException(s"Failed to parse request with body: $body")