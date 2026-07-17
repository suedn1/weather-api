This project was created using spring initializr.

To run the project in IntelliJ, import it as a Maven project, wait for dependencies to download, and run the `WeatherApplication` class as a Spring Boot application.  

To run it from the terminal, go to the project root folder and use `./mvnw spring-boot:run`. You must be using Java version 17 or above.

As Redis is being used for cache, a `docker-compose.yml` file was created to build the Redis image in a container. In order to create this image:

- Install `Docker` as it is required to create the container. Do it through the official documentation `https://docs.docker.com/get-started/introduction/get-docker-desktop/`
- In root of the `weather-api` folder, run `docker compose up -d`. By doing it, you should be able to see a container called `redis_cache` running.

At this point, we should have the application running, and Redis cache container up.

## WeatherApplication

Nothing was changed in this file, since it's initiating the app.

## LocationController

This controller was created to handle calls to the "locations" entity. The endpoint `"/{zipCode}/weather"` was added to handle API calls that want to get weather information according to a given zip code. This endpoint accepts a query param called `extendedDetails`. By default, this query param is `false` but can be changed if passed on URL. If this query param is `true`, it will add extended weather information to the return of the endpoint. Inside the service layer, for each call we have different errors, so considering it the controller has some treatments to handle different errors.

## LocationService

This service is responsible for calling external APIs to get geolocation information from a zip code and, based on the coordinates, retrieving current weather data and optional extended climate details. It centralizes the orchestration between the geolocation provider and the Open Meteo weather APIs, exposing a single method that the controller uses to build the response for the client.

- Biggest challenge was to create DTOs for all the calls, and join all the information to build the return according to the `extendedDetails` flag. Needed to find a way to relate all the needed DTOs, and also needed to find a way to pass the query params in a more elegant way than a single and concatenated string in uri. Fortunately, a solution was found and it's on the code.

## RestClientConfig

I learnt briefly some months ago that do this kind of file was good to keep a single instance of the client, that can be used in every place of the application instead of instantiate everywhere. In this project this is not needed, since we have a specific class using it, but I preferred to follow good practices.

## RedisCacheConfig

I also had an opportunity to see Redis cache working some months ago when I worked on a project as a fullstack developer and needed to implement a TTL (Time To Live) for an endpoint response. But a challenge was to configure it from beggining and make it work as expected.

`RedisCacheConfig` only creates a RedisCacheManager instance to identify cacheable information through the application and save them. The configuration `entryTtl(Duration.ofMinutes(15))` only adds the the required rule of keeping data on cache for 15 minutes.

On `WeatherApplication.java` the annotation `@EnableCaching` were added to enable caching functionality through the entire application.

On `LocationService.java` we can find the `@Cacheable` annotation, which sets a value (reference to identify the source of the cache) and a key (which identifies the value of the params used to cache the data, for example: 63100000-false).

How to see cached information through the terminal:

When a call to `http://localhost:8080/locations/63100000/weather`, for example:

- A registry is created on `redis_cache`. You can see the key of this registry running the following command on terminal: `docker exec redis_cache redis-cli KEYS '*'`. After running the command you should see the registry `weatherByZipCode::63100000-false` on terminal. The structure of the key is `{value}::{#zipCode}-{#extendedDetails}`

- In order to see the content cached on this key, you should run the command `docker exec redis_cache redis-cli GET weatherByZipCode::63100000-false`. The last parameter after GET is any key returned from the first command, that you want to see information about.

When the result of the endpoint is coming from the API's (first call with the given parameters), in response headers will be present the `X-Cache-Status` containing value `MISS`. Otherwise, this header will not be present in the response headers.

## CurrentWeather

Made it public to access some of the properties of this record.

## CurrentWeatherUnits

Made it public to access some of the properties of this record.

## ExtendedWeatherDTO

DTO created to join information about forecast and climate APIs from Open Meteo, than it can be returned when the flag `extendedDetails` is `true`.

## NominatimResponseDTO

DTO created to identify relevant information that we use to retrieve weather information by geolocation coordinated.

## OpenMeteoClimateResponseDTO

DTO created to identify relevant information that we use from weather climate API.

## OpenMeteoResponseDTO

DTO created to get basic weather information to be returned when `extendedDetails` is false, and to be used as complement when the mentioned flag is true.

## SimpleWeatherDTO

DTO created to return the fields of endpoint when `extendedDetails` is `false`.
