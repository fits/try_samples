
# create table

```
aws --endpoint-url=http://localhost:8000 dynamodb create-table --table-name Items --attribute-definitions "AttributeName=id,AttributeType=S" --key-schema "AttributeName=id,KeyType=HASH" --provisioned-throughput "ReadCapacityUnits=1,WriteCapacityUnits=1"
```