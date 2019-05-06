#!/bin/bash
source functions.sh 

echoc 33 "start publish"

curl -vvv -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{ "dataMap": { "POLICY_FILE_NAME":"PolicyPublish.xml", "REQUEST_FILE_NAME":"RequestPublish.xml" }, "uuid": "" }' 'http://localhost:8080/PEPStandalone/v1/startDUCSPublish'

echoc 33 "\ndone\n"
