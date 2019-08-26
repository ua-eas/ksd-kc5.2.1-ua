/var/jenkins_home/tools/hudson.tasks.Maven_MavenInstallation/maven-3.2.3/bin/mvn -Dmaven.test.skip=true install  &&     \
/var/jenkins_home/tools/hudson.tasks.Maven_MavenInstallation/maven-3.2.3/bin/mvn jar:jar  &&                            \
/var/jenkins_home/tools/hudson.tasks.Maven_MavenInstallation/maven-3.2.3/bin/mvn install:install-file                   \
    -Dpackaging=jar                        \
        -DgroupId=org.kuali.kra            \
    -DartifactId=kc-printing-xsds          \
        -Dversion=2.0.0                    \
    -DgeneratePom=true                     \
        -Dfile=target/kc-printing-xsds-2.0.0.jar
