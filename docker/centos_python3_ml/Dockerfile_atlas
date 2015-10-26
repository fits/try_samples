FROM centos

RUN yum update -y && yum install -y make automake libtool openssl-devel curl

RUN curl -O https://www.python.org/ftp/python/3.5.0/Python-3.5.0.tgz
RUN tar zxf Python-3.5.0.tgz
RUN cd Python-3.5.0 && ./configure && make && make install
RUN rm -fr Python-3.5.0 && rm -f Python-3.5.0.tgz

RUN yum install -y lapack-devel atlas-devel gcc-c++ freetype-devel libpng-devel

RUN cd /usr/lib64/atlas && ln -s libtatlas.so libcblas.so

RUN pip3.5 install numpy
RUN pip3.5 install scipy
RUN pip3.5 install matplotlib
RUN pip3.5 install scikit-learn

RUN yum clean all
