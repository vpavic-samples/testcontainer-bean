package sample;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestContainerBeanApplication.class)
@ContextConfiguration(initializers = TestContainerBeanApplicationTests.Initializer.class)
public class TestContainerBeanApplicationTests {

	@ClassRule
	public static GenericContainer redisContainer = new GenericContainer("redis:3.2.9")
			.withExposedPorts(6379);

	@Test
	public void contextLoads() {
	}

	public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		@Override
		public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
			EnvironmentTestUtils.addEnvironment("testcontainers",
					configurableApplicationContext.getEnvironment(),
					"spring.redis.host=" + redisContainer.getContainerIpAddress(),
					"spring.redis.port=" + redisContainer.getMappedPort(6379));
		}

	}

}
