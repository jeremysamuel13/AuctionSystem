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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import big.data.DataSource;

/**
 * AuctionTable class
 * Contains all the auctions
 */
public class AuctionTable implements Serializable {

    HashMap<String, Auction> table = new HashMap<>();

    //heading of the table that is to be printed
    final String tableHeading = " Auction ID  | Bid         | Seller         "
            + "        | Buyer                     | Time       | Item  " +
            "Info\n=======================================================" +
            "======" +
            "=============================================================" +
            "======" +
            "==============================================";


    /**
     * Creates a table using valid data from a url containing XML data. Uses
     * BigData library.
     * @param URL
     * The URL to have data be retrieved from
     * @return
     * Returns an AuctionTable full of listings based on the data given in
     * the XML
     * @throws IllegalArgumentException
     * Throws exception when the URL contained invalid/non-supported data or
     * if the URL syntax was bad
     */
    public static AuctionTable buildFromURL(String URL) throws
            IllegalArgumentException {
        DataSource ds = DataSource.connectXML(URL);
        ds.load();
        String[] sellerNames = ds.fetchStringArray("listing/seller_info" +
                "/seller_name");
        String[] currentBids = ds.fetchStringArray("listing/auction_info" +
                "/current_bid");
        String[] timesLeft = ds.fetchStringArray("listing/auction_info" +
                "/time_left");
        String[] idNums = ds.fetchStringArray("listing/auction_info" +
                "/id_num");
        String[] bidderNames = ds.fetchStringArray("listing/auction_info" +
                "/high_bidder/bidder_name");
        String[] memory = ds.fetchStringArray("listing/item_info/memory");
        String[] hdd = ds.fetchStringArray("listing/item_info/hard_drive");
        String[] cpu = ds.fetchStringArray("listing/item_info/cpu");


        AuctionTable t = new AuctionTable();

        for (int i = 0; i < timesLeft.length; i++) {
            String[] split = timesLeft[i].split(", ");
            int p = 0;
            for (String y : split) {
                int l;
                if(y.contains("day")){
                    l = Integer.parseInt(y.substring(0, y.indexOf(" ")));
                    p = p + (l*24);
                    if(y.substring(y.indexOf(" ")).contains("hour") ||
                            y.substring(y.indexOf(" ")).contains("hr")) {
                        l = Integer.parseInt(y.substring(y.indexOf(" "),
                                y.indexOf("h")));
                        p = p + l;
                    }

                }else if(y.contains("hour") || y.contains("hr")){
                    l = Integer.parseInt(y.substring(0, y.indexOf('h') - 1));
                    p = p + l;
                }
            }

            timesLeft[i] = p + "";

        }

        for (int i = 0; i < sellerNames.length; i++) {
            try {
                double d = DecimalFormat.getNumberInstance().parse(
                        currentBids[i].substring(1)).doubleValue();

                String g = "";

                if(!cpu[i].isBlank()){
                    g = g + cpu[i].strip();
                }
                if(!memory[i].isBlank()){
                    if(!g.isBlank())
                        g = g + " - ";
                    g = g + memory[i].strip();
                }
                if(!hdd[i].isBlank()) {
                    if(!g.isBlank())
                        g = g + " - ";
                    g = g + hdd[i].strip();
                }

                Auction x = new Auction(Integer.parseInt(timesLeft[i]), d,
                        idNums[i], sellerNames[i], bidderNames[i].trim(),
                        g);

                t.table.put(idNums[i], x);
            }catch (ParseException e){
                throw new IllegalArgumentException("Improper values: Check " +
                        "bid values");
            }

        }

        return t;
    }

    /**
     * Puts a new Auction in the AuctionTable
     * @param auctionID
     * The ID of the auction to be added
     * @param auction
     * The auction that is to be added
     * @throws IllegalArgumentException
     * Throws exception when the given ID already exists within the AuctionTable
     */
    public void putAuction(String auctionID, Auction auction) throws
            IllegalArgumentException{
        if(table.get(auctionID) != null)
            throw new IllegalArgumentException("auction id already exists");
        else
            table.put(auctionID, auction);
    }

    /**
     * Gets an Auction by referring an auctionID
     * @param auctionID
     * The ID of the Auction to be retrieved
     * @return
     * The Auction with the associated ID. Returns null if auctionID does not
     * exist
     */
    public Auction getAuction(String auctionID){
        return table.get(auctionID);
    }

    /**
     * Lets a specified amount of time (in hours) pass for all auctions
     * @param numHours
     * The amount of time to be passed
     * @throws IllegalArgumentException
     * Throws exception when there is an invalid input for the amount of time
     * to be passed.
     */
    public void letTimePass(int numHours) throws IllegalArgumentException{
        if(numHours > 0) {
            for (Auction a : table.values()) {
                a.decrementTimeRemaining(numHours);
            }
        }else
            throw new IllegalArgumentException("Only positive, nonzero " +
                    "numbers are allowed");
    }

    /**
     * Removes all expired auctions in the AuctionTable
     */
    public void removeExpiredAuctions(){
        table.values().removeIf(element -> element.getTimeRemaining() == 0);
    }

    /**
     * Prints the AuctionTable in a tabular format
     */
    public void printTable(){
        System.out.println("\n");
        System.out.println(tableHeading);
        for(Auction x : table.values()){
            System.out.println(x.toString());
        }
        System.out.println("\n");
    }
}
