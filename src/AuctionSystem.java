/*
 * @author Jeremy Samuel
 * E-mail: jeremy.samuel@stonybrook.edu
 * Stony Brook ID: 113142817
 * CSE 214
 * Recitation Section 3
 * Recitation TA: Dylan Andres
 * HW #6
 */

import big.data.DataSourceException;
import java.io.*;
import java.util.*;

/**
 * AuctionSystem class
 * Allows the user to interact with the AuctionTable
 */
public class AuctionSystem implements Serializable {
    public static void main(String[] args) {

        AuctionTable auction = new AuctionTable();
        String username;

        String input;

        Scanner scan = new Scanner(System.in);


        //for deserialization
        try {
            FileInputStream fis = new FileInputStream("auction.obj");
            ObjectInputStream ois = new ObjectInputStream(fis);
            auction.table = (HashMap<String, Auction>) ois.readObject();
            System.out.println("Previous auction table detected.");
            System.out.println("Loading table...");
            fis.close();
            ois.close();
        }catch (EOFException ignored){
            //this exception is thrown when the reader reaches the end of the
            // file, therefore it can safely be ignored.
        }catch (Exception e){
            //exception is thrown when no object is found
            System.out.println("No previous auction table detected.");
            System.out.println("Creating new table...");
            auction = new AuctionTable();
        }


        System.out.println("\nPlease select a username: ");
        username = scan.nextLine();

        boolean active = true;

        //ui
        //loop keeps going until user enters "q"
        while(active){

            System.out.println("\n(D) - Import Data from URL\n" +
                    "(A) - Create a New Auction\n" +
                    "(B) - Bid on an Item\n" +
                    "(I) - Get Info on Auction\n" +
                    "(P) - Print All Auctions\n" +
                    "(R) - Remove Expired Auctions\n" +
                    "(T) - Let Time Pass\n" +
                    "(Q) - Quit");

            System.out.println("\nPlease select an option: ");
            input = scan.nextLine();

            //inputs are not case sensitive
            switch (input.toLowerCase()) {
                case "d" -> {
                    System.out.println("\nPlease enter a URL: ");
                    String url = scan.nextLine();
                    AuctionTable x;
                    try{
                        System.out.println("\nLoading...");
                        x = AuctionTable.buildFromURL(url);
                    }catch (DataSourceException e){
                        System.out.println("Invalid URL: Bad syntax or URL " +
                                "has no valid XML data");
                        continue;
                    }
                    System.out.println("Auction data loaded successfully!");
                    auction = x;
                }
                case "a" -> {
                    System.out.println("\nCreating new Auction as " + username);
                    System.out.println("Please enter an Auction ID: ");
                    String id = scan.nextLine();
                    System.out.println("Please enter an Auction time " +
                            "(hours): ");
                    int time = scan.nextInt();

                    //prevents next scanLine from being skipped
                    scan.nextLine();

                    if(!(time > 0)){
                        System.out.println("\nInvalid input, only positive, " +
                                "nonzero numbers allowed");
                        continue;
                    }

                    System.out.println("Please enter some Item Info: ");
                    String info = scan.nextLine();

                    auction.putAuction(id, new Auction(time, -1, id,
                            username, null, info));

                    System.out.println("\nAuction " + id + " inserted into " +
                            "table");
                }
                case "b" -> {
                    System.out.println("\nPlease enter an Auction ID: ");
                    String id = scan.nextLine();
                    Auction a = auction.getAuction(id);

                    if(a != null){
                        if(a.isOpen()) {
                            System.out.println("\nAuction " + id + " is OPEN");
                            if(a.getCurrentBid() > -1)
                                System.out.println("\tCurrent Bid: $ " +
                                        a.getStringBid() + "\n");
                            else
                                System.out.println("\tCurrent Bid: None\n");

                            System.out.println("What would you like to bid?: ");
                            try {
                                a.newBid(username, scan.nextDouble());
                                System.out.println("Bid accepted");
                                //bypass future skipped nextLine
                                scan.nextLine();
                            } catch (ClosedAuctionException e) {
                                System.out.println("Auction is closed");
                            } catch (IllegalArgumentException e){
                                System.out.println(e.getMessage());
                            }
                        }else{
                            System.out.println("\nAuction " + id + " is " +
                                    "CLOSED");
                            if(a.getCurrentBid() > -1)
                                System.out.println("\tCurrent Bid: $ " +
                                        a.getStringBid() + "\n");
                            else
                                System.out.println("\tCurrent Bid: None\n");

                            System.out.println("You can no longer bid on " +
                                    "his item.");
                        }
                    }else{
                        System.out.println("Auction does not exist");
                    }

                }
                case "i" -> {
                    System.out.println("\nPlease enter an Auction ID: ");
                    String id = scan.nextLine();
                    Auction a = auction.getAuction(id);
                    if(a != null){
                        System.out.println("\nAuction " + id);
                        System.out.println("\tSeller: " + a.getSellerName());
                        System.out.println("\tBuyer: " + a.getBuyerName());
                        System.out.println("\tTime (Hours): " +
                                a.getTimeRemaining());
                        System.out.println("\tInfo: " + a.getItemInfo());
                    }else{
                        System.out.println("Auction does not exist");
                    }
                }
                case "p" -> auction.printTable();
                case "r" -> {
                    System.out.println("\nRemoving expired auctions...");
                    auction.removeExpiredAuctions();
                    System.out.println("All expired auctions removed.");
                }
                case "t" -> {
                    System.out.println("How many hours should pass: ");
                    try{
                        auction.letTimePass(scan.nextInt());
                        System.out.println("\nTime passing...");
                        System.out.println("Auction times updated.");
                    }catch (IllegalArgumentException e){
                        System.out.println(e.getMessage());
                    }
                    //prevents next scan from being skipped
                    scan.nextLine();
                }
                case "q" -> {
                    try {
                        //serialization
                        System.out.println("\nWriting Auction Table to " +
                                "file...  ");
                        FileOutputStream fos =
                                new FileOutputStream("auction.obj");
                        ObjectOutputStream oos = new ObjectOutputStream(fos);
                        oos.writeObject(auction.table);
                        System.out.println("Done!");
                        active = false;
                        System.out.println("Goodbye.");
                        fos.close();
                        oos.close();
                    }catch (IOException e){
                        //Exception thrown in rare cases (read-only
                        // directories, etc)
                        System.out.println(e.getMessage());
                    }
                }
                default -> System.out.println("\nInvalid input");
            }
        }

    }
}
