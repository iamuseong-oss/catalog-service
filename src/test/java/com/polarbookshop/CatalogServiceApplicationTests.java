package com.polarbookshop;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.polarbookshop.domain.Book;
import com.polarbookshop.domain.BookRepository;

import dasniko.testcontainers.keycloak.KeycloakContainer;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@Testcontainers
class CatalogServiceApplicationTests {

	private static KeycloakToken bjornTokens;
	private static KeycloakToken isabelleTokens;

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private BookRepository bookRepository;

	@SuppressWarnings("resource")
	@Container
	private static final KeycloakContainer keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:26.0")
			.withRealmImportFile("/test-realm-config.json");

	@DynamicPropertySource
	static void dynamicProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
				() -> keycloakContainer.getAuthServerUrl() + "/realms/PolarBookshop");
	}

	private record KeycloakToken(String accessToken) {
		@JsonCreator
		private KeycloakToken(@JsonProperty("access_token") final String accessToken) {
			this.accessToken = accessToken;
		}
	}

	@BeforeEach
	void cleanUp() {
		bookRepository.deleteAll();
	}

	@BeforeAll
	static void generateAccessTokens() {
		WebClient webClient = WebClient.builder()
				.baseUrl(keycloakContainer.getAuthServerUrl() + "/realms/PolarBookshop/protocol/openid-connect/token")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.build();

		isabelleTokens = authenticateWith("isabelle", "password", webClient);
		bjornTokens = authenticateWith("bjorn", "password", webClient);
	}

	@Test
	void whenGetRequestWithIdThenBookReturned() {
		var bookIsbn = "1231231230";
		var bookToCreate = Book.builder()
				.isbn(bookIsbn)
				.title("Title")
				.author("Author")
				.price(9.90)
				.publisher("Polarsophia")
				.build();

		Book expectedBook = webTestClient
				.post()
				.uri("/books")
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Book.class).value(book -> Assertions.assertNotNull(book))
				.returnResult().getResponseBody();

		webTestClient
				.get()
				.uri("/books/" + bookIsbn)
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(Book.class).value(actualBook -> {
					Assertions.assertNotNull(actualBook);
					Assertions.assertEquals(actualBook.getIsbn(), expectedBook.getIsbn());
				});
	}

	@Test
	void whenPostRequestThenBookCreated() {
		var expectedBook = Book.builder()
				.isbn("1231231230")
				.title("Title")
				.author("Author")
				.price(9.90)
				.publisher("Polarsophia")
				.build();

		webTestClient
				.post()
				.uri("/books")
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
				.bodyValue(expectedBook)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Book.class).value(actualBook -> {
					Assertions.assertNotNull(actualBook);
					Assertions.assertEquals(actualBook.getIsbn(), expectedBook.getIsbn());
				});
	}

	@Test
	void whenPostRequestUnauthenticatedThen401() {
		var expectedBook = Book.builder()
				.isbn("1231231230")
				.title("Title")
				.author("Author")
				.price(9.90)
				.publisher("Polarsophia")
				.build();

		webTestClient
				.post()
				.uri("/books")
				.bodyValue(expectedBook)
				.exchange()
				.expectStatus().isUnauthorized();
	}

	@Test
	void whenPostRequestUnauthorizedThen403() {
		var expectedBook = Book.builder()
				.isbn("1231231230")
				.title("Title")
				.author("Author")
				.price(9.90)
				.publisher("Polarsophia")
				.build();

		webTestClient
				.post()
				.uri("/books")
				.headers(headers -> headers.setBearerAuth(bjornTokens.accessToken()))
				.bodyValue(expectedBook)
				.exchange()
				.expectStatus().isForbidden();
	}

	@Test
	void whenPutRequestThenBookUpdated() {
		var bookIsbn = "1231231232";

		var bookToCreate = Book.builder()
				.isbn(bookIsbn)
				.title("Title")
				.author("Author")
				.price(9.90)
				.publisher("Polarsophia")
				.build();

		Book createdBook = webTestClient
				.post()
				.uri("/books")
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Book.class).value(book -> Assertions.assertNotNull(book))
				.returnResult().getResponseBody();

		var bookToUpdate = Book.builder()
				.isbn(createdBook.getIsbn())
				.id(createdBook.getId())
				.title(createdBook.getTitle())
				.author(createdBook.getAuthor())
				.price(7.95)
				.publisher(createdBook.getPublisher())
				.createdAt(createdBook.getCreatedAt())
				.updatedAt(createdBook.getUpdatedAt())
				.version(createdBook.getVersion())
				.build();

		webTestClient
				.put()
				.uri("/books/" + bookIsbn)
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
				.bodyValue(bookToUpdate)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Book.class).value(actualBook -> {
					Assertions.assertNotNull(actualBook);
					Assertions.assertEquals(actualBook.getPrice(), bookToUpdate.getPrice());
				});
	}

	@Test
	void whenDeleteRequestThenBookDeleted() {
		var bookIsbn = "1231231230";

		var bookToCreate = Book.builder()
				.isbn(bookIsbn)
				.title("Title")
				.author("Author")
				.price(9.90)
				.publisher("Polarsophia")
				.build();

		webTestClient
				.post()
				.uri("/books")
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated();

		webTestClient
				.delete()
				.uri("/books/" + bookIsbn)
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
				.exchange()
				.expectStatus().isNoContent();

		webTestClient
				.get()
				.uri("/books/" + bookIsbn)
				.exchange()
				.expectStatus().isNotFound()
				.expectBody(String.class).value(
						errorMessage -> Assertions.assertEquals(errorMessage,
								"The book with ISBN " + bookIsbn + " was not found."));
	}

	private static KeycloakToken authenticateWith(String username, String password, WebClient webClient) {
		return webClient.post()
				.body(BodyInserters.fromFormData("grant_type", "password")
						.with("client_id", "polar-test")
						.with("username", username)
						.with("password", password))
				.retrieve()
				.bodyToMono(KeycloakToken.class)
				.block();
	}

}
