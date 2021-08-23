package org.training.controller;

import lombok.Data;
import org.training.model.Book;
import org.training.service.BookService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.stream.Collectors;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GET()
    @Path("/{id}")
    public Response getBook(@PathParam("id") final String id) {
        return
                bookService
                        .findOneById(id)
                        .fold(
                                it -> Response.status(Response.Status.BAD_REQUEST).header("error", it.getMessage()).build(),
                                it -> Response.ok(it).build()
                        );
    }

    @GET
    public Response getBooks() {
        return
                Response
                        .ok(
                                bookService
                                        .findAll()
                        )
                        .build();
    }

    @POST
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveBook(final Book book) {
        return
                bookService
                        .saveOne(book)
                        .fold(
                                it -> Response.status(Response.Status.BAD_REQUEST).header("error", it.getMessage()).build(),
                                it -> Response.ok(it).build()
                        );
    }

    @POST
    @Path("savebooks")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveBooks(final Collection<Book> books) {
        final var savedBooks =
                ViewBooksRecord
                        .fromBooksRecord2ViewBooksRecord(
                                bookService.saveAll(books)
                        );
        return
                Response
                        .ok(savedBooks)
                        .build();

    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateBook(final Book book) {
        return
                bookService
                        .updateOne(book)
                        .fold(
                                it -> Response.status(Response.Status.BAD_REQUEST).header("error", it.getMessage()).build(),
                                it -> Response.ok(it).build()
                        );
    }

    @DELETE
    @Path("/delete/{id}")
    public Response deleteBook(@PathParam("id") final String id) {
        return
                bookService
                        .deleteOneById(id)
                        .fold(
                                it -> Response.status(Response.Status.BAD_REQUEST).header("error", it.getMessage()).build(),
                                it -> Response.ok(it).build()
                        );
    }

    @DELETE
    @Path("/delete")
    public Response deleteBooks() {
        bookService.deleteAll();
        return
                Response
                        .ok("All the Books are successfully deleted!")
                        .build();
    }

    @Data
    public static final class ViewBooksRecord {
        private final Collection<Book> savedBooks;
        private final Collection<UnsavedBooks> unsavedBooks;

        @Data
        public static final class UnsavedBooks {
            private final Book book;
            private final String reason;
        }

        public static ViewBooksRecord fromBooksRecord2ViewBooksRecord(final BookService.BooksRecord booksRecord) {
            return
                    new ViewBooksRecord
                            (
                                    booksRecord.getSavedBooks(),
                                    booksRecord.getUnsavedBooks()
                                            .stream()
                                            .map(it -> new UnsavedBooks(it.getBook(), it.getReason().getMessage()))
                                            .collect(Collectors.toUnmodifiableList())
                            );
        }

    }
}
