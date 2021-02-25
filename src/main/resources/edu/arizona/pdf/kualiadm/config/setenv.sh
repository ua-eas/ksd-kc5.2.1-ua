#
#setenv.sh for the kra-5.2.1-ua-build357 component
#

export JPDA_TRANSPORT="dt_socket"
export JPDA_ADDRESS="8444"
export JPDA_SUSPEND="n"
export JPDA_OPTS="-agentlib:jdwp=transport=$JPDA_TRANSPORT,address=$JPDA_ADDRESS,server=y,suspend=$JPDA_SUSPEND"

CATALINA_OPTS="-Xms6g -Xmx30g -XX:MaxPermSize=1g -XX:+UseTLAB -XX:NewRatio=3 -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:CMSInitiatingOccupancyFraction=60 -XX:+DisableExplicitGC -verbose:gc -XX:+PrintGCDetails -Dorg.apache.el.parser.SKIP_IDENTIFIER_CHECK=true -Dorg.apache.jasper.compiler.Parser.STRICT_QUOTE_ESCAPING=false -Djavamelody.system-actions-enabled=false -Djava.awt.headless=true -Djava.util.prefs.syncInterval=2000000 -Dnetworkaddress.cache.ttl=60 -Djava.security.egd=file:///dev/urandom -Denvironment=saas573 -Dalt.config.location=/home/kualiadm/kuali/main/saas573/kc-config.xml -Dnewrelic.environment=saas573 -javaagent:/var/opt/kuali/tomcat/newrelic/newrelic.jar $JPDA_OPTS"
export CATALINA_OPTS

CATALINA_PID="$CATALINA_BASE/tomcat.pid"
export CATALINA_PID

LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$CATALINA_HOME/lib
export LD_LIBRARY_PATH
