/*
 * @author Jeremy Samuel
 * E-mail: jeremy.samuel@stonybrook.edu
 * Stony Brook ID: 113142817
 * CSE 214
 * Recitation Section 3
 * Recitation TA: Dylan Andres
 * HW #6
 */

/**
 * ClosedAuctionException class
 * Exception is thrown when the auction is closed.
 */
public class ClosedAuctionException extends RuntimeException{
    public ClosedAuctionException(String message){
        super(message);
    }
}
