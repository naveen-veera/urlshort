# URL shortening application

See `src/main/resources/application.yml` for configuration options

Running with HSQL database:

```
./gradlew -Pe=hsql bootrun
```

Running with PostgreSQL database:

```
./gradlew -Pe=psql bootrun
```

Debugging

```
./gradlew -Pe=hsql bootrun -Pdebug
```
