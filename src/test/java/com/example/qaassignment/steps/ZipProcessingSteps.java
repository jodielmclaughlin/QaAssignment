package com.example.qaassignment.steps;

import com.example.qaassignment.config.ConfigReader;
import com.example.qaassignment.support.ScenarioContext;
import com.example.qaassignment.support.TestFileFactory.ArchiveEntry;
import com.example.qaassignment.support.ZipAssertions;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;

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
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        System.out.println(request);

        context.setResponse(
                given()
                        .baseUri(ConfigReader.baseUrl())
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .log()
                        .all()
                        .post(ConfigReader.processPath())
                        .then()
                        .log()
                        .all()
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
        Path file = processingFolder().resolve(expectedFile);
        assertTrue(Files.exists(file), () -> "Expected file to exist" + file);
    }

    @Then("{string} exists in the encoded folder")
    public void existsInTheEncodedFolder(String expectedFile) {
        Path file = encodedFolder().resolve(expectedFile);
        assertTrue(Files.exists(file), () -> "Expected file to exist" + file);
    }

    @Then("{string} exists in the error folder")
    public void existsInTheErrorFolder(String expectedFile) {
        Path file = errorFolder().resolve(expectedFile);
        assertTrue(Files.exists(file), () -> "Expected file to exist" + file);
    }

    @Then("{string} no longer exists in the input folder")
    public void doesNotExistInTheInputFolder(String expectedFile) {
        Path file = inputFolder().resolve(expectedFile);
        assertTrue(Files.notExists(file), () -> "Expected file to not exist" + file);
    }

    @Then("the base64 output decodes to a valid ZIP")
    public void theBase64OutputDecodesToAValidZIP() throws IOException{
        Path encodedPath = encodedOutputPath();
        byte[] decoded = ZipAssertions.decodeBase64(encodedPath);
        ZipAssertions.assertValidZip(decoded);
    }

    @Then("the decoded ZIP matches the processed ZIP")
    public void theDecodedZIPMatchesTheProcessedZIP() throws IOException {
        String processedFile = context.response().jsonPath().getString("processedFile");
        byte[] decoded = ZipAssertions.decodeBase64(encodedOutputPath());
        byte[] processed = Files.readAllBytes(processingFolder().resolve(processedFile));

        assertEquals(processed, decoded);
    }

    @Then("the processed ZIP contains only:")
    public void theProcessedZIPContainsOnly(DataTable table) throws IOException{
        String processedFile = context.response()
                .jsonPath()
                .getString("processedFile");

        List<String> expected = table.asMaps().stream()
                .map(row -> row.get("fileName"))
                .sorted()
                .toList();

        List<String> actual = ZipAssertions
                .entryNames(processingFolder().resolve(processedFile))
                .stream()
                .sorted()
                .toList();

        assertEquals(expected, actual);

    }

    @Then("no processed ZIP has been created")
    public void noProcessedZIPHasBeenCreated() throws IOException{
        try( var files = Files.list(processingFolder())) {
            var filesList = files.filter(Files::isRegularFile).toList();
            assertTrue(filesList.isEmpty());
        }
    }

    @Then("no Base64 output has been created")
    public void noBase64OutputHasBeenCreated() throws IOException{
        try( var files = Files.list(encodedFolder())) {
            var filesList = files.filter(Files::isRegularFile).toList();
            assertTrue(filesList.isEmpty());
        }
    }

    private Path encodedOutputPath(){
        String encodedFile = context.response()
                .jsonPath().getString("encodedFile");
        return encodedFolder().resolve(encodedFile);
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
