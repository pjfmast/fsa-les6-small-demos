package avans.avd.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import java.io.File


fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Welcome to FSA demo auth and image upload/static content!")
        }

        staticFiles(
            remotePath = "/images",
            dir = File("uploads"),
        ) {
            default("incident.png")
            extensions("jpg", "png", "jpeg", "gif")
        }


        post("/images") {

            var imageDescription = ""
            var imageFileName = ""

            val multipartData = call.receiveMultipart()
            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        imageDescription = part.value
                    }
                    is PartData.FileItem -> {
                        imageFileName = part.originalFileName ?: "NoImageName"
                        val fileBytes = part.provider().toByteArray()
                        File(getImageUploadPath(imageFileName)).writeBytes(fileBytes)
                    }

                    else                 -> {}
                }
                part.dispose()
            }
            call.respond(
                HttpStatusCode.OK,
                "$imageDescription is uploaded to ${getImageUploadPath(imageFileName)}"
            )
        }
    }

}

//must match with the path location of the uploads dir
private fun getImageUploadPath(imagefile: String) = "uploads/$imagefile"
