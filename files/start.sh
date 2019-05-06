#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
cd $DIR

source functions.sh

echo "146.48.99.172 elastic" >> /etc/hosts

source setup_mysql.sh
source setup_tomcat.sh
sleep 3

echoc 31 "try publish.sh, retrieve.sh"

bash
