# TestdataGenerator

A tool implemented in Kotlin to generate AIXM 5.1 test data. It uses the schema files as input to produce an AIXMBasicMessage file containing all features with first-level properties set to nil.

## Work in progress

The following enhancements are foreseen in future versions:
* supporting objects with nil values
* generate valid random values instead of nil
* generate meta data
* generate geometry objects
* support for AIXM 5.1.1

## System Requirements
Installed and working Java JRE (Oracle or OpenJDK) version 8 or higher.

## Usage

```
data-gen [OPTIONS] INPUTFILE OUTPUTFILE

Options:
  --valid-time-begin VALUE  validTime beginPosition
  --valid-time-end VALUE    validTime endPosition
  -h, --help                Show this message and exit

Arguments:
  INPUTFILE   The name of the message schema file, e.g. <path to
              AIXMBasicMessage.xsd>.
  OUTPUTFILE  The name of the output file.
```

Windows:
```
bin\data-gen.bat C:\aixm51\xsd\message\AIXM_BasicMessage.xsd output.xml --valid-time-begin "2019-12-24T00:00:00.000Z" --valid-time-end "2019-12-31T00:00:00.000Z"
```

Unix-like (Mac, Linux, etc)
```bash
bin/data-gen ~/aixm51/xsd/message/AIXM_BasicMessage.xsd output.xml --valid-time-begin="2019-12-24T00:00:00.000Z" --valid-time-end "2019-12-31T00:00:00.000Z"
```