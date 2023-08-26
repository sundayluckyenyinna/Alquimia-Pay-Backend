package com.gms.alquimiapay.exception;

public class BadModelException extends RuntimeException
{
    public BadModelException(){
        super();
    }

    public BadModelException(String message){
        super(message);
    }
}
