# isikato_backend


## Running in dev mode with in memory database
### Using Maven (Java version 17 is needed)

```
//just clone this repo and
//run below command in root directory
 $ ./mvnw spring-boot:run
```

### Using Docker(Docker needed)

```
//just clone this repo and
//run below commands in the given order
 $ docker build -t isikato .
 $ docker run -p 8080:8080 isikato
```
## Running in production
```
//just clone this repo and
//run below commands in the given order(replace ?)
    $ docker build -t isikato -f Dockerfile-production .
    $ docker run -d -p 1?:80 -e SV_NAME=2? -e DB_USER=3? -e DB_PASSWORD=4? --add-host=db:5? isikato

//1? host port
//2? oracle service name
//3? oracle user
//4? oracle password
//5? oracle host address(if local then 127.0.0.1)
```



## Api docs
swagger ui is available at [this](http://localhost:8080/api/swagger-ui.html) url after running the app
