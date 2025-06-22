package com.luv2code.books.controller;


import com.luv2code.books.entity.Book;
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
                new Book("The Hobbit", "JRR Tolken", "Fantasy"),
                new Book("The Lord of the Rings", "JRR Tolken", "Fantasy"),
                new Book("The Hunger Games", "Suzane Collins", "Action"),
                new Book("To Kill a Mockingbird", "Harper Lee", "Classic"),
                new Book("1984", "George Orwell", "Science Fiction"),
                new Book("Pride and Prejudice", "Jane Austen", "Romance"),
                new Book("The Great Gatsby", "F. Scott Fitzgerald", "Classic")
        ));
    }

    public BookController() {
        initializeBooks();
    }

    @GetMapping
    public List<Book> getBooks(@RequestParam(required = false) String category) {
        if (category == null) {
            return books;
        }

       return books.stream()
               .filter(book -> book.getCategory().equalsIgnoreCase(category))
               .toList();
    }


    @GetMapping("/{title}")
    public Book getBookByTitle(@PathVariable String title) {
        return books.stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(title))
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

    @PostMapping
    public void addBook(@RequestBody Book newBook) {
        if (doesBookExist(newBook)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Book already exists");
        }
        createBook(newBook);
    }

    @PutMapping("/{title}")
    public void updateBook(@PathVariable String title, @RequestBody Book updatedBook) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getTitle().equalsIgnoreCase(title)) {
                books.set(i, updatedBook);
                return;
            }
        }
    }

    @DeleteMapping("/{title}")
    public void deleteBook(@PathVariable String title) {
        books.removeIf(book -> book.getTitle().equalsIgnoreCase(title));
    }

}
