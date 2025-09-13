package useong.bookshop.catalogservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CatalogServiceTests {

  public static void main(String[] args) {
    SpringApplication.from(CatalogServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
  }
}
