package dev.bookt.common;

import dev.bookt.booking.BookingConflictException;
import dev.bookt.booking.BookingNotFoundException;
import dev.bookt.booking.InvalidHoldStateException;
import dev.bookt.resource.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BookingConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleBookingConflict(BookingConflictException e){
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(value = InvalidHoldStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleInvalidHoldState(InvalidHoldStateException e){
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(value = BookingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleBookingNotFound(BookingNotFoundException e){
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleResourceNotFound(ResourceNotFoundException e){
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleNoResourceFound(NoResourceFoundException e){
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleUnexpectedException(Exception e){
        e.printStackTrace();
        return new ErrorResponse("An unexpected error occurred while processing the request");
    }





}
