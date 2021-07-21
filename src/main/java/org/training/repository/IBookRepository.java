package org.training.repository;

import org.training.model.Book;

import java.util.Collection;
import java.util.Optional;

public interface IBookRepository {
    Book saveOne(final Book book);
    Collection<Book> saveAll(final Collection<Book> books);
    Optional<Book> findOneById(final String id);
    Collection<Book> findAll();
    Optional<Book> updateOne(final Book book);
    Optional<Book> deleteOneById(final String id);
    void deleteAll();
    boolean isExist(final String id);
}
