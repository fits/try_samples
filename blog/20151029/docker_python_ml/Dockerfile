FROM python

RUN apt-get update && apt-get upgrade -y

RUN apt-get install -y libfreetype6-dev libatlas-base-dev liblapack-dev gfortran

RUN pip install numpy
RUN pip install scipy
RUN pip install matplotlib
RUN pip install scikit-learn

RUN apt-get clean
