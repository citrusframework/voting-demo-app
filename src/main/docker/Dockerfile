FROM fabric8/tomcat-8

MAINTAINER ConSol Software GmbH <testautomatisierung@consol.de>

ENV APP_VERSION=1.0.0

ENV CATALINA_OUT=/dev/null

USER root

RUN mkdir ${DEPLOY_DIR} && rm -rf /opt/tomcat/webapps/ROOT

RUN curl http://build-n.int.consol.de/repository/citrus-releases/com/consol/citrus/samples/voting-demo-app/${APP_VERSION}/voting-demo-app-${APP_VERSION}.war -o ${DEPLOY_DIR}/ROOT.war

RUN ln -s ${DEPLOY_DIR}/ROOT.war /opt/tomcat/webapps/ROOT.war

# Use faster (though more unsecure) random number generator
RUN export CATALINA_OPTS="${CATALINA_OPTS} $(/opt/run-java-options) $(/opt/java-container-options) $(/opt/debug-options) -Djava.security.egd=file:/dev/./urandom"

RUN groupadd -g 1000080000 openshift
RUN useradd -u 1000080000 -g openshift -G jboss openshift
RUN chown -h openshift:jboss /opt/tomcat && chown -R openshift:jboss /opt/apache-tomcat-${TOMCAT_VERSION}

CMD /opt/tomcat/bin/catalina.sh run

USER openshift