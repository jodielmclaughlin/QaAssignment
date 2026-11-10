Feature: Validate and process ZIP files

  Background:
    Given the ZIP processing folders exist
    And the ZIP processing folders are empty


  Scenario: Reject a request when the ZIP does not exist
    Given "missing.zip" does not exist in the input folder
    When the ZIP file "missing.zip" is submitted for processing
    Then the API response code is 400
    And the response status is "FAILED"
    And the response message is "Input file does not exist: missing.zip"
    And no processed ZIP has been created
    And no Base64 output has been created

  Scenario: Reject a corrupt ZIP file
    Given a corrupt ZIP file named "corrupt.zip" exists in the input folder
    When the ZIP file "corrupt.zip" is submitted for processing
    Then the API response code is 400
    And the response status is "FAILED"
    And "corrupt.zip" exists in the error folder
    And no processed ZIP has been created
    And no Base64 output has been created

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

  Scenario Outline: Reject unsupported file type
    Given a ZIP file named "unsupported.zip" containing an unsupported file with:
      | fileName      | type   |
      | <fileName>    | <type> |
    When the ZIP file "unsupported.zip" is submitted for processing
    Then the API response code is 400
    And the response status is "FAILED"
    And the response message is "Invalid entries in zip file"
    And the response unsupported file name is "<fileName>"
    And "unsupported.zip" exists in the error folder
    And "unsupported.zip" no longer exists in the input folder
    And no processed ZIP has been created
    And no Base64 output has been created

    Examples:
      | fileName         | type |
      | notes.txt        | TEXT |
      | data.json        | JSON |
      | spreadsheet.xlsx | XLSX |

  Scenario: Show multiple rejected unsupported file types in response
    Given a ZIP file named "unsupportedMulti.zip" containing an unsupported file with:
      | fileName      | type |
      | notes.txt     | TEXT |
      | jsonFile.json     | JSON |
      | spreadsheet.xlsx     | XLSX |
    When the ZIP file "unsupportedMulti.zip" is submitted for processing
    Then the API response code is 400
    And the response status is "FAILED"
    And the response message is "Invalid entries in zip file"
    And the response unsupported file names are:
      | fileName          |
      | notes.txt         |
      | jsonFile.json     |
      | spreadsheet.xlsx  |
    And "unsupportedMulti.zip" exists in the error folder
    And "unsupportedMulti.zip" no longer exists in the input folder
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
    And "document.zip" exists in the processed folder
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
    And "documents.zip" exists in the processed folder
    And "documents_processed_base64.txt" exists in the encoded folder
    And the processed ZIP contains only:
      | fileName          |
      | application.pdf   |
      | identity.jpg      |
      | photograph.jpeg   |
    And the base64 output decodes to a valid ZIP
    And the decoded ZIP matches the processed ZIP
    And "documents.zip" no longer exists in the input folder




