package org.training.service;

import io.vavr.control.Either;
import lombok.Data;
import org.training.model.Book;
import org.training.repository.IBookRepository;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


public class BookService {
    private final IBookRepository bookRepository;

    public BookService(final IBookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public final Either<? extends BookException, Book> saveOne(final Book book) {
        final var isBookValid = isValidBookForSave(book);

        return
                isBookValid.isValid()
                        ? Either.right(this.bookRepository.saveOne(book))
                        : Either.left(
                        isBookValid
                                .reason()
                                .orElse(new BookException("Unknown error")));
    }

    public final BooksRecord saveAll(final Collection<Book> books) {

        final var booksPartition =
                books
                        .stream()
                        .map(this::isValidBookForSave)
                        .collect(
                                Collectors.partitioningBy(
                                        ValidBookRecord::isValid
                                )
                        );

        final var validBooks =
                booksPartition
                        .get(true)
                        .stream()
                        .map(ValidBookRecord::book)
                        .collect(Collectors.toUnmodifiableList());
        final var unvalidBooks =
                booksPartition
                        .get(false)
                        .stream()
                        .map(this::validBookRecord2UnsavedBooks)
                        .collect(Collectors.toUnmodifiableList());

        return
                new BooksRecord(
                        this.bookRepository.saveAll(validBooks),
                        unvalidBooks
                );

    }


    final Either<? extends BookException, Book> findOneById(final String id) {
        final var isBookIdValid = isBookIdValid(id);
        if (isBookIdValid.isPresent())
            return Either.left(isBookIdValid.get());

        return
                this.bookRepository.findOneById(id)
                        .<Either<? extends BookException, Book>>map(Either::right)
                        .orElse(Either.left(new BookException.BookNotFoundException("Book not found!")));

    }

    final Collection<Book> findAll() {
        return
                this.bookRepository
                        .findAll();
    }

    final Either<? extends BookException, Book> updateOne(final Book book) {
        final var isBookValid = isValidBookForUpdate(book);

        if (isBookValid.isValid())
            return
                    this.bookRepository
                            .updateOne(book)
                            .<Either<? extends BookException, Book>>map(Either::right)
                            .orElse(Either.left(new BookException("Unknown book exception!")));

        return
                Either.left(
                        isBookValid
                                .reason()
                                .orElse(new BookException("Unknown book exception!")));
    }

    final Either<? extends BookException, Book> deleteOneById(final String id) {
        final var isBookIdValid = isBookIdValid(id);
        if (isBookIdValid.isPresent())
            return
                    Either
                            .left(new BookException.BookIdEmptyOrNullException("Book id is unvalid"));

        return
                this.bookRepository
                        .findOneById(id)
                        .<Either<? extends BookException, Book>>map(Either::right)
                        .orElse(
                                Either
                                        .left(new BookException.BookNotFoundException("Book not found")));
    }

    final void deleteAll() {
        this.bookRepository.deleteAll();
    }

    public final boolean isExist(final String id) {
        return
                this.bookRepository.isExist(id);
    }

    public final Optional<BookException.BookNullException> isBookNull(final Book book) {
        return
                Objects.isNull(book)
                        ? Optional.ofNullable(new BookException.BookNullException("Book is null"))
                        : Optional.empty();
    }

    public final Optional<BookException.BookIdEmptyOrNullException> isBookIdValid(final String id) {
        final var idIsEmptyOrNull = Objects.isNull(id) || id.isBlank();
        return idIsEmptyOrNull
                ? Optional.ofNullable(new BookException.BookIdEmptyOrNullException("Book id is empty or null"))
                : Optional.empty();
    }

    public final Optional<BookException.BookNameEmptyOrNullException> isBookNameValid(final String name) {
        final var isNameValid = Objects.isNull(name) || name.isBlank();
        return isNameValid
                ? Optional.ofNullable(new BookException.BookNameEmptyOrNullException("Book name is empty or null"))
                : Optional.empty();
    }

    public final ValidBookRecord isValidBookForSave(final Book book) {
        final var isBookNull = isBookNull(book);
        if (isBookNull.isPresent()) {
            return
                    ValidBookRecord
                            .invalid(book, isBookNull.get());
        }

        final var isBookIdValid = isBookIdValid(book.getId());
        if (isBookIdValid.isPresent()) {
            return
                    ValidBookRecord
                            .invalid(book, isBookIdValid.get());

        }

        final var isBookNameValid = isBookNameValid(book.getName());
        if (isBookNameValid.isPresent()) {
            return
                    ValidBookRecord
                            .invalid(book, isBookNameValid.get());

        }
        final var isBookExist = isExist(book.getId());
        if (isBookExist) {
            return
                    ValidBookRecord
                            .invalid(book, new BookException.BookAlreadyExistException("Book already exist!"));
        }

        return ValidBookRecord.valid(book);
    }

    public final ValidBookRecord isValidBookForUpdate(Book book) {
        final var isBookNull = isBookNull(book);
        if (isBookNull.isPresent()) {
            return
                    ValidBookRecord
                            .invalid(book, isBookNull.get());
        }

        final var isBookIdValid = isBookIdValid(book.getId());
        if (isBookIdValid.isPresent()) {
            return
                    ValidBookRecord
                            .invalid(book, isBookIdValid.get());

        }

        final var isBookNameValid = isBookNameValid(book.getName());
        if (isBookNameValid.isPresent()) {
            return
                    ValidBookRecord
                            .invalid(book, isBookNameValid.get());

        }

        final var isBookExist = isExist(book.getId());
        if (!isBookExist) {
            return
                    ValidBookRecord
                            .invalid(
                                    book,
                                    new BookException.BookNotFoundException("Book not found!"));
        }

        return
                ValidBookRecord
                        .valid(book);
    }

    @Data
    protected static final class BooksRecord {
        private final Collection<Book> savedBooks;
        private final Collection<UnsavedBooks> unsavedBooks;

        @Data
        protected static final class UnsavedBooks {
            private final Book book;
            private final BookException reason;
        }
    }

    private final BooksRecord.UnsavedBooks validBookRecord2UnsavedBooks(final ValidBookRecord validBookRecord) {
        return
                new
                        BooksRecord
                                .UnsavedBooks(
                        validBookRecord.book,
                        validBookRecord
                                .reason()
                                .orElse(new BookException("Unknown exception")));

    }

    private static final class ValidBookRecord {
        private final Book book;
        private final BookException reason;

        private ValidBookRecord(final Book book) {
            this.book = book;
            this.reason = null;
        }

        private ValidBookRecord(final Book book, final BookException reason) {
            this.book = book;
            this.reason = reason;
        }

        public Book book() {
            return this.book;
        }

        public Optional<BookException> reason() {
            return
                    Objects.nonNull(this.reason)
                            ? Optional.ofNullable(this.reason)
                            : Optional.empty();
        }

        public final boolean isValid() {
            return
                    Objects.isNull(this.reason);
        }

        public static ValidBookRecord valid(final Book book) {
            return
                    new ValidBookRecord(book);
        }

        public static ValidBookRecord invalid(final Book book, final BookException reason) {
            return
                    new ValidBookRecord(
                            book,
                            reason
                    );
        }
    }

    public static class BookException extends RuntimeException {
        public BookException(String message) {
            super(message);
        }

        public final static class BookNotFoundException extends BookException {

            public BookNotFoundException(String message) {
                super(message);
            }
        }

        public final static class BookNullException extends BookException {

            public BookNullException(String message) {
                super(message);
            }
        }

        public final static class BookIdEmptyOrNullException extends BookException {

            public BookIdEmptyOrNullException(String message) {
                super(message);
            }
        }

        public final static class BookNameEmptyOrNullException extends BookException {

            public BookNameEmptyOrNullException(String message) {
                super(message);
            }
        }

        public final static class BookAlreadyExistException extends BookException {

            public BookAlreadyExistException(String message) {
                super(message);
            }
        }

    }
}
