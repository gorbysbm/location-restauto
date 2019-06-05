import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;

import java.util.HashMap;

public class Verifications {

    public void verifyResponseValues(ValidatableResponse response, int statusCode, ContentType contentType){
        response
                .assertThat()
                .statusCode(statusCode)
                .and()
                .assertThat()
                .contentType(contentType);
    }

    public void verifyHeaders(ValidatableResponse response, HashMap<String, String> headers){
        headers.entrySet().stream().forEach(item ->
                response
                        .assertThat()
                        .header(item.getKey(),item.getValue()));

    }
}
