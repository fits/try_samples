
# System Environment Settings

* ```AWS_REGION=ap-northeast-1```
* ```AWS_ACCESS_KEY_ID=fakeMyKeyId```
* ```AWS_SECRET_ACCESS_KEY=fakeSecretAccessKey```
* ```DYNAMODB_ENDPOINT=http://localhost:8000```

# Run DynamoDB Local

```
cd dynamodb_local_latest
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -inMemory
```
