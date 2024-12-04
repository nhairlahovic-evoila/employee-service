# Employee Service

This is a simple Spring Boot application for managing employees, backed by Elasticsearch. The project demonstrates how to integrate Spring Boot with Elasticsearch.

## Branches
- `main`: Contains the base application.
- `cloudfoundry-deploy`: Contains necessary adjustments for deploying the application on Cloud Foundry, including Cloud Foundry-specific data source configuration and manifest file.

## Deploying on Cloud Foundry
To deploy the employee-service application on Cloud Foundry, follow these steps:

#### 1. Build the Application

Build the Spring Boot application using Maven:

```bash
cd employee-service
./mvnw clean install -DskipTests
```

This will generate the JAR file `employee-service-0.0.1-SNAPSHOT.jar` in the `target` directory.

### 2. Deploy to Cloud Foundry

Once the JAR file is built, deploy the application to Cloud Foundry using the provided `manifest.yml`:
```bash
cf push
```

This command will:
- Deploy the `employee-service-0.0.1-SNAPSHOT.jar`: The JAR file from the `employee-service/target/` directory will be deployed.
- Use the configuration in `manifest.yml`: Cloud Foundry will automatically use the settings specified in the manifest for deployment.

### 3. Manifest explanation

#### Environment Variables (`env`)

- `JBP_CONFIG_OPEN_JDK_JRE`: This specifies the version of the Java Runtime Environment (JRE) to use. In this case, it sets the JRE to version 17 or higher.
- `SPRING_PROFILES_ACTIVE`: This activates the Spring `cloudfoundry` profile (environment-specific configurations in `application-cloudfoundry.yml`).
- `SERVICE_OFFERING_NAME`: This specifies the name of the Cloud Foundry service offering for Elasticsearch. The application will use this value to look for the service credentials in the `VCAP_SERVICES` environment variable at runtime.


#### Services (`services`)

  ```yaml
  services:
  - elastic-db
  ```

`elastic-db`: This binds the application to a Elasticsearch database service in Cloud Foundry. The service must be created and available in your Cloud Foundry environment.
During deployment, Cloud Foundry will inject the database credentials into the `VCAP_SERVICES` environment variable, which the application uses to connect to the database.

**Note**: If the services section is omitted from the `manifest.yml`, the service binding can still be done manually after deployment with the following command:
```sh
cf bind-service employee-service elastic-db
```

## Testing the Application

After a successful deployment, Cloud Foundry will provide a URL to access the application. You can use the following command to check the app's routes:

```bash
cf apps
```

The app will be accessible via the route `http://employee-service.<your-cloud-foundry-domain>/`.

**1. Add an Employee**

POST `/api/employees`

Request Body:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "department": "SALES"
}
```

**2. Get All Employees**

GET `/api/employees`

