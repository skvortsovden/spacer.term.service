# Spacer Term Service

REST API With Scala Play Framework and Reactive Mongo.

## Local development

Spin up docker container with mongo database.
```
docker-compose up -d
```

## Endpoints

### Get all terms
```
curl localhost:9000/terms
```

### Get term by id
```
curl localhost:9000/terms/62c89d630c9e72d7efccedca
```

### Create term
```
curl -X POST localhost:9000/terms -H "Content-Type: application/json" \
--data '{ "text":"text1", "definition":"def1", "example":"ex1", "tags": ["tag1"] }'
```

### Update term
```
curl -X PUT localhost:9000/terms/62c89d630c9e72d7efccedca -H "Content-Type: application/json" \
--data '{ "text":"updated_text1", "definition":"def1", "example":"ex1", "tags": ["tag1"] }'
```

### Delete term
```
curl -X DELETE localhost:9000/terms/62c89d630c9e72d7efccedca
```