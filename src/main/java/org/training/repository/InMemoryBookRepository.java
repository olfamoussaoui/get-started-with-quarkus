package org.training.repository;

import org.training.model.Book;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InMemoryBookRepository implements IBookRepository {

    private Collection<Book> books;

    private InMemoryBookRepository() {
        this.books = new ArrayList<Book>();
    }

    @Override
    public Book saveOne(final Book book) {
        this.books.add(book);
        return book;
    }

    @Override
    public Collection<Book> saveAll(final Collection<Book> books) {
        this.books.addAll(books);
        return books;
    }

    @Override
    public Optional<Book> findOneById(final String id) {
        return this.books
                .stream()
                .filter(it -> it.getId().equals(id))
                .findFirst();
    }

    @Override
    public Collection<Book> findAll() {
        return this.books;
    }

    @Override
    public Optional<Book> updateOne(final Book book) {
        final var bookToBeUpdated = deleteOneById(book.getId());

        if (bookToBeUpdated.isPresent()) {
            this.books.add(book);
            return Optional.ofNullable(book);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Book> deleteOneById(final String id) {
        final var bookToBeDeleted = findOneById(id);

        if (bookToBeDeleted.isPresent()) {
            this.books =
                    this.books
                            .stream()
                            .filter(Predicate.not(it -> it.getId().equals(id)))
                            .collect(Collectors.toList());
        }

        return bookToBeDeleted;
    }

    @Override
    public void deleteAll() {
        this.books = new ArrayList<Book>();
    }

    @Override
    public boolean isExist(final String id) {
        return this.books
                .stream()
                .anyMatch(it -> it.getId().equals(id));
    }
}
