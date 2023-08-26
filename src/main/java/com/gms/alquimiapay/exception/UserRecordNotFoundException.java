package com.gms.alquimiapay.exception;

public class UserRecordNotFoundException extends RuntimeException
{
    public UserRecordNotFoundException(){ super(); }
    public UserRecordNotFoundException(String message){ super(message); }
}
