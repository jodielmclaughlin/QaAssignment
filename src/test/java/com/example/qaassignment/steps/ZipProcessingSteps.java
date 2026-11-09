package com.example.qaassignment.steps;

import com.example.qaassignment.config.ConfigReader;
import com.example.qaassignment.support.ScenarioContext;
import com.example.qaassignment.support.TestFileFactory.ArchiveEntry;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.example.qaassignment.config.ConfigReader.*;
import static com.example.qaassignment.support.TestFileFactory.createZip;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZipProcessingSteps {
    private final ScenarioContext context;

    public ZipProcessingSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("the ZIP processing folders exist")
    public void theZIPProcessingFoldersExist() throws IOException {
        Files.createDirectories(inputFolder());
        Files.createDirectories(processingFolder());
        Files.createDirectories(encodedFolder());
        Files.createDirectories(errorFolder());
    }

    @Given("the ZIP processing folders are empty")
    public void theZIPProcessingFoldersAreEmpty() throws IOException{
        emptyFolder(inputFolder());
        emptyFolder(processingFolder());
        emptyFolder(encodedFolder());
        emptyFolder(errorFolder());
    }

    @Given("ZIP file named {string} containing supported files exists in the input folder")
    public void aZIPContainingSupportedFilesExistsInTheInputFolder(String zipName, DataTable table) throws IOException {
        List<ArchiveEntry> entries = table.asMaps().stream()
                .map(row -> new ArchiveEntry(
                        row.get("fileName"),
                        row.get("type")
                ))
                .toList();
        createZip(inputFolder(), zipName, entries);
    }

    @Given("a ZIP file named {string} contains a TXT file with:")
    public void aZIPContainingATXTFileExistsInTheInputFolder(String zipName, DataTable table) throws IOException {
        List<ArchiveEntry> entries = table.asMaps().stream()
                .map(row -> new ArchiveEntry(
                        row.get("fileName"),
                        row.get("type")
                ))
                .toList();
        createZip(inputFolder(), zipName, entries);
    }

    @Given("a ZIP file named {string} containing an unsupported file with:")
    public void aZIPContainingAnUnsupportedFileExistsInTheInputFolder(String zipName, DataTable table) throws IOException {
        List<ArchiveEntry> entries = table.asMaps().stream()
                .map(row -> new ArchiveEntry(
                        row.get("fileName"),
                        row.get("type")
                ))
                .toList();
        createZip(inputFolder(), zipName, entries);
    }

    @Given("a ZIP file named {string} containing a supported file exists in the input folder")
    public void aZIPContainingOneSupportedFilesExistsInTheInputFolder(String zipName, DataTable table) throws IOException {
        List<ArchiveEntry> entries = table.asMaps().stream()
                .map(row -> new ArchiveEntry(
                        row.get("fileName"),
                        row.get("type")
                ))
                .toList();
        createZip(inputFolder(), zipName, entries);
    }

    @When("the ZIP file {string} is submitted for processing")
    public void theZIPIsSubmittedForProcessing(String fileName) {
        Map<String, String> request = Map.of("fileName", fileName);

        context.setResponse(
                given()
                        .baseUri(ConfigReader.baseUrl())
                        .contentType("application/json")
                        .body(request)
                        .when()
                        .post(ConfigReader.processPath())
                        .then()
                        .extract()
                        .response()
        );
    }

    @Then("the API response code is {int}")
    public void theAPIReturnsStatus(int status) {
        assertEquals(status, context.response().statusCode());
    }

    @Then("the response status is {string}")
    public void theResponseStatusIs(String expectedStatus) {
        assertEquals(expectedStatus, context.response().jsonPath().getString("status"));
    }

    @Then("the response message is {string}")
    public void theResponseMessageIs(String expectedMessage) {
        assertEquals(expectedMessage, context.response().jsonPath().getString("message"));
    }

    @Then("the response source file is {string}")
    public void sourceFileIs(String expectedFile) {
        assertEquals(expectedFile, context.response().jsonPath().getString("sourceFile"));
    }

    @Then("the response processed file is {string}")
    public void theResponseProcessedFileIs(String expectedFile) {
        assertEquals(expectedFile, context.response().jsonPath().getString("processedFile"));
    }

    @Then("the response encoded file is {string}")
    public void theResponseEncodedFileIs(String expectedFile) {
        assertEquals(expectedFile, context.response().jsonPath().getString("encodedFile"));
    }

    @Then("the response unsupported file name is {string}")
    public void theResponseUnsupportedFileNameIs(String expectedFile) {
        assertEquals(expectedFile, context.response().jsonPath().getString("fileName"));
    }

    @Then("{string} exists in the processed folder")
    public void existsInTheProcessedFolder(String expectedFile) {

    }

    @Then("{string} exists in the encoded folder")
    public void existsInTheEncodedFolder(String expectedFile) {

    }

    @Then("{string} exists in the error folder")
    public void existsInTheErrorFolder() {

    }

    @Then("{string} no longer exists in the input folder")
    public void doesNotExistInTheInputFolder() {

    }

    @Then("the base64 output decodes to a valid ZIP")
    public void theBase64OutputDecodesToAValidZIP() {

    }

    @Then("the decoded ZIP matches the processed ZIP")
    public void theDecodedZIPMatchesTheProcessedZIP() {

    }

    @Then("the API throws an exception")
    public void theAPIThrowsAnException() {

    }

    @Then("the processed ZIP contains only:")
    public void theProcessedZIPContainsOnly(){

    }

    @Then("the original ZIP no longer exists in the input folder")
    public void originalZIPNoLongerExistsInTheInputFolder(){

    }

    @Then("no processed ZIP has been created")
    public void noProcessedZIPHasBeenCreated(){

    }

    @Then("no Base64 output has been created")
    public void noBase64OutputHasBeenCreated(){

    }


    private static void emptyFolder(Path folder) throws IOException{
        if(!Files.exists(folder)){
            return;
        }
        try(var paths = Files.walk(folder)) {
            for(Path path: paths
                    .sorted(Comparator.reverseOrder())
                    .filter(candidate -> !candidate.equals(folder))
                    .toList()){
                    Files.deleteIfExists(path);
            }
        }
    }
}
