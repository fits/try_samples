FROM centos

RUN yum -y update
RUN yum -y install make automake libtool git

RUN git clone https://github.com/twitter/twemproxy.git
RUN cd twemproxy && autoreconf -fvi && ./configure && make && make install
RUN rm -fr twemproxy

RUN yum clean all
