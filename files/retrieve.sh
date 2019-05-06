#!/bin/bash
source functions.sh 

echoc 33 "start retrieve"

curl -vvv -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{ "dataMap": { "REQUEST_FILE_NAME":"RequestRetrieve.xml" }, "uuid": "" }' 'http://localhost:8080/PEPStandalone/v1/startDUCSRetrieve'

echoc 33 "\ndone\n"
