mvn -Dmaven.test.skip=true install  &&     \
mvn jar:jar  &&                            \
mvn install:install-file                   \
    -Dpackaging=jar                        \
        -DgroupId=org.kuali.kra            \
    -DartifactId=kc-printing-xsds          \
        -Dversion=2.0.0                    \
    -DgeneratePom=true                     \
        -Dfile=target/kc-printing-xsds-2.0.0.jar
