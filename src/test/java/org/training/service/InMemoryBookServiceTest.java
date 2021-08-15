package org.training.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.training.model.Book;
import org.training.repository.IBookRepository;
import org.training.repository.InMemoryBookRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryBookServiceTest {
    private final BookService bookService;

    public InMemoryBookServiceTest() {
        final IBookRepository bookRepository = new InMemoryBookRepository();
        bookService = new BookService(bookRepository);
    }

    @BeforeEach
    private void beforeEach() {
        bookService.deleteAll();
    }

    @Test
    public void save_one_valid_book_expected_one_book_inserted() {
        final var bookToBeSaved = new Book("123", "Java Cookbook");
        final var savedBook = this.bookService.saveOne(bookToBeSaved);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, this.bookService.findAll().size()),
                () -> Assertions.assertEquals(bookToBeSaved, savedBook.get())
        );
    }

    @Test
    public void save_one_book_with_null_id_expected_BookIdEmptyOrNullException() {
        final var bookToBeSave = new Book(null, "Java CookBook");
        final var savedBook = this.bookService.saveOne(bookToBeSave);

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, this.bookService.findAll().size()),
                () -> org.hamcrest.MatcherAssert.assertThat(
                        savedBook.getLeft(),
                        org.hamcrest.CoreMatchers.instanceOf(BookService.BookException.BookIdEmptyOrNullException.class)
                )
        );
    }

    @Test
    public void save_one_book_with_empty_id_expected_BookIdEmptyOrNullException() {
        final var bookToBeSave = new Book("", "Java CookBook");
        final var savedBook = this.bookService.saveOne(bookToBeSave);

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, this.bookService.findAll().size()),
                () -> org.hamcrest.MatcherAssert.assertThat(
                        savedBook.getLeft(),
                        org.hamcrest.CoreMatchers.instanceOf(BookService.BookException.BookIdEmptyOrNullException.class)
                )
        );
    }

    @Test
    public void save_one_book_with_null_name_expected_BookNameEmptyOrNullException() {
        final var bookToBeSave = new Book("123", null);
        final var savedBook = this.bookService.saveOne(bookToBeSave);

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, this.bookService.findAll().size()),
                () -> org.hamcrest.MatcherAssert.assertThat(
                        savedBook.getLeft(),
                        org.hamcrest.CoreMatchers.instanceOf(BookService.BookException.BookNameEmptyOrNullException.class)
                )
        );
    }

    @Test
    public void save_one_book_with_empty_name_expected_BookNameEmptyOrNullException() {
        final var bookToBeSave = new Book("123", "");
        final var savedBook = this.bookService.saveOne(bookToBeSave);

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, this.bookService.findAll().size()),
                () -> org.hamcrest.MatcherAssert.assertThat(
                        savedBook.getLeft(),
                        org.hamcrest.CoreMatchers.instanceOf(BookService.BookException.BookNameEmptyOrNullException.class)
                )
        );
    }

    @Test
    public void save_one_existed_book_expected_BookAlreadyExistException() {
        final var bookToBeSave = new Book("123", "Java Cookbook");
        this.bookService.saveOne(bookToBeSave);
        final var savedBook = this.bookService.saveOne(bookToBeSave);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, this.bookService.findAll().size()),
                () -> org.hamcrest.MatcherAssert.assertThat(
                        savedBook.getLeft(),
                        org.hamcrest.CoreMatchers.instanceOf(BookService.BookException.BookAlreadyExistException.class)
                )
        );
    }

    @Test
    public void save_one_null_book_expected_BookNullException() {
        final var savedBook = this.bookService.saveOne(null);

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, this.bookService.findAll().size()),
                () -> org.hamcrest.MatcherAssert.assertThat(
                        savedBook.getLeft(),
                        org.hamcrest.CoreMatchers.instanceOf(BookService.BookException.BookNullException.class)
                )
        );
    }

    @Test
    public void save_all_3_valid_books_expected_3_books_saved() {
        final var booksToBeSave = List.of(
                new Book("123", "Java Cookbook"),
                new Book("234", "Quarkus Cookbook"),
                new Book("345", "Micronaut Cookbook")
        );
        final var savedBooks = this.bookService.saveAll(booksToBeSave);

        Assertions.assertAll(
                () -> Assertions.assertEquals(3, this.bookService.findAll().size()),
                () -> Assertions.assertEquals(booksToBeSave, savedBooks.getSavedBooks()),
                () -> Assertions.assertTrue(savedBooks.getUnsavedBooks().isEmpty())
        );

    }

    @Test
    public void save_all_3_invalid_books_expected_0_books_saved() {
        final var booksToBeSave = List.of(
                new Book(" ", "Java Cookbook"),
                new Book("", null),
                new Book(null, "Micronaut Cookbook")
        );
        final var savedBooks = this.bookService.saveAll(booksToBeSave);

        Assertions.assertAll(
                () -> Assertions.assertTrue(this.bookService.findAll().isEmpty()),
                () -> Assertions.assertTrue(savedBooks.getSavedBooks().isEmpty()),
                () -> Assertions.assertEquals(3, savedBooks.getUnsavedBooks().size()),
                () -> Assertions.assertEquals(
                        booksToBeSave,
                        savedBooks
                                .getUnsavedBooks()
                                .stream()
                                .map(BookService.BooksRecord.UnsavedBooks::getBook)
                                .collect(Collectors.toUnmodifiableList())
                )
        );
    }

    @Test
    public void save_all_3_books_with_one_invalid_book_expected_2_books_saved() {
        final var booksToBeSave = List.of(
                new Book("123", "Java Cookbook"),
                new Book("234", "Quarkus Cookbook"),
                new Book("", "Micronaut Cookbook")
        );
        final var savedBooks = this.bookService.saveAll(booksToBeSave);

        Assertions.assertAll(

                () -> Assertions.assertEquals(1, savedBooks.getUnsavedBooks().size()),
                () -> Assertions.assertEquals(2, savedBooks.getSavedBooks().size()),
                () -> Assertions.assertEquals(2, this.bookService.findAll().size())
        );
    }

    @Test
    public void save_all_one_null_book_expected_BookNullException() {
        final var booksToBeSave = new ArrayList<Book>();
        booksToBeSave.add(null);
        final var savedBooks = this.bookService.saveAll(booksToBeSave);

        org.hamcrest.MatcherAssert.assertThat(
                savedBooks
                        .getUnsavedBooks()
                        .stream()
                        .map(BookService.BooksRecord.UnsavedBooks::getReason)
                        .findFirst()
                        .get(),
                org.hamcrest.CoreMatchers.instanceOf(BookService.BookException.BookNullException.class)
        );
    }

    @Test
    public void findOneById_existed_book_expected_one_book_returned() {
        final var bookToBeSave = new Book("123", "Java CookBook");
        this.bookService.saveOne(bookToBeSave);
        final var returnedBook = this.bookService.findOneById(bookToBeSave.getId());

        Assertions.assertEquals(bookToBeSave, returnedBook.get());
    }

    @Test
    public void findOneById_inexisted_book_expexted_BookNotFoundException() {
        final var bookToBeSave = new Book("123", "Java CookBook");
        this.bookService.saveOne(bookToBeSave);

        final var returnedBook = this.bookService.findOneById("234");

        org.hamcrest.MatcherAssert.assertThat(
                returnedBook.getLeft(),
                org.hamcrest.CoreMatchers.instanceOf(BookService.BookException.BookNotFoundException.class)
        );
    }

    @Test
    public void findOneById_book_with_null_id_expected_BookIdNullOrEmptyException() {
        final var bookToBeSave = new Book("123", "Java CookBook");
        this.bookService.saveOne(bookToBeSave);

        final var returnedBook = this.bookService.findOneById(null);

        org.hamcrest.MatcherAssert.assertThat(
                returnedBook.getLeft(),
                org.hamcrest.CoreMatchers.instanceOf(BookService.BookException.BookIdEmptyOrNullException.class)
        );
    }

    @Test
    public void findOneById_book_with_empty_id_expected_BookIdNullOrEmptyException() {
        final var bookToBeSave = new Book("123", "Java CookBook");
        this.bookService.saveOne(bookToBeSave);

        final var returnedBook = this.bookService.findOneById("");

        org.hamcrest.MatcherAssert.assertThat(
                returnedBook.getLeft(),
                org.hamcrest.CoreMatchers.instanceOf(BookService.BookException.BookIdEmptyOrNullException.class)
        );
    }

    @Test
    public void findAll_3_books_expected_3_books_returned() {
        final var booksToBeSave = List.of(
                new Book("123", "Java Cookbook"),
                new Book("234", "Quarkus Cookbook"),
                new Book("456", "Micronaut Cookbook")
        );
        final var savedBooks = this.bookService.saveAll(booksToBeSave);

        Assertions.assertEquals(3, this.bookService.findAll().size());
    }

    @Test
    public void updateOne_book_with_id_existed_expected_one_book_updated() {
        final var bookToBeSave = new Book("123", "Java CookBook");
        this.bookService.saveOne(bookToBeSave);

        final var bookToBeUpdate = new Book(bookToBeSave.getId(), "Quarkus");
        final var updatedBook = this.bookService.updateOne(bookToBeUpdate);

        Assertions.assertAll(
                () -> Assertions.assertEquals(bookToBeUpdate, updatedBook.get()),
                () -> Assertions.assertEquals(1, this.bookService.findAll().size()),
                () -> Assertions.assertEquals(bookToBeUpdate, this.bookService.findOneById(bookToBeSave.getId()).get())
        );
    }

    @Test
    public void updateOne_book_with_id_inexisted_expected_BookNotFoundException() {
        final var bookToBeSave = new Book("123", "Java CookBook");
        this.bookService.saveOne(bookToBeSave);

        final var bookToBeUpdate = new Book("234", "Quarkus");
        final var updatedBook = this.bookService.updateOne(bookToBeUpdate);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, this.bookService.findAll().size()),
                () -> Assertions.assertEquals(bookToBeSave, this.bookService.findOneById(bookToBeSave.getId()).get()),
                () -> org.hamcrest.MatcherAssert.assertThat(
                        updatedBook.getLeft(),
                        org.hamcrest.CoreMatchers.instanceOf(BookService.BookException.BookNotFoundException.class)
                )
        );
    }

    @Test
    public void updateOne_book_with_id_null_expected_BookIdNullOrEmptyException() {
        final var bookToBeSave = new Book("123", "Java CookBook");
        this.bookService.saveOne(bookToBeSave);

        final var bookToBeUpdate = new Book(null, "Quarkus");
        final var updatedBook = this.bookService.updateOne(bookToBeUpdate);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, this.bookService.findAll().size()),
                () -> Assertions.assertEquals(bookToBeSave, this.bookService.findOneById(bookToBeSave.getId()).get()),
                () -> org.hamcrest.MatcherAssert.assertThat(
                        updatedBook.getLeft(),
                        org.hamcrest.CoreMatchers.instanceOf(BookService.BookException.BookIdEmptyOrNullException.class)
                )
        );
    }

    @Test
    public void updateOne_book_with_id_empty_expected_BookIdNullOrEmptyException() {
        final var bookToBeSave = new Book("123", "Java CookBook");
        this.bookService.saveOne(bookToBeSave);

        final var bookToBeUpdate = new Book("", "Quarkus");
        final var updatedBook = this.bookService.updateOne(bookToBeUpdate);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, this.bookService.findAll().size()),
                () -> Assertions.assertEquals(bookToBeSave, this.bookService.findOneById(bookToBeSave.getId()).get()),
                () -> org.hamcrest.MatcherAssert.assertThat(
                        updatedBook.getLeft(),
                        org.hamcrest.CoreMatchers.instanceOf(BookService.BookException.BookIdEmptyOrNullException.class)
                )
        );
    }

    @Test
    public void updateOne_book_with_name_null_expected_BookNameNullOrEmptyException() {
        final var bookToBeSave = new Book("123", "Java CookBook");
        this.bookService.saveOne(bookToBeSave);

        final var bookToBeUpdate = new Book("123", null);
        final var updatedBook = this.bookService.updateOne(bookToBeUpdate);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, this.bookService.findAll().size()),
                () -> Assertions.assertEquals(bookToBeSave, this.bookService.findOneById(bookToBeSave.getId()).get()),
                () -> org.hamcrest.MatcherAssert.assertThat(
                        updatedBook.getLeft(),
                        org.hamcrest.CoreMatchers.instanceOf(BookService.BookException.BookNameEmptyOrNullException.class)
                )
        );
    }

    @Test
    public void updateOne_book_with_name_empty_expected_BookNameNullOrEmptyException() {
        final var bookToBeSave = new Book("123", "Java CookBook");
        this.bookService.saveOne(bookToBeSave);

        final var bookToBeUpdate = new Book("123", "");
        final var updatedBook = this.bookService.updateOne(bookToBeUpdate);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, this.bookService.findAll().size()),
                () -> Assertions.assertEquals(bookToBeSave, this.bookService.findOneById(bookToBeSave.getId()).get()),
                () -> org.hamcrest.MatcherAssert.assertThat(
                        updatedBook.getLeft(),
                        org.hamcrest.CoreMatchers.instanceOf(BookService.BookException.BookNameEmptyOrNullException.class)
                )
        );
    }

    @Test
    public void updateOne_null_book_expected_BookNullExpection() {
        final var updatedBook = this.bookService.updateOne(null);

        org.hamcrest.MatcherAssert.assertThat(
                updatedBook.getLeft(),
                org.hamcrest.CoreMatchers.instanceOf(BookService.BookException.BookNullException.class)
        );
    }

    @Test
    public void deleteOneById_book_with_id_existed_expected_one_book_deleted() {
        final var bookToBeSave = new Book("123", "Java CookBook");
        this.bookService.saveOne(bookToBeSave);

        final var deletedBook = this.bookService.deleteOneById(bookToBeSave.getId());
        Assertions.assertAll(
                () -> Assertions.assertEquals(bookToBeSave, deletedBook.get()),
                () -> Assertions.assertEquals(0, this.bookService.findAll().size())
        );
    }

    @Test
    public void deleteOneById_book_with_id_inexisted_expected_BookNotFoundException() {
        final var bookToBeSave = new Book("123", "Java CookBook");
        this.bookService.saveOne(bookToBeSave);

        final var deletedBook = this.bookService.deleteOneById("234");

        Assertions.assertAll(
                () -> org.hamcrest.MatcherAssert.assertThat(
                        deletedBook.get(),
                        org.hamcrest.CoreMatchers.instanceOf(BookService.BookException.BookNotFoundException.class)
                ),
                () -> Assertions.assertEquals(1, this.bookService.findAll().size())
        );
    }

    @Test
    public void deleteOneById_book_with_id_null_expected_BookIdNullOrEmptyException() {
        final var bookToBeSave = new Book("123", "Java CookBook");
        this.bookService.saveOne(bookToBeSave);

        final var deletedBook = this.bookService.deleteOneById(null);

        Assertions.assertAll(
                () -> org.hamcrest.MatcherAssert.assertThat(
                        deletedBook.get(),
                        org.hamcrest.CoreMatchers.instanceOf(BookService.BookException.BookIdEmptyOrNullException.class)
                ),
                () -> Assertions.assertEquals(1, this.bookService.findAll().size())
        );
    }

    @Test
    public void deleteOneById_book_with_id_empty_expected_BookIdNullOrEmptyException() {
        final var bookToBeSave = new Book("123", "Java CookBook");
        this.bookService.saveOne(bookToBeSave);

        final var deletedBook = this.bookService.deleteOneById("");

        Assertions.assertAll(
                () -> org.hamcrest.MatcherAssert.assertThat(
                        deletedBook.get(),
                        org.hamcrest.CoreMatchers.instanceOf(BookService.BookException.BookIdEmptyOrNullException.class)
                ),
                () -> Assertions.assertEquals(1, this.bookService.findAll().size())
        );
    }

    @Test
    public void deleteAll_3_books_expected_3_books_deleted() {
        final var booksToBeSave = List.of(
                new Book("123", "Java Cookbook"),
                new Book("234", "Quarkus Cookbook"),
                new Book("456", "Micronaut Cookbook")
        );
        this.bookService.saveAll(booksToBeSave);
        this.bookService.deleteAll();

        Assertions.assertEquals(0, this.bookService.findAll().size());
    }

    @Test
    public void isExist_book_with_id_existed_expected_true() {
        final var bookToBeSave = new Book("123", "Java CookBook");
        this.bookService.saveOne(bookToBeSave);

        final var isBookExist = this.bookService.isExist(bookToBeSave.getId());
        Assertions.assertTrue(isBookExist);
    }

    @Test
    public void isExist_book_with_id_inexisted_expected_false() {
        final var bookToBeSave = new Book("123", "Java CookBook");
        this.bookService.saveOne(bookToBeSave);

        final var isBookExist = this.bookService.isExist("234");
        Assertions.assertFalse(isBookExist);
    }
}
