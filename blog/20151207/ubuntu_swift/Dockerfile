
FROM ubuntu:15.10

ENV SWIFT_PACKAGE swift-2.2-SNAPSHOT-2015-12-01-b-ubuntu15.10

RUN apt-get update
RUN apt-get -y upgrade

RUN apt-get -y install curl clang libicu-dev

RUN curl https://swift.org/builds/ubuntu1510/swift-2.2-SNAPSHOT-2015-12-01-b/$SWIFT_PACKAGE.tar.gz -o $SWIFT_PACKAGE.tar.gz

RUN tar zxf $SWIFT_PACKAGE.tar.gz
RUN rm -f $SWIFT_PACKAGE.tar.gz

RUN apt-get clean

ENV PATH /$SWIFT_PACKAGE/usr/bin:$PATH
