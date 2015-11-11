FROM centos

RUN yum update -y && yum install -y make automake libtool openssl-devel curl
RUN yum install -y lapack-devel atlas-devel gcc-c++ python-devel

RUN curl -O https://bootstrap.pypa.io/get-pip.py
RUN python get-pip.py

RUN pip install https://storage.googleapis.com/tensorflow/linux/cpu/tensorflow-0.5.0-cp27-none-linux_x86_64.whl

RUN yum clean all
