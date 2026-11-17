package com.example.qaassignment.steps;

import com.example.qaassignment.shared.ZipProcessingClient;
import com.example.qaassignment.support.ScenarioContext;
import com.example.qaassignment.support.TestFileFactory;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.example.qaassignment.config.ConfigReader.*;
import static com.example.qaassignment.support.TestFileFactory.createZip;
import static org.junit.jupiter.api.Assertions.*;

public class MetadataFileContentsSteps {

    private final ScenarioContext context;
    private final ZipProcessingClient client;
    private Document xmlDocument;

    public MetadataFileContentsSteps(ScenarioContext context, ZipProcessingClient client) {
        this.context = context;
        this.client = client;
    }

    @Given("a ZIP file named {string} is processed with:")
    public void aZIPFileNamedIsProcessedWith(String zipName, DataTable table) throws IOException {
        List<TestFileFactory.ArchiveEntry> entries = table.asMaps().stream()
                .map(row -> new TestFileFactory.ArchiveEntry(
                        row.get("fileName"),
                        row.get("type")
                ))
                .toList();
        createZip(inputFolder(), zipName, entries);

        context.setResponse(client.process(zipName));
    }

    @When("I load the XML file {string} from the processed ZIP {string}")
    public void iLoadTheXMLFile(String filename, String zipName) throws Exception {
        Path zipPath = processedFolder().resolve(zipName);


        assertTrue(Files.isRegularFile(zipPath), "XML file not found at: " + zipPath.toAbsolutePath());

        try(ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipPath))){
            ZipEntry entry;
            boolean xmlFound = false;

            while ((entry = zipInputStream.getNextEntry()) != null){
                if (entry.getName().equals(filename)) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    xmlDocument = builder.parse(zipInputStream);
                    xmlDocument.getDocumentElement().normalize();

                    xmlFound = true;
                    break;
                }
                zipInputStream.closeEntry();
            }
            assertTrue(
                    xmlFound,
                    "XML file '" + filename
                            + "' was not found inside "
                            + zipName
            );
        }
    }

    @Then("the XML tag {string} should have the value {string}")
    public void theXMLTagShouldHaveTheValue(String tagName, String value) {
        String actualValue = xmlDocument.getElementsByTagName(tagName).item(0).getTextContent();
        assertEquals(value, actualValue);

    }
}
