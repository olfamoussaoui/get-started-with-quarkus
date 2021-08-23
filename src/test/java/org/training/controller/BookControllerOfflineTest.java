package org.training.controller;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.training.model.Book;

import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;

@QuarkusTest
public class BookControllerOfflineTest {
    private final BookController bookController;

    public BookControllerOfflineTest(BookController bookController) {
        this.bookController = bookController;
    }

    @BeforeEach
    public void beforeEach() {
        this.bookController.deleteBooks();
    }

    @Test
    public void getBook_with_book_exist_expected_book_reponse() {
        final var savedBook = new Book("123", "Quarkus cookbook");
        this.bookController.saveBook(savedBook);

        final var response = this.bookController.getBook(savedBook.getId());
        Assertions.assertAll(
                () -> Assertions.assertEquals(savedBook, response.getEntity()),
                () -> Assertions.assertEquals(Response.Status.OK, response.getStatusInfo()),
                () -> Assertions.assertFalse(response.getHeaders().containsKey("error"))
        );
    }

    @Test
    public void getBook_with_book_id_null_expected_bad_request() {
        final var response = this.bookController.getBook(null);
        Assertions.assertAll(
                () -> Assertions.assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo()),
                () -> Assertions.assertTrue(response.getHeaders().containsKey("error"))
        );
    }

    @Test
    public void getBook_with_book_id_empty_expected_bad_request() {
        final var response = this.bookController.getBook("");

        Assertions.assertAll(
                () -> Assertions.assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo()),
                () -> Assertions.assertTrue(response.getHeaders().containsKey("error"))
        );
    }

    @Test
    public void getBook_with_book_inexist_expected_bad_request() {
        final var response = this.bookController.getBook("345");

        Assertions.assertAll(
                () -> Assertions.assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo()),
                () -> Assertions.assertTrue(response.getHeaders().containsKey("error"))
        );
    }

    @Test
    public void getBooks_3_books_expected_3_books_returned() {
        final var booksToBeSaved = List.of(
                new Book("123", "Quarkus cookbook"),
                new Book("234", "Java cookbook"),
                new Book("345", "Angular cookbook")
        );
        this.bookController.saveBooks(booksToBeSaved);

        final var booksResponse = this.bookController.getBooks();
        Assertions.assertAll(
                () -> Assertions.assertEquals(3, booksResponse.readEntity(Collection.class).size()),
                () -> Assertions.assertEquals(booksToBeSaved, booksResponse.getEntity())
        );
    }

    @Test
    public void saveBook_with_valid_book_expected_1_book_saved() {
        final var bookToBeSave = new Book("123", "Quarkus cookbook!");
        final var savedBook = this.bookController.saveBook(bookToBeSave);
        final var bookResponse = this.bookController.getBook(bookToBeSave.getId());

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, this.bookController.getBooks().readEntity(Collection.class).size()),
                () -> Assertions.assertEquals(bookToBeSave, bookResponse.getEntity()),
                () -> Assertions.assertEquals(bookToBeSave, savedBook.getEntity()),
                () -> Assertions.assertEquals(Response.Status.OK, savedBook.getStatusInfo())
        );
    }

    @Test
    public void saveBook_with_book_id_null_expected_bad_request() {
        final var bookToBeSave = new Book(null, "Quarkus cookbook!");
        final var savedBook = this.bookController.saveBook(bookToBeSave);

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, this.bookController.getBooks().readEntity(Collection.class).size()),
                () -> Assertions.assertEquals(Response.Status.BAD_REQUEST, savedBook.getStatusInfo()),
                () -> Assertions.assertTrue(savedBook.getHeaders().containsKey("error"))
        );
    }

    @Test
    public void saveBook_with_book_id_empty_expected_bad_request() {
        final var bookToBeSave = new Book("", "Quarkus cookbook!");
        final var savedBook = this.bookController.saveBook(bookToBeSave);

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, this.bookController.getBooks().readEntity(Collection.class).size()),
                () -> Assertions.assertEquals(Response.Status.BAD_REQUEST, savedBook.getStatusInfo()),
                () -> Assertions.assertTrue(savedBook.getHeaders().containsKey("error"))
        );
    }

    @Test
    public void saveBook_with_book_name_null_expected_bad_request() {
        final var bookToBeSave = new Book("123", null);
        final var savedBook = this.bookController.saveBook(bookToBeSave);

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, this.bookController.getBooks().readEntity(Collection.class).size()),
                () -> Assertions.assertEquals(Response.Status.BAD_REQUEST, savedBook.getStatusInfo()),
                () -> Assertions.assertTrue(savedBook.getHeaders().containsKey("error"))
        );
    }

    @Test
    public void saveBook_with_book_name_empty_expected_bad_request() {
        final var bookToBeSave = new Book("123", "");
        final var savedBook = this.bookController.saveBook(bookToBeSave);

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, this.bookController.getBooks().readEntity(Collection.class).size()),
                () -> Assertions.assertEquals(Response.Status.BAD_REQUEST, savedBook.getStatusInfo()),
                () -> Assertions.assertTrue(savedBook.getHeaders().containsKey("error"))
        );
    }

    @Test
    public void saveBook_with_book_id_exist_expected_bad_request() {
        final var bookToBeSave = new Book("123", "Quarkus cookbook");
        this.bookController.saveBook(bookToBeSave);
        final var savedBook = this.bookController.saveBook(new Book(bookToBeSave.getId(), "Java cookbook"));

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, this.bookController.getBooks().readEntity(Collection.class).size()),
                () -> Assertions.assertEquals(Response.Status.BAD_REQUEST, savedBook.getStatusInfo()),
                () -> Assertions.assertTrue(savedBook.getHeaders().containsKey("error"))
        );

    }
    @Test
    public void saveBook_with_book_null_expected_bad_request() {
        final var savedBook = this.bookController.saveBook(null);
        Assertions.assertAll(
                () -> Assertions.assertEquals(0, this.bookController.getBooks().readEntity(Collection.class).size()),
                () -> Assertions.assertEquals(Response.Status.BAD_REQUEST, savedBook.getStatusInfo()),
                () -> Assertions.assertTrue(savedBook.getHeaders().containsKey("error"))
        );
    }
}
