#!/usr/bin/python3

import json
import requests
import time
import subprocess
import os

pepurl = "http://localhost:9999/"
startEvaluation = "startEvaluation"
finish = "finish"
status = "flowStatus"

#start evaluation

print("*****Start evaluation*****")

response = requests.post(pepurl + startEvaluation)
assert(response.status_code == 200)
messageId = str(response.content.decode('utf-8'))
assert('ID:' in messageId)
print(messageId)

print("*****Wait 10 seconds*****")

flowResponse = ''
for i in range(0,5) : 
    time.sleep(1)

    print("Get flow status")

    payload='messageId=' + messageId

    response = requests.get(pepurl + status, params=payload)
    assert(response.status_code == 200)
    flowResponse = response.content.decode('utf-8')


print(flowResponse)

statusObject = json.loads(flowResponse)

#"status": "STARTACCESS_PERMIT"
assert(statusObject['status'] == 'STARTACCESS_PERMIT')
assert(len(statusObject['sessionId']) >  0)

print("*****REVOKE*****")
#docker exec -it usagecontrolsystem_ucs_1 bash -c 'echo 40.0 > pips/temperature.txt'
os.system("docker exec continuousdevelopmentpipeline_ucs_1 bash -c 'echo 40.0 > pips/temperature.txt'")
#subprocess.call(["docker", "exec", "-it", "usagecontrolsystem_ucs_1", "bash", "-c", "'echo 40.0 > pips/temperature.txt'"])

for i in range(0,15) :
    time.sleep(1)

    #print("Get flow status")

    payload='messageId=' + messageId

    response = requests.get(pepurl + status, params=payload)
    assert(response.status_code == 200)
    flowResponse = response.content.decode('utf-8')
    
    print("Flow status: " + json.loads(flowResponse)['status'])

print(flowResponse)

statusObject = json.loads(flowResponse)

#"status": "STARTACCESS_PERMIT"
assert(statusObject['status'] == 'ENDACCESS_DENY')

os.system("docker exec continuousdevelopmentpipeline_ucs_1 bash -c 'echo 20.0 > pips/temperature.txt'")

print("*****END*****")
