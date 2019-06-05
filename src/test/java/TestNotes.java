import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import util.StringUtilities;

import java.io.IOException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestNotes extends BaseTests{

    TestNotes(){
    }

    NotesTestHelper notesTestHelper;
    String notesResource = "/api/0.6/notes";
    List<String> notesAdded = new ArrayList<>();

    //*********Tests*********

    @DataProvider(name="getInvalidNotesDataProvider")
    public Object[][] getInvalidNotesDataProvider() throws IOException {
        return getDataProvider("testNoteIsNotCreatedDueToInvalidParams");
    }

    @Test(dataProvider = "getInvalidNotesDataProvider", groups = {"functional"}
    , description = "Try to submit a note with invalid or missing params")
    public void testNoteIsNotCreatedDueToInvalidParams(String description,
            String lon, String lat, String text, String statusCode, String errorMessage) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Error", errorMessage);

        ValidatableResponse response  = notesTestHelper.setupInvalidNotesAndSubmit(notesResource, lon, lat, text);
        verifyHeaders(response, headers);
        verifyResponseValues(response, Integer.parseInt(statusCode), ContentType.TEXT);
    }

    @DataProvider(name="getTestNoteIsCreated")
    public Object[][] getValidNotesDataprovider() throws IOException {
        return getDataProvider("testNoteIsCreated");
    }

    @Test(dataProvider = "getTestNoteIsCreated", groups = {"smoke", "functional"}
            , description = "Add valid notes and verify response contents")
    public void testNoteIsCreated(String description,String lon, String lat, String noteText,
                                    String statusCode, String noteStatus, String noteCommentStatus, String contentType) {
        String todaysDateUTC = StringUtilities.getTodaysDateTimeFormatted("yyyy-MM-dd", ZoneOffset.UTC);
        String baseUrlforNewNote = RestAssured.baseURI+notesResource+"/";

        ValidatableResponse response = notesTestHelper.setupValidNoteAndSubmit(notesResource, lon, lat, noteText);
        verifyResponseValues(response, Integer.parseInt(statusCode),  ContentType.fromContentType(contentType));
        String noteId = notesTestHelper.verifyNoteCreatedSuccessfully(lon, lat, noteText, noteStatus, noteCommentStatus
                , todaysDateUTC, baseUrlforNewNote, response);
        notesAdded.add(noteId);
    }

    @DataProvider(name="getTestVerifyNotePresentForGeoLocation")
    public Object[][] getNotePresentForGeoLocationDataprovider() throws IOException {
        return getDataProvider("testVerifyNotePresentForGeoLocation");
    }

    @Test(dependsOnMethods = "testNoteIsCreated", dataProvider = "getTestVerifyNotePresentForGeoLocation",
            groups = {"smoke", "functional"} , description = "Verify that notes are present in geo location bounding box")
    private void testVerifyNotePresentForGeoLocation(String description, String lon, String lat,
                                                     String statusCode, String respContentType) {
        String requestPath = notesResource+"?bbox={minLon},{minLat},{maxLon},{maxLat}";

        ValidatableResponse response = notesTestHelper.setupLocationBoundingBoxAndSubmit(requestPath, lon, lat);
        verifyResponseValues(response, Integer.parseInt(statusCode), ContentType.fromContentType(respContentType));
        notesTestHelper.verifyNoteShowsupInBoundingBox(response , notesAdded);
    }

    @DataProvider(name="getTestCloseNote")
    public Object[][] getTestCloseNoteDataProvider() throws IOException {
        return getDataProvider("testCloseNote");
    }

    @Test(invocationCount = 1, dependsOnMethods = "testNoteIsCreated", dataProvider = "getTestCloseNote"
            , groups = {"smoke", "functional"}, description = "Close notes that were created in the current test suite run")
    public void testCloseNote(String description, String verbType, String statusCode, String user
            , String pswd, String respContentType){
        String requestPath = notesResource + "/{noteId}" + "/close";

        for (String noteId: notesAdded) {
            ValidatableResponse response = notesTestHelper.setupCloseNoteAndSubmit(requestPath, verbType, noteId, user, pswd);
            verifyResponseValues(response, Integer.parseInt(statusCode), ContentType.fromContentType(respContentType));
        }
    }

    @DataProvider(name="getTestCloseExistingNote")
    public Object[][] getTestCloseExistingNoteDataProvider() throws IOException {
        return getDataProvider("testCloseExistingNote");
    }

    @Test( invocationCount = 1, dataProvider = "getTestCloseExistingNote", groups = {"functional"}
            , description = "Try to close previously created notes ")
    public void testCloseExistingNote(String description, String verbType, String noteId, String statusCode, String user
            , String pswd, String respContentType){
        String requestPath = notesResource + "/{noteId}" + "/close";

        ValidatableResponse response = notesTestHelper.setupCloseNoteAndSubmit(requestPath, verbType, noteId, user, pswd);
        verifyResponseValues(response, Integer.parseInt(statusCode), ContentType.fromContentType(respContentType));
    }

    //*********Page Methods*********

    @Override
    public void initTestHelperObjects() {
        notesTestHelper = new NotesTestHelper();
    }

}
