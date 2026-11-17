@metadata-contents
Feature: Validate and confirm metadata file contents
  Background:
    Given the ZIP processing folders exist
    And the ZIP processing folders are empty

    Scenario: XML file validation
      Given a ZIP file named "metadata.zip" is processed with:
        | fileName          | type |
        | application.pdf   | PDF  |
      When I load the XML file "metadata.xml" from the processed ZIP "metadata_processed.zip"
      Then the XML tag "name" should have the value "application.pdf"