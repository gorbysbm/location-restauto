import com.google.gson.JsonObject;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.log4testng.Logger;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasXPath;

public class NotesTestHelper extends BaseTestHelper {
    private static final Logger LOGGER = Logger.getLogger(NotesTestHelper.class);

    //*********Helper Methods*********

    public ValidatableResponse setupCloseNoteAndSubmit(String requestPath, String verbType, String noteId, String user, String pswd) {
        RequestSpecification requestSpecification = given()
                .auth()
                //Note: If our company and this provider properly supported Oauth, we can use that as needed for extra security
                .basic(user, pswd)
                .pathParam("noteId", noteId)
                .when();

        return setRequestVerbTypeAndSubmit(verbType ,requestSpecification, requestPath);
    }


    public ValidatableResponse setupInvalidNotesAndSubmit(String path, String lon, String lat, String text) {
        JsonObject requestParams = new JsonObject();
        if(lat != null && lat.length() != 0){
            requestParams.addProperty ("lat", lat);
        }
        if(lon != null && lon.length() != 0){
            requestParams.addProperty ("lon", lon);
        }
        if(text != null && text.length() != 0){
            requestParams.addProperty ("text", text);
        }

        return sendPost(requestParams, path);
    }

    public ValidatableResponse setupValidNoteAndSubmit(String path, String lon, String lat, String noteText) {
        JsonObject requestParams = new JsonObject();
        requestParams.addProperty ("lon", lon);
        requestParams.addProperty ("lat", lat);
        requestParams.addProperty ("text", noteText);
        return sendPost(requestParams, path);
    }

    public ValidatableResponse setupLocationBoundingBoxAndSubmit(String path, String lon, String lat) {
        return given()
                .pathParam("minLon",lon )
                .pathParam("minLat", lat)
                .pathParam("maxLon", lon)
                .pathParam("maxLat", lat)
                .get(path).then();
    }

    //*********Verifications*********
    public String verifyNoteCreatedSuccessfully( String lon, String lat, String noteText, String noteStatus, String noteCommentStatus, String todaysDateUTC, String baseUrlforNewNote, ValidatableResponse response) {
        String noteId = rawResponseToXMLPath(response).get("osm.note.id");
        //Check if note id is a number
        response.body(hasXPath("/osm/note/id[.=number()]"));
        //set noteId for later comparison
        response.body(hasXPath(String.format("/osm/note[@lon='%s']", lon)));
        response.body(hasXPath(String.format("/osm/note[@lat='%s']", lat)));
        response.body(hasXPath(String.format("/osm/note/status[.='%s']", noteStatus)));
        response.body(hasXPath(String.format("/osm/note/url[.='%s']", baseUrlforNewNote + noteId)));
        response.body(hasXPath(String.format("/osm/note/comment_url[.='%s']", baseUrlforNewNote + noteId +"/comment"  )));
        response.body(hasXPath(String.format("/osm/note/close_url[.='%s']", baseUrlforNewNote + noteId +"/close")));
        response.body(hasXPath(String.format("/osm/note/date_created[contains(text(),'%s')]", todaysDateUTC)));
        response.body(hasXPath(String.format("/osm/note/comments/comment/action[.='%s']", noteCommentStatus)));
        response.body(hasXPath(String.format("/osm/note/comments/comment/text[.='%s']",noteText)));
        response.body(hasXPath(String.format("/osm/note/comments/comment/html[contains(text(),'%s')]", noteText)));
        response.body(hasXPath(String.format("/osm/note/comments/comment/date[contains(text(),'%s')]", todaysDateUTC)));
        LOGGER.info(">>Note with this id was created successfully: "+noteId);
        return noteId;
    }

    public void verifyNoteShowsupInBoundingBox(ValidatableResponse response, List<String> noteIds) {
        for (String noteId: noteIds) {
            response.body(hasXPath(String.format("/osm/note/id[.='%s']",noteId)));
        }
    }


}
