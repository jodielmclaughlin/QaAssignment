package com.example.qaassignment.support;

import io.restassured.response.Response;

public class ScenarioContext {
    private Response response;

    public Response response(){
        if (response == null){
            throw new IllegalStateException("No API response available");
        }
        return response;
    }

    public void setResponse(Response response){
        this.response = response;
    }

    public void reset(){
        response = null;
    }
}
