#!/bin/bash


function main() {
    cp ~/git/kc_custom/target/kuali-coeus-5.2.1-ua-SNAPSHOT.war /tmp/kra-saas573.war
    chmod 755 /tmp/kra-saas573.war
}

main $@
