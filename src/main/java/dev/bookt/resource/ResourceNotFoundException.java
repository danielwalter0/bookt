package dev.bookt.resource;

public class ResourceNotFoundException extends RuntimeException{
        public  ResourceNotFoundException(String message){
            super(message);
        }
}
