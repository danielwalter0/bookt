package dev.bookt.booking;

public class ResourceNotFoundException extends RuntimeException{
        public  ResourceNotFoundException(String message){
            super(message);
        }
}
