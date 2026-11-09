package com.example.qaassignment.runners;


import org.junit.platform.suite.api.*;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectPackages("features")
@ConfigurationParameters({
        @ConfigurationParameter(key= GLUE_PROPERTY_NAME, value="steps, shared"),
        @ConfigurationParameter(key="cucumber.snippet-type", value ="camelcase")
})
public class RunCucumberTest {
}
