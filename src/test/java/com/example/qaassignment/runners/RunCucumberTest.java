package com.example.qaassignment.runners;


import org.junit.platform.suite.api.*;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectPackages("features")
@ConfigurationParameters({
        @ConfigurationParameter(key= GLUE_PROPERTY_NAME, value="com.example.qaassignment.steps, com.example.qaassignment.shared"),
        @ConfigurationParameter(key="cucumber.snippet-type", value ="camelcase"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports/report.html, json:target/cucumber-reports/report.json")
})
public class RunCucumberTest {
}
