package dev.bookt.booking;

public class InvalidHoldStateException extends RuntimeException{
    public InvalidHoldStateException(String message){
        super(message);
    }
}
