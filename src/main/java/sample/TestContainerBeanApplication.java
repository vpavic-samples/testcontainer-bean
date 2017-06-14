package sample;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;

@SpringBootApplication
public class TestContainerBeanApplication extends SpringBootServletInitializer
		implements CommandLineRunner {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(TestContainerBeanApplication.class);
	}

	@Override
	public void run(String... args) throws Exception {
		this.redisTemplate.boundValueOps(getClass().getSimpleName()).set(UUID.randomUUID().toString());
	}

	public static void main(String[] args) {
		SpringApplication.run(TestContainerBeanApplication.class, args);
	}

	@Configuration
	@Profile("redisContainer")
	static class RedisContainerConfig {

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

	}

}
