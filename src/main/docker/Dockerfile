FROM maven:3.5.0-jdk-8

MAINTAINER citrusframework.org <citrus-dev-l@consol.de>

ENV APP_VERSION=1.0.0
ENV APP_ARTIFACT=voting-demo-app
ENV DEPLOY_DIR /app
ENV INST_SCRIPTS /install

EXPOSE 8080

USER root

ADD ./scripts/ $INST_SCRIPTS/
RUN find $INST_SCRIPTS -name '*.sh' -exec chmod a+x {} +

RUN mkdir ${DEPLOY_DIR}

RUN curl http://build-n.int.consol.de/repository/citrus-releases/com/consol/citrus/samples/${APP_ARTIFACT}/${APP_VERSION}/${APP_ARTIFACT}-${APP_VERSION}-executable.war -o ${DEPLOY_DIR}/${APP_ARTIFACT}.war

### configure startup
RUN $INST_SCRIPTS/set_user_permission.sh $DEPLOY_DIR

RUN useradd -U openshift

CMD java -jar ${DEPLOY_DIR}/${APP_ARTIFACT}.war

USER openshift