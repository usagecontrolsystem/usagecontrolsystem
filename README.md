# usagecontrolsystem (UCS)

Read the following papers to understand the concepts of ABAC and how UCS implements them, especially the concept of continuous monitoring.
1) https://fardapaper.ir/mohavaha/uploads/2017/10/Implementing-Usage-Control-in-Internet-of-Things.pdf
2) https://www.iit.cnr.it/sites/default/files/Internet%20of%20Things.pdf
3) https://www.iit.cnr.it/sites/default/files/main_22.pdf
4) https://www.iit.cnr.it/sites/default/files/Improving_MQTT_by_inc_of_UCON.pdf

## Try UCS by performing the following steps

1) clone this repository to your local workplace
2) build it using **mvn clean install** (optionally you can skip the tests by specifying **-DskipTests=true**)
3) run the following 2 java main applications, which will start two REST services
* in PEPRest project, it.cnr.iit.peprest.PEPRestStarter (will listen on port 9999)
* in UCSRest project, it.cnr.iit.ucsrest.rest.UCSRestStarter (will listen on port 9998)
4) open up a web browser and goto the following URL
* http://localhost:9999/swagger-ui.html#!/pep-rest-controller/startEvaluationUsingPOST
5) click on **Try it out** button
6) you will see in the log outputs the following
* in PEPRest: after tryAccess, startAccess and evaluation **Permit** is received
* in UCSReST: policy XML, pdp response **Permit** and several "Polling on value..." messages per monitored attribute 
7) go to directory <cloned-location-of-ucs>/res/pips
8) open text file **light** and change its value from 0 to 1 and save the file
9) you will see in the log outputs the following
  * in UCSRest: Reevaluation of Sessions for attribute light, revoke and end of session
  * in PEPRest: On going evaluuation, ending of session an evalution **Deny**
10) retry by restarting UCSRest as in step 3 and then repeat step 4
  * since the light in text file has value 1, you will see **Deny** in the log files and no Polling for attributes take place
11) to retry again and to receive **Permit**, edit the light value to 0, save the file and repeat steps 3-4  
  
There are several unit, coverage and integration tests written in BDD style, especially in PEPRest and UCSRest projects that you can execute to dive more into the code.
