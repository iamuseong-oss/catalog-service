package useong.bookshop.catalogservice.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;

public interface BookRepository extends JpaRepository<Book, Long> {
  Optional<Book> findByIsbn(String isbn);

  boolean existsByIsbn(String isbn);

  @Modifying
  @Transactional
  @Query("DELETE FROM Book WHERE isbn = :isbn")
  void deleteByIsbn(String isbn);
}
