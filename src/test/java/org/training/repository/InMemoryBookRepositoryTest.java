package org.training.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.training.model.Book;

import java.util.List;
import java.util.NoSuchElementException;

public class InMemoryBookRepositoryTest {
    private final IBookRepository bookRepository;

    public InMemoryBookRepositoryTest() {
        this.bookRepository = new InMemoryBookRepository();
    }

    @BeforeEach
    private void beforeEach() {
        this.bookRepository.deleteAll();
    }

    @Test
    public void save_one_valid_book_expected_book_saved() {
        final var bookToBeSaved = new Book("123", "Quarkus cookbook");
        final var savedBook = this.bookRepository.saveOne(bookToBeSaved);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, this.bookRepository.findAll().size()),
                () -> Assertions.assertEquals(bookToBeSaved, savedBook)
        );
    }

    @Test
    public void save_all_3_books_expected_3_books_saved() {
        final var booksToBeSaved = List.of(
                new Book("123", "Quarkus cookbook"),
                new Book("234", "Java cookbook"),
                new Book("345", "Angular cookbook")
        );
        final var booksSaved = this.bookRepository.saveAll(booksToBeSaved);

        Assertions.assertAll(
                () -> Assertions.assertEquals(3, this.bookRepository.findAll().size()),
                () -> Assertions.assertEquals(booksToBeSaved, booksSaved)
        );
    }

    @Test
    public void find_one_by_id_exist_expected_book_found() {
        final var bookToBeSaved = new Book("123", "Quarkus cookbook");
        final var bookSaved = this.bookRepository.saveOne(bookToBeSaved);

        final var bookFound = this.bookRepository.findOneById(bookSaved.getId()).get();

        Assertions.assertEquals(bookSaved, bookFound);

    }

    @Test
    public void find_one_by_id_not_exist_expected_NoSuchElementException() {
        final var bookToBeSaved = new Book("123", "Quarkus cookbook");
        this.bookRepository.saveOne(bookToBeSaved);

        Assertions.assertThrows(
                NoSuchElementException.class,
                () -> this.bookRepository.findOneById("333").get()
        );
    }

    @Test
    public void find_all_3_books_expected_3_books_found() {
        final var booksToBeSaved = List.of(
                new Book("123", "Quarkus cookbook"),
                new Book("234", "Java cookbook"),
                new Book("345", "Angular cookbook")
        );
        final var booksSaved = this.bookRepository.saveAll(booksToBeSaved);

        Assertions.assertEquals(3, this.bookRepository.findAll().size());
    }

    @Test
    public void update_one_valid_book_expected_book_updated() {
        final var bookToBeSaved = new Book("123", "Quarkus cookbook");
        this.bookRepository.saveOne(bookToBeSaved);

        final var bookToBeUpdated = new Book(bookToBeSaved.getId(), "Java cookbook");
        final var updatedBook = this.bookRepository.updateOne(bookToBeUpdated).get();

        Assertions.assertAll(
                () -> Assertions.assertEquals(bookToBeUpdated, updatedBook),
                () -> Assertions.assertEquals(bookToBeUpdated, this.bookRepository.findOneById(bookToBeSaved.getId()).get())
        );
    }

    @Test
    public void update_one_by_id_not_exist_expected_NoSuchElementException() {
        final var bookToBeSaved = new Book("123", "Quarkus cookbook");
        this.bookRepository.saveOne(bookToBeSaved);

        final var bookToBeUpdated = new Book("333", "Java cookbook");

        Assertions.assertThrows(
                NoSuchElementException.class,
                () -> this.bookRepository.updateOne(bookToBeUpdated).get()
        );
    }

    @Test
    public void delete_one_by_id_exist_expected_book_deleted() {
        final var bookToBeSaved = new Book("123", "Quarkus cookbook");
        this.bookRepository.saveOne(bookToBeSaved);

        final var deletedBook = this.bookRepository.deleteOneById(bookToBeSaved.getId()).get();

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, this.bookRepository.findAll().size()),
                () -> Assertions.assertEquals(bookToBeSaved, deletedBook)
        );
    }

    @Test
    public void delete_one_by_id_not_exist_expected_NoSuchElementException() {
        final var bookToBeSaved = new Book("123", "Quarkus cookbook");
        this.bookRepository.saveOne(bookToBeSaved);

        Assertions.assertThrows(
                NoSuchElementException.class,
                () -> this.bookRepository.deleteOneById("333").get()
        );
    }

    @Test
    public void delete_all_3_books_expected_3_books_deleted() {
        final var booksToBeSaved = List.of(
                new Book("123", "Quarkus cookbook"),
                new Book("234", "Java cookbook"),
                new Book("345", "Angular cookbook")
        );
        this.bookRepository.saveAll(booksToBeSaved);
        this.bookRepository.deleteAll();

        Assertions.assertEquals(0, this.bookRepository.findAll().size());
    }

    @Test
    public void is_exist_by_id_exist_expected_true() {
        final var bookToBeSaved = new Book("123", "Quarkus cookbook");
        this.bookRepository.saveOne(bookToBeSaved);

        final var isBookExist = this.bookRepository.isExist(bookToBeSaved.getId());
        Assertions.assertTrue(isBookExist);
    }

    @Test
    public void is_exist_by_id_not_exist_expected_false() {
        final var bookToBeSaved = new Book("123", "Quarkus cookbook");
        this.bookRepository.saveOne(bookToBeSaved);

        final var isBookExist = this.bookRepository.isExist("333");
        Assertions.assertFalse(isBookExist);
    }

}
