import com.google.gson.JsonObject;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;

public class BaseTestHelper {

    public String generateStringFromResource(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    public ValidatableResponse sendPost(JsonObject requestParams, String path) {
        return given()
                .body(requestParams.toString())
                .contentType(ContentType.JSON)
                .post(path)
                .then();
    }

    public XmlPath rawResponseToXMLPath(ValidatableResponse response) {
        return response.extract().xmlPath();
    }

    public JsonPath rawResponseToJsonPath(ValidatableResponse response) {
        return response.extract().jsonPath();
    }

    public ValidatableResponse setRequestVerbTypeAndSubmit(String verbType, RequestSpecification requestSpecification, String path) {
        ValidatableResponse response;
        switch(verbType){
            case "GET":
                response = requestSpecification.get(path).then();
                break;
            case "POST":
                response = requestSpecification.post(path).then();
                break;
            case "PUT":
                response = requestSpecification.put(path).then();
                break;
            case "PATCH":
                response = requestSpecification.patch(path).then();
                break;
            case "DELETE":
                response = requestSpecification.delete(path).then();
                break;
            default:
                throw new IllegalArgumentException("Unsupported Verb Type: "+ verbType);
        }
        return response;
    }
}
