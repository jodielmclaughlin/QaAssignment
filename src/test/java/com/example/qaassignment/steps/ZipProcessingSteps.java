package com.example.qaassignment.steps;

import com.example.qaassignment.shared.ZipProcessingClient;
import com.example.qaassignment.support.FileSystemSupport;
import com.example.qaassignment.support.ScenarioContext;
import com.example.qaassignment.support.TestFileFactory.ArchiveEntry;
import com.example.qaassignment.support.ZipAssertions;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.example.qaassignment.config.ConfigReader.*;
import static com.example.qaassignment.support.TestFileFactory.createZip;
import static org.junit.jupiter.api.Assertions.*;

public class ZipProcessingSteps {
    private final ScenarioContext context;
    private final ZipProcessingClient client;

    public ZipProcessingSteps(ScenarioContext context, ZipProcessingClient client) {
        this.context = context;
        this.client = client;
    }

    private List<Path> processingFolders() {
        return List.of(
                inputFolder(),
                processedFolder(),
                encodedFolder(),
                errorFolder()
        );
    }

    private Path folder (String folderName){
        return switch (folderName.toLowerCase()) {
            case "input" -> inputFolder();
            case "processed" -> processedFolder();
            case "encoded" -> encodedFolder();
            case "error" -> errorFolder();

            default -> throw new IllegalArgumentException("Unknown processing folder: " + folderName);
        };
    }

    @Given("the ZIP processing folders exist")
    public void theZIPProcessingFoldersExist() throws IOException {
        FileSystemSupport.createDirectories(processingFolders());
    }

    @Given("the ZIP processing folders are empty")
    public void theZIPProcessingFoldersAreEmpty() throws IOException{
        for(Path folder : processingFolders()){
        FileSystemSupport.emptyDirectory(folder);
        }
    }

    @Given("{string} does not exist in the input folder")
    public void missingFileDoesNotExistInTheInputFolder(String missingFile) throws IOException{
        Path filePath = inputFolder().resolve(missingFile);

        Files.deleteIfExists(filePath);

        assertFalse(
                Files.exists(filePath),
                "Expected file not to exist in input folder: " + filePath
        );
    }

    @Given("a corrupt ZIP file named {string} exists in the input folder")
    public void corruptZIPFileExistInTheInputFolder(String corruptFile) throws IOException{
        Files.write(
                inputFolder().resolve(corruptFile),
                "this is not a ZIP file".getBytes(StandardCharsets.UTF_8)
        );
    }

    @Given("a ZIP file named {string} exists in the input folder with:")
    public void zipExistsInTheInputFolder(String zipName, DataTable table) throws IOException{
        List<ArchiveEntry> entries = table.asMaps().stream()
                .map(row -> new ArchiveEntry(
                        row.get("fileName"),
                        row.get("type")
                ))
                .toList();
                createZip(inputFolder(), zipName,entries);
    }


    @When("the ZIP file {string} is submitted for processing")
    public void theZIPIsSubmittedForProcessing(String fileName) {
        context.setResponse(client.process(fileName));
    }

    @Then("the API response code is {int}")
    public void theAPIReturnsStatus(int status) {
        assertEquals(status, context.response().statusCode(), () -> "Expected HTTP response to be " + status +
                ", but was " + context.response().statusCode());
    }

    @Then("the response status is {string}")
    public void theResponseStatusIs(String expectedStatus) {
        assertEquals(expectedStatus, context.response().jsonPath().getString("status"), () ->
                "Expected HTTP response to be " + expectedStatus + ", but was " +
                        context.response().jsonPath().getString("status"));
    }

    @Then("the response message is {string}")
    public void theResponseMessageIs(String expectedMessage) {
        assertEquals(expectedMessage, context.response().jsonPath().getString("message"), "Expected HTTP response to be " + expectedMessage + ", but was " +
                context.response().jsonPath().getString("message"));
    }

    @Then("the response source file is {string}")
    public void sourceFileIs(String expectedFile) {
        assertEquals(expectedFile, context.response().jsonPath().getString("sourceFile"), "Expected HTTP response to be " + expectedFile + ", but was " +
                context.response().jsonPath().getString("sourceFile"));
    }

    @Then("the response processed file is {string}")
    public void theResponseProcessedFileIs(String expectedFile) {
        assertEquals(expectedFile, context.response().jsonPath().getString("processedFile"), "Expected HTTP response to be " + expectedFile + ", but was " +
                context.response().jsonPath().getString("processedFile"));
    }

    @Then("the response encoded file is {string}")
    public void theResponseEncodedFileIs(String expectedFile) {
        assertEquals(expectedFile, context.response().jsonPath().getString("encodedFile"), "Expected HTTP response to be " + expectedFile + ", but was " +
                context.response().jsonPath().getString("encodedFile"));
    }

    @Then("the response unsupported file name is {string}")
    public void theResponseUnsupportedFileNameIs(String expectedFile) {
        String actualFile = context.response().jsonPath().getString("details.unsupportedFiles[0]");
        assertEquals(expectedFile, actualFile, "Expected file name to be " + expectedFile + "but was " + actualFile);
    }

    @Then("the response unsupported file names are:")
    public void theResponseUnsupportedFileNamesAre(DataTable table) {
        List<String> expected = table.asMaps().stream()
                .map(row -> row.get("fileName"))
                .sorted()
                .toList();

        List<String> actual = context.response()
                        .jsonPath()
                        .getList("details.unsupportedFiles", String.class)
                        .stream()
                        .sorted()
                        .toList();

        assertEquals(expected, actual, "Expected unsupported file names to contain " + expected + "but actually contains " + actual);

    }

    @Then("{string} exists in the {word} folder")
    public void existsInTheFolder(String expectedFile, String folderName) {
        Path file = folder(folderName).resolve(expectedFile);
        assertTrue(Files.isRegularFile(file), () -> "Expected file to exist" + file);
    }

    @Then("{string} no longer exists in the {word} folder")
    public void doesNotExistInTheInputFolder(String expectedFile, String folderName) {
        Path file = folder(folderName).resolve(expectedFile);
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
        byte[] processed = Files.readAllBytes(processedFolder().resolve(processedFile));

        assertArrayEquals(processed, decoded, "Expected decoded ZIP to match processed ZIP, but it does not.");
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
                .entryNames(processedFolder().resolve(processedFile))
                .stream()
                .sorted()
                .toList();

        assertEquals(expected, actual, "Expected processed ZIP to contain " + expected + "but actually contains " + actual );


    }

    @Then("the {word} folder contains no files")
    public void folderContainsNoFiles(String folderName) throws IOException{
        Path folder = folder(folderName);
        List<Path> files = FileSystemSupport.regularFiles(folder);
        assertTrue(files.isEmpty(), () -> "Expected no files in " + folder + ", but found: " + files);
    }

    private Path encodedOutputPath(){
        String encodedFile = context.response()
                .jsonPath().getString("encodedFile");
        return encodedFolder().resolve(encodedFile);
    }

}
