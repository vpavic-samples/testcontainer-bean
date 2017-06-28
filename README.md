# TestContainers - stop failure if used as Spring bean

This is a minimal project to demonstrate the problem with stopping TestContainers when it is being used Spring bean in a traditional web application deployed to Tomcat.

When a container is configured like this:

```java
@Bean(initMethod = "start")
public GenericContainer redisContainer() {
	return new GenericContainer("redis:3.2.9").withExposedPorts(6379);
}

@Bean
public JedisConnectionFactory redisConnectionFactory() {
	JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
	connectionFactory.setHostName(redisContainer().getContainerIpAddress());
	connectionFactory.setPort(redisContainer().getMappedPort(6379));
	return connectionFactory;
}
```

The following shutdown error occurs:

```log
14-Jun-2017 14:48:10.987 SEVERE [localhost-startStop-2] org.apache.catalina.loader.WebappClassLoaderBase.checkThreadLocalMapForLeaks The web application [ROOT] created a ThreadLocal with key of type [java.lang.ThreadLocal] (value [java.lang.ThreadLocal@2b9ff8a8]) and a value of type [org.testcontainers.shaded.io.netty.util.internal.InternalThreadLocalMap] (value [org.testcontainers.shaded.io.netty.util.internal.InternalThreadLocalMap@4581e709]) but failed to remove it when the web application was stopped. Threads are going to be renewed over time to try and avoid a probable memory leak.
14-Jun-2017 14:48:10.988 SEVERE [localhost-startStop-2] org.apache.catalina.loader.WebappClassLoaderBase.checkThreadLocalMapForLeaks The web application [ROOT] created a ThreadLocal with key of type [java.lang.ThreadLocal] (value [java.lang.ThreadLocal@2b9ff8a8]) and a value of type [org.testcontainers.shaded.io.netty.util.internal.InternalThreadLocalMap] (value [org.testcontainers.shaded.io.netty.util.internal.InternalThreadLocalMap@13da7c79]) but failed to remove it when the web application was stopped. Threads are going to be renewed over time to try and avoid a probable memory leak.
14-Jun-2017 14:48:10.988 SEVERE [localhost-startStop-2] org.apache.catalina.loader.WebappClassLoaderBase.checkThreadLocalMapForLeaks The web application [ROOT] created a ThreadLocal with key of type [java.lang.ThreadLocal] (value [java.lang.ThreadLocal@2b9ff8a8]) and a value of type [org.testcontainers.shaded.io.netty.util.internal.InternalThreadLocalMap] (value [org.testcontainers.shaded.io.netty.util.internal.InternalThreadLocalMap@34e88997]) but failed to remove it when the web application was stopped. Threads are going to be renewed over time to try and avoid a probable memory leak.
```

This leaves Tomcat hanging.

To reproduce the problem either run the `integrationTest` Gradle task using `./gradlew integrationTest` (this uses [gretty](https://akhikhl.github.io/gretty-doc/) to integration test the app on Tomcat 8). Alternatively, build the WAR using `./gradlew war` and run it on Tomcat (in this case the `-Dspring.profiles.active=redisContainer` VM parameter is needed).
