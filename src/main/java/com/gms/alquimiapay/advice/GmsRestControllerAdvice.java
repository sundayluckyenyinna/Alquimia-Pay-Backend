package com.gms.alquimiapay.advice;

import com.gms.alquimiapay.exception.BadModelException;
import com.gms.alquimiapay.exception.UserRecordNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@RestControllerAdvice(basePackages = {"com.gms.suretrade"})
public class GmsRestControllerAdvice
{

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BadModelException.class)
    public void handleBadModelException(BadModelException exception, HttpServletResponse response){
       this.pushExceptionToClient(exception, response);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(UserRecordNotFoundException.class)
    public void handleUserRecordNotFoundException(UserRecordNotFoundException exception, HttpServletResponse response){
        this.pushExceptionToClient(exception, response);
    }

    private void pushExceptionToClient(RuntimeException exception, HttpServletResponse response){
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String errorJson = exception.getMessage();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        writer.write(errorJson);
    }
}
