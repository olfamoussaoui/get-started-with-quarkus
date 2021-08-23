package org.training.config;

import org.training.repository.IBookRepository;
import org.training.repository.InMemoryBookRepository;
import org.training.service.BookService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;

@ApplicationScoped
public class BeanConfiguration {
    @Singleton
    public IBookRepository inMemoryBookRepository(){
        return new InMemoryBookRepository();
    }
    @Singleton
    public BookService bookService(IBookRepository bookRepository){
        return new BookService(bookRepository) ;
    }
}
