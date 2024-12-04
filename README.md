# Employee Service

This is a simple Spring Boot application for managing employees, backed by Elasticsearch. The project demonstrates how to integrate Spring Boot with Elasticsearch.

## Branches
- `main`: Contains the base application.
- `cloud-foundry-deploy`: Contains necessary adjustments for deploying the application on Cloud Foundry, including Cloud Foundry-specific data source configuration and manifest file.

## Testing the Application

After starting the application, you can use tools like Postman or cURL to interact with the API.

**1. Add an Employee**

POST `http://localhost:8080/api/employees`

Request Body:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "department": "SALES"
}
```

**2. Get All Employees**

GET `http://localhost:8080/api/employees`

Example cURL Command:

```bash
curl http://localhost:8080/api/employees
```

## Running Elasticsearch with Docker
To run Elasticsearch with a specified username and password on `localhost:9200`, use the following command:

```
bash
docker run -d --name elasticsearch \
  -p 9200:9200 \
  -e "discovery.type=single-node" \
  -e "ELASTIC_PASSWORD=your-password" \
  -e "ELASTIC_USERNAME=your-username" \
  docker.elastic.co/elasticsearch/elasticsearch:8.6.1
```

Replace `your-username` and `your-password` with your desired credentials, and make sure to update the `application.yml` file with the same credentials.
