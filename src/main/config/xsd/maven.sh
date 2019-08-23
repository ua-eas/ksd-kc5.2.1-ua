mvn -Dmaven.test.skip=true -Dhttps.protocols=TLSv1.2 install -e -X  &&     \
mvn jar:jar -Dhttps.protocols=TLSv1.2 -e -X &&                            \
mvn install:install-file -e -X                \
    -Dpackaging=jar                        \
        -DgroupId=org.kuali.kra            \
    -DartifactId=kc-printing-xsds          \
        -Dversion=2.0.0                    \
    -DgeneratePom=true                     \
        -Dhttps.protocols=TLSv1.2          \
        -Dfile=target/kc-printing-xsds-2.0.0.jar
