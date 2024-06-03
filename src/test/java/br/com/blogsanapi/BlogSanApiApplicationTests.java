package br.com.blogsanapi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

import br.com.blogsanapi.integration.H2Test;

@SpringBootTest
@ActiveProfiles("test")
@PropertySource("classpath:application-test.properties")
@ExtendWith(H2Test.class)
class BlogSanApiApplicationTests {

	@Test
	void contextLoads() {
	}
}