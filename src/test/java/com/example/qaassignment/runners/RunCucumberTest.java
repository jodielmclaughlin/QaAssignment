package com.example.qaassignment.runners;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
@SelectPackages("features")
@ConfigurationParameters({
        @ConfigurationParameter(key= Constants.GLUE_PROPERTY_NAME, value="steps, shared"),
        @ConfigurationParameter(key="cucumber.snippet-type", value ="camelcase")
})
public class RunCucumberTest {
}
