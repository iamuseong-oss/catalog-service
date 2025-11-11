package com.polarbookshop.book.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polarbookshop.config.SecurityConfig;
import com.polarbookshop.domain.Book;
import com.polarbookshop.domain.BookNotFoundException;
import com.polarbookshop.domain.BookService;
import com.polarbookshop.web.BookController;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import({ SecurityConfig.class })
public class BookControllerMvcTests {

  private static final String ROLE_EMPLOYEE = "ROLE_employee";

  private static final String ROLE_CUSTOMER = "ROLE_customer";

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockitoBean
  BookService bookService;

  @Test
  public void whenGetBookExistingAndAuthenticatedThenShouldReturn200() throws Exception {
    var isbn = "7373731394";
    var expectedBook = Book.builder()
        .isbn(isbn)
        .title("Title")
        .author("Author")
        .price(9.90)
        .publisher("Polarsophia")
        .build();

    given(bookService.viewBookDetails(isbn)).willReturn(expectedBook);
    mockMvc.perform(get("/books/" + isbn)
        .with(jwt()))
        .andExpect(status().isOk());
  }

  @Test
  void whenGetBookExistingAndNotAuthenticatedThenShouldReturn200() throws Exception {
    var isbn = "7373731394";
    var expectedBook = Book.builder()
        .isbn(isbn)
        .title("Title")
        .author("Author")
        .price(9.90)
        .publisher("Polarsophia")
        .build();
    given(bookService.viewBookDetails(isbn)).willReturn(expectedBook);
    mockMvc
        .perform(get("/books/" + isbn))
        .andExpect(status().isOk());
  }

  @Test
  void whenGetBookNotExistingAndAuthenticatedThenShouldReturn404() throws Exception {
    var isbn = "7373731394";
    given(bookService.viewBookDetails(isbn)).willThrow(BookNotFoundException.class);
    mockMvc
        .perform(get("/books/" + isbn)
            .with(jwt()))
        .andExpect(status().isNotFound());
  }

  @Test
  void whenGetBookNotExistingAndNotAuthenticatedThenShouldReturn404() throws Exception {
    var isbn = "7373731394";
    given(bookService.viewBookDetails(isbn)).willThrow(BookNotFoundException.class);
    mockMvc
        .perform(get("/books/" + isbn))
        .andExpect(status().isNotFound());
  }

  @Test
  void whenDeleteBookWithEmployeeRoleThenShouldReturn204() throws Exception {
    var isbn = "7373731394";
    mockMvc
        .perform(delete("/books/" + isbn)
            .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_EMPLOYEE))))
        .andExpect(status().isNoContent());
  }

  @Test
  void whenDeleteBookWithCustomerRoleThenShouldReturn403() throws Exception {
    var isbn = "7373731394";
    mockMvc
        .perform(delete("/books/" + isbn)
            .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_CUSTOMER))))
        .andExpect(status().isForbidden());
  }

  @Test
  void whenDeleteBookNotAuthenticatedThenShouldReturn401() throws Exception {
    var isbn = "7373731394";
    mockMvc
        .perform(delete("/books/" + isbn))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void whenPostBookWithEmployeeRoleThenShouldReturn201() throws Exception {
    var isbn = "7373731394";
    var bookToCreate = Book.builder()
        .isbn(isbn)
        .title("Title")
        .author("Author")
        .price(9.90)
        .publisher("Polarsophia")
        .build();
    given(bookService.addBookToCatalog(bookToCreate)).willReturn(bookToCreate);
    mockMvc
        .perform(post("/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookToCreate))
            .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_EMPLOYEE))))
        .andExpect(status().isCreated());
  }

  @Test
  void whenPostBookWithCustomerRoleThenShouldReturn403() throws Exception {
    var isbn = "7373731394";
    var bookToCreate = Book.builder()
        .isbn(isbn)
        .title("Title")
        .author("Author")
        .price(9.90)
        .publisher("Polarsophia")
        .build();
    given(bookService.addBookToCatalog(bookToCreate)).willReturn(bookToCreate);
    mockMvc
        .perform(post("/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookToCreate))
            .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_CUSTOMER))))
        .andExpect(status().isForbidden());
  }

  @Test
  void whenPostBookAndNotAuthenticatedThenShouldReturn403() throws Exception {
    var isbn = "7373731394";
    var bookToCreate = Book.builder()
        .isbn(isbn)
        .title("Title")
        .author("Author")
        .price(9.90)
        .publisher("Polarsophia")
        .build();
    mockMvc
        .perform(post("/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookToCreate)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void whenPutBookWithEmployeeRoleThenShouldReturn200() throws Exception {
    var isbn = "7373731394";
    var bookToCreate = Book.builder()
        .isbn(isbn)
        .title("Title")
        .author("Author")
        .price(9.90)
        .publisher("Polarsophia")
        .build();
    given(bookService.addBookToCatalog(bookToCreate)).willReturn(bookToCreate);
    mockMvc
        .perform(put("/books/" + isbn)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookToCreate))
            .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_EMPLOYEE))))
        .andExpect(status().isOk());
  }

  @Test
  void whenPutBookWithCustomerRoleThenShouldReturn403() throws Exception {
    var isbn = "7373731394";
    var bookToCreate = Book.builder()
        .isbn(isbn)
        .title("Title")
        .author("Author")
        .price(9.90)
        .publisher("Polarsophia")
        .build();
    given(bookService.addBookToCatalog(bookToCreate)).willReturn(bookToCreate);
    mockMvc
        .perform(put("/books/" + isbn)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookToCreate))
            .with(jwt().authorities(new SimpleGrantedAuthority(ROLE_CUSTOMER))))
        .andExpect(status().isForbidden());
  }

  @Test
  void whenPutBookAndNotAuthenticatedThenShouldReturn401() throws Exception {
    var isbn = "7373731394";
    var bookToCreate = Book.builder()
        .isbn(isbn)
        .title("Title")
        .author("Author")
        .price(9.90)
        .publisher("Polarsophia")
        .build();
    mockMvc
        .perform(put("/books/" + isbn)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookToCreate)))
        .andExpect(status().isUnauthorized());
  }

}
