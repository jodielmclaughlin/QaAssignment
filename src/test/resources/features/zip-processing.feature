Feature: Validate and process ZIP files

  Background:
    Given the ZIP processing folders exist
    And the ZIP processing folders are empty

  Scenario: Reject individually unsupported file types
    Given a ZIP file named "textFile.zip" contains a TXT file with:
      | fileName        | type |
      | application.pdf | PDF  |
      | notes.txt       | TEXT |
    When the ZIP file "textFile.zip" is submitted for processing
    Then the API response code is 400
    And the response status is "FAILED"
    And the response message is "Invalid entries in zip file"
    And the response unsupported file name is "notes.txt"
    And "textFile.zip" exists in the error folder
    And "textFile.zip" no longer exists in the input folder
    And no processed ZIP has been created
    And no Base64 output has been created

  Scenario: Reject individually unsupported file types
    Given a ZIP file named "unsupported.zip" containing an unsupported file with:
      | fileName      | type |
      | notes.txt     | TEXT |
    When the ZIP file "unsupported.zip" is submitted for processing
    Then the API response code is 400
    And the response status is "FAILED"
    And the response message is "Invalid entries in zip file"
    And the response unsupported file name is "notes.txt"
    And "unsupported.zip" exists in the error folder
    And "unsupported.zip" no longer exists in the input folder
    And no processed ZIP has been created
    And no Base64 output has been created

  Scenario: Process a ZIP containing one supported file
    Given a ZIP file named "document.zip" containing a supported file exists in the input folder
      | fileName          | type |
      | application.pdf   | PDF  |
    When the ZIP file "document.zip" is submitted for processing
    Then the API response code is 200
    And the response status is "SUCCESS"
    And the response source file is "document.zip"
    And the response processed file is "document_processed.zip"
    And the response encoded file is "document_processed_base64.txt"
    And "document_processed.zip" exists in the processed folder
    And "document_processed_base64.txt" exists in the encoded folder
    And the processed ZIP contains only:
      | fileName          |
      | application.pdf   |
    And the base64 output decodes to a valid ZIP
    And the decoded ZIP matches the processed ZIP
    And "document.zip" no longer exists in the input folder

  Scenario: Process a ZIP file containing multiple supported files
    Given  ZIP file named "documents.zip" containing supported files exists in the input folder
      | fileName          | type |
      | application.pdf   | PDF  |
      | identity.jpg      | JPEG |
      | photograph.jpeg   | JPEG |
    When the ZIP file "documents.zip" is submitted for processing
    Then the API response code is 200
    And the response status is "SUCCESS"
    And the response source file is "documents.zip"
    And the response processed file is "documents_processed.zip"
    And the response encoded file is "documents_processed_base64.txt"
    And "documents_processed.zip" exists in the processed folder
    And "documents_processed_base64.txt" exists in the encoded folder
    And the processed ZIP contains only:
      | fileName          |
      | application.pdf   |
      | identity.jpg      |
      | photograph.jpeg   |
    And the base64 output decodes to a valid ZIP
    And the decoded ZIP matches the processed ZIP
    And "documents.zip" no longer exists in the input folder




