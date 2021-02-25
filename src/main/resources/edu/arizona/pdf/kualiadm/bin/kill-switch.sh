#!/bin/bash


CLASSPATH_DIR="/var/opt/kuali/tomcat/webapps/kra-saas573/WEB-INF/classes"

function main() {
    if [[ $# -lt 1 ]]; then
        echo "usage: kill-switch.sh <click|reset>"
        exit 1
    fi

    command="$1"
    if [[ "$command" = "click" ]]; then
        echo "Setting file to kill pdf processing."
        touch "$CLASSPATH_DIR/kill-switch.txt"
    else
        echo "Removing kill file."
        rm -f "$CLASSPATH_DIR/kill-switch.txt"
    fi
}

main $@

