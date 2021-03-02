#!/bin/bash

CATALINA_BASE="/var/opt/kuali/tomcat"

function main() {
    /etc/opt/kuali/kualictl tomcat stop
    /etc/opt/kuali/kualictl tomcat start
}

main $@

