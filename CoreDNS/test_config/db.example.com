$ORIGIN example.com.
@ IN SOA  dns.example.com. admin.example.com. 2025080413 240 120 3600 1800

test    IN A    127.0.0.2
db1     IN A    127.0.0.3
db2     IN A    127.0.0.4

db-endpoint.example.com.    IN SRV  120 1 13306 db1.example.com.

