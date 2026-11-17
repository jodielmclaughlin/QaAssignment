package com.example.qaassignment.shared;

import com.example.qaassignment.config.ConfigReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ZipProcessingClient {

    public Response process(String fileName){
        Map<String, String> request = Map.of("fileName", fileName);

        return
                given()
                        .baseUri(ConfigReader.baseUrl())
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .post(ConfigReader.processPath())
                        .then()
                        .extract()
                        .response();

    }
}
