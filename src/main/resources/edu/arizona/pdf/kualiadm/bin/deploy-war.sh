#!/bin/bash

CATALINA_BASE="/var/opt/kuali/tomcat"

function main() {
    /etc/opt/kuali/kualictl tomcat stop

    echo "Deleting old war"
    rm -rf $CATALINA_BASE/webapps/kra-saas573
    rm -f $CATALINA_BASE/webapps/kra-saas573.war

    echo "Copying new war"
    cd  $CATALINA_BASE/webapps
    mkdir kra-saas573
    cd kra-saas573
    cp /tmp/kra-saas573.war .

    echo "Extracting war"
    jar xf kra-saas573.war
    rm kra-saas573.war
    cd

    echo "Copying config"
    cp ~/config/web.xml $CATALINA_BASE/webapps/kra-saas573/WEB-INF/
    cp ~/config/setenv.sh /var/opt/kuali/tomcat/bin/setenv.sh
    cp ~/config/kc-config.xml ~/kuali/main/saas573/

    /etc/opt/kuali/kualictl tomcat start
}

main $@

