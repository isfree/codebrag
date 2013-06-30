package com.softwaremill.codebrag.rest

import com.softwaremill.codebrag.service.user.Authenticator
import org.scalatra.swagger.{SwaggerSupport, Swagger}
import org.scalatra.json.JacksonJsonSupport
import com.softwaremill.codebrag.dao.reporting._
import scala.Some
import org.bson.types.ObjectId
import com.softwaremill.codebrag.dao.reporting.views.{FollowupView, FollowupListView}
import com.softwaremill.codebrag.service.followups.FollowupService
import org.scalatra.NotFound

class FollowupsServlet(val authenticator: Authenticator,
                       val swagger: Swagger,
                       followupFinder: FollowupFinder,
                       followupService: FollowupService)
  extends JsonServletWithAuthentication with FollowupsServletSwaggerDefinition with JacksonJsonSupport {

  get("/", operation(getOperation)) {
    haltIfNotAuthenticated()
    followupFinder.findAllFollowupsForUser(new ObjectId(user.id))
  }

  get("/:id", operation(getSingleOperation)) {
    haltIfNotAuthenticated()
    val followupId = params("id")
    followupFinder.findFollowupForUser(new ObjectId(user.id), new ObjectId(followupId)) match {
      case Right(followup) => followup
      case Left(msg) => NotFound(msg)
    }
  }

  delete("/:id", operation(dismissOperation)) {
    haltIfNotAuthenticated()
    val followupId = params("id")
    followupService.deleteUserFollowup(new ObjectId(user.id), new ObjectId(followupId))
  }
}

trait FollowupsServletSwaggerDefinition extends SwaggerSupport {

  override protected val applicationName = Some(FollowupsServlet.MappingPath)
  protected val applicationDescription: String = "Follow-ups endpoint"

  val getOperation = apiOperation[FollowupListView]("get")
  val getSingleOperation = apiOperation[FollowupView]("get")
  val dismissOperation = apiOperation[Unit]("dismiss")
    .summary("Deletes selected follow-up for current user")
    .parameter(pathParam[String]("id").description("Commit identifier").required)
}

object FollowupsServlet {
  val MappingPath = "followups"
}