package useong.bookshop.catalogservice.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
  Optional<Book> findByIsbn(String isbn);

  boolean existsByIsbn(String isbn);

  void deleteByIsbn(String isbn);
}
