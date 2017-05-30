FROM maven:3.5.0-jdk-8

MAINTAINER citrusframework.org <citrus-dev-l@consol.de>

ENV APP_VERSION=1.0.0
ENV APP_ARTIFACT=voting-demo-app
ENV DEPLOY_DIR /app

EXPOSE 8080

USER root

RUN mkdir ${DEPLOY_DIR}

RUN curl http://build-n.int.consol.de/repository/citrus-releases/com/consol/citrus/samples/${APP_ARTIFACT}/${APP_VERSION}/${APP_ARTIFACT}-${APP_VERSION}-executable.war -o ${DEPLOY_DIR}/${APP_ARTIFACT}.war

RUN find $DEPLOY_DIR -name '*.war' -exec chmod a+x {} +

RUN useradd -U openshift

CMD java -jar ${DEPLOY_DIR}/${APP_ARTIFACT}.war

USER openshift