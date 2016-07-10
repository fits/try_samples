FROM fedora

RUN dnf update -y
RUN dnf install -y clinfo pocl pocl-devel

RUN ln -s /lib64/libpoclu.so /lib64/libOpenCL.so

RUN dnf install -y tar findutils make python2

RUN dnf clean all
