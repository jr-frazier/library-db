package com.luv2code.books.controller;


import com.luv2code.books.entity.Book;
import com.luv2code.books.request.BookRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final List<Book> books = new ArrayList<>();

    private void initializeBooks() {
        books.addAll(List.of(
                new Book(1, "The Hobbit", "JRR Tolken", "Fantasy", 5),
                new Book(2, "The Lord of the Rings", "JRR Tolken", "Fantasy", 5),
                new Book(3, "The Hunger Games", "Suzane Collins", "Action", 4),
                new Book(4, "To Kill a Mockingbird", "Harper Lee", "Classic", 3),
                new Book(5, "1984", "George Orwell", "Science Fiction", 4),
                new Book(6, "Pride and Prejudice", "Jane Austen", "Romance", 3),
                new Book(7, "The Great Gatsby", "F. Scott Fitzgerald", "Classic", 3)
        ));
    }

    public BookController() {
        initializeBooks();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Book> getBooks(@RequestParam(required = false) String category) {
        if (category == null) {
            return books;
        }

       return books.stream()
               .filter(book -> book.getCategory().equalsIgnoreCase(category))
               .toList();
    }



    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable @Min(value = 1) long id) {
        return books.stream()
                .filter(book -> book.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void createBook(Book newBook) {
        boolean isNewBook = books.stream()
                .noneMatch(book -> book.getTitle().equalsIgnoreCase(newBook.getTitle()));

        if (isNewBook) {
            books.add(newBook);
        }

    }

    public boolean doesBookExist(Book newBook) {
       return books.stream()
                .anyMatch(book -> book.getTitle().equalsIgnoreCase(newBook.getTitle()));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void addBook(@Valid @RequestBody BookRequest newBook) {
       long id = books.isEmpty() ? 1 : books.get(books.size() - 1 ).getId() + 1;
       Book book = convertToBook(id, newBook);

       books.add(book);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void updateBook(@PathVariable @Min(value = 1) long id, @Valid @RequestBody BookRequest bookRequest) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId() == id) {
                Book updatedBook = convertToBook(id, bookRequest);
                books.set(i, updatedBook);
                return;
            }
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable @Min(value = 1) long id) {
        books.removeIf(book -> book.getId() == id);
    }


    private Book convertToBook(long id, BookRequest bookRequest) {
        return new Book(id, bookRequest.getTitle(), bookRequest.getAuthor(), bookRequest.getCategory(), bookRequest.getRating());
    }

}
