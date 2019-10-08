package com.example.demo.controller;

import com.example.demo.form.DirectoryResponse;
import exceptions.CollectionAlreadyExistsException;
import exceptions.CollectionNotFoundException;
import exceptions.FileStorageException;
import exceptions.QueryParserException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DirectoryExceptionController {
    @ExceptionHandler(value = CollectionNotFoundException.class)
    public ResponseEntity<Object> exception(CollectionNotFoundException exception) {
        return new ResponseEntity<>(new DirectoryResponse(false,"Directory Not Found!"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = CollectionAlreadyExistsException.class)
    public ResponseEntity<Object> exception(CollectionAlreadyExistsException exception) {
        return new ResponseEntity<>(new DirectoryResponse(false,"Directory Already Exists"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = FileStorageException.class)
    public ResponseEntity<Object> exception(FileStorageException exception) {
        return new ResponseEntity<>(new DirectoryResponse(false,exception.getMessage()), HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(value = QueryParserException.class)
    public ResponseEntity<Object> exception(QueryParserException exception) {
        return new ResponseEntity<>(new DirectoryResponse(false,exception.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    }

}
