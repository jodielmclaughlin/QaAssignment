@metadata
  Feature: Validate and return metadata files

    Background:
      Given the ZIP processing folders exist
      And the ZIP processing folders are empty

    Scenario: A processed multiple file ZIP will show an XML file in the processed ZIP
    Given a ZIP file named "metadataCheck.zip" exists in the input folder with:
      | fileName          | type |
      | application.pdf   | PDF  |
      | identity.jpg      | JPEG |
      | photograph.jpeg   | JPEG |
      When the ZIP file "metadataCheck.zip" is submitted for processing
      Then the API response code is 200
      And the processed ZIP contains only:
        | fileName          |
        | application.pdf   |
        | identity.jpg      |
        | photograph.jpeg   |
        | metadata.xml      |

     Scenario Outline: A processed ZIP for every type of OK file will show an XML file in the processed ZIPs
        Given a ZIP file named "metadataSingle.zip" exists in the input folder with:
          | fileName          | type    |
          | <fileName>        | <type>  |
       When the ZIP file "metadataSingle.zip" is submitted for processing
       Then the API response code is 200
       And the processed ZIP contains only:
         | fileName          |
         | <fileName>        |
         | metadata.xml      |

       Examples:
         | fileName          | type |
         | application.pdf   | PDF  |
         | identity.jpg      | JPEG |
         | photograph.jpeg   | JPEG |

