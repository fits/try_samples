
FROM centos

RUN yum update -y
RUN yum install -y http://dev.mysql.com/get/mysql-community-release-el7-5.noarch.rpm 
RUN yum install -y mysql-community-server
RUN mysql_install_db --datadir=/var/lib/mysql --user=mysql

ADD mysql_init.sql mysql_init.sql

RUN /usr/bin/mysqld_safe & sleep 5 ; mysql < mysql_init.sql

EXPOSE 3306

CMD ["/usr/bin/mysqld_safe"]
