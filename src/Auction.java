/*
 * @author Jeremy Samuel
 * E-mail: jeremy.samuel@stonybrook.edu
 * Stony Brook ID: 113142817
 * CSE 214
 * Recitation Section 3
 * Recitation TA: Dylan Andres
 * HW #6
 */

import java.io.Serializable;

/**
 * Auction class
 * Holds all info of a specific auction
 */
public class Auction implements Serializable {

    int timeRemaining;

    double currentBid;

    String auctionID;
    String sellerName;
    String buyerName;
    String itemInfo;

    //contains currentBid in a string format which is later turned into the
    // dollar/cent format ($xx.xx)
    String stringBid;

    boolean open;

    public Auction(){

    }

    /**
     * Constructor with parameters
     * @param timeRemaining
     * The time remaining in the auction
     * @param currentBid
     * The current bid on the auction
     * @param auctionID
     * The id of the auction
     * @param sellerName
     * The seller of the auction
     * @param buyerName
     * The buyer/highest bidder of the auction
     * @param itemInfo
     * Info of the auction. Includes cpu, memory and hdd info.
     */
    public Auction(int timeRemaining, double currentBid, String auctionID,
                   String sellerName, String buyerName, String itemInfo){
        this.timeRemaining = timeRemaining;
        this.currentBid = currentBid;
        this.auctionID = auctionID;
        this.sellerName = sellerName;
        this.buyerName = buyerName;
        this.itemInfo = itemInfo;

        open = timeRemaining != 0;

        //converts currentBid into stringBid which will be used for printing
        if(currentBid > -1){
            stringBid = Double.toString(currentBid);
            if(stringBid.substring(stringBid.indexOf(".") + 1).length() != 2){
                stringBid = stringBid + "0";
            }
        }
    }

    /**
     * Gets the time remaining of the auction
     * @return
     * Returns the time remaining of the auction
     */
    public int getTimeRemaining() {
        return timeRemaining;
    }

    /**
     * Gets the current bid of the auction
     * @return
     * Returns the current bid of the auction
     */
    public double getCurrentBid() {
        return currentBid;
    }

    /**
     * Gets the id of the auction
     * @return
     * Returns the id of the auction
     */
    public String getAuctionID() {
        return auctionID;
    }

    /**
     * Gets the seller name of the auction
     * @return
     * Returns the seller name of the auction
     */
    public String getSellerName() {
        return sellerName;
    }

    /**
     * Gets the name of the buyer/highest bidder of auction
     * @return
     * Returns the buyer/highest bidder of auction
     */
    public String getBuyerName() {
        return buyerName;
    }

    /**
     * Gets the info of the auction
     * @return
     * Returns the info of the auction
     */
    public String getItemInfo() {
        return itemInfo;
    }

    /**
     * Gets the currentBid in a dollar/cent form
     * @return
     * Returns the currentBid in a dollar/cent form
     */
    public String getStringBid() {
        return stringBid;
    }

    /**
     * Gets if the auction is open or not
     * @return
     * Returns true if auction is open, false if otherwise
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Decrements the time of the current auction
     * @param time
     * The amount of time (in hours) that you want to pass
     */
    public void decrementTimeRemaining(int time){
        if(time > timeRemaining) {
            timeRemaining = 0;
            open = false;
        }else
            timeRemaining = timeRemaining - time;

    }

    /**
     * Creates a new bid on the current auction and overwrites the previous
     * bid if the new bid exceeds the previous bid
     * @param bidderName
     * The name of the bidder
     * @param bidAmt
     * The amount the bidder has bid on the auction
     * @throws ClosedAuctionException
     * Throws exception when auction is closed
     */
    public void newBid(String bidderName, double bidAmt) throws
            ClosedAuctionException, IllegalArgumentException{
        if(open){
            if(bidAmt > currentBid) {
                currentBid = bidAmt;
                buyerName = bidderName;
                stringBid = Double.toString(currentBid);
                if(stringBid.substring(stringBid.indexOf(".") + 1)
                        .length() != 2){
                    stringBid = stringBid + "0";
                }
            }else if(bidAmt < 0){
                throw new IllegalArgumentException("Invalid bid: Bid amount " +
                        "has to be a positive value and greater than the " +
                        "current bid");
            }else{
                throw new IllegalArgumentException("Bid has to be higher than" +
                        " current bid");
            }
        }else{
            throw new ClosedAuctionException("Auction is closed");
        }
    }

    /**
     * Converts auction into a readable string which is to be used in the
     * printTable() method in the AuctionTable class
     * @return
     * Returns a String that contains the auction in a readable manner
     */
    public String toString(){
        //removes new lines that are caused by flawed formatting in xml (ex.
        // o econgo1 in the yahoo auction data)
        itemInfo = itemInfo.replace("\n", "");
        sellerName = sellerName.replace("\n", "");

        if(currentBid == -1 && buyerName == null){
            return String.format("%12s%3s%11s%3s%-22s%4s%24s%3s%10s%3s%-100s",
                    auctionID, " | ", "", " | ", sellerName, " |  ",
                    "", " | ", timeRemaining + " hours", " | ", itemInfo);
        }else {
            return String.format("%12s%4s%10s%3s%-22s%4s%24s%3s%10s%3s%-100s",
                    auctionID, " | $", stringBid, " | ", sellerName, " |  ",
                    buyerName, " | ", timeRemaining + " hours", " | ",
                    itemInfo);
        }
    }
}
