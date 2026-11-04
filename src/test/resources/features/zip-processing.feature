Feature: Validate and processing ZIP files

  Background:
    Given the ZIP processing folders exist
    And the ZIP processing folders are empty

  Scenario: Reject a ZIP containing an unsupported file
    Given a ZIP contains a TXT file
    When the ZIP is submitted for processing
    Then the API returns status 400
    And the ZIP file exists in the Error folder

  Scenario: Reject individually unsupported file types
    Given a ZIP file contains a valid type and an unsupported type
    When the ZIP is submitted for processing
    Then the API returns status 400
    And the API throws an exception
    And the ZIP file exists in the Error folder

  Scenario: Rejected ZIP receives Error message which contains unsupported file name
    Given a ZIP file is rejected
    Then the API returns status 400
    And the API throws an exception
    And the response status is "FAILED"
    And the response message is "Unsupported file type found"
    And the response unsupported file name matches

  Scenario: Process a ZIP containing one supported file
    Given a ZIP containing a supported file exists in the input folder
    When the ZIP file is submitted for processing
    Then the API response code is 200

#    to refactor
  Scenario: Process a ZIP containing multiple supported files
    Given a ZIP containing supported files exists in the input folder
    When the ZIP file is submitted for processing
    Then the API response code is 200
    And the response processed file is the ZIP file
    And the response encoded file is the base64 file
    And the processed ZIP file exists in the processed folder
    And the base64 file exists in the encoded folder
    And the base64 output decodes to a valid ZIP
    And the decoded ZIP matches the processed ZIP
    And the original ZIP no longer exists in the input folder




