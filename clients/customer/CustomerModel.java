package clients.customer;

import catalogue.Basket;
import catalogue.Product;
import debug.DEBUG;
import middle.MiddleFactory;
import middle.OrderProcessing;
import middle.StockException;
import middle.StockReader;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Implements the Model of the customer client
 */
public class CustomerModel extends Observable
{
  private Product     theProduct = null;          // Current product
  private Basket      theBasket  = null;          // Bought items

  private String      pn = "";                    // Product being processed

  private StockReader     theStock     = null;
  private OrderProcessing theOrder     = null;
  private ImageIcon       thePic       = null;

  /*
   * Construct the model of the Customer
   * @param mf The factory to create the connection objects
   */
  public CustomerModel(MiddleFactory mf)
  {
    try                                          // 
    {  
      theStock = mf.makeStockReader();           // Database access
    } catch ( Exception e )
    {
      DEBUG.error("CustomerModel.constructor\n" +
                  "Database not created?\n%s\n", e.getMessage() );
    }
    theBasket = makeBasket();                    // Initial Basket
  }
  
  /**
   * return the Basket of products
   * @return the basket of products
   */
  public Basket getBasket()
  {
    return theBasket;
  }

  /**
   * Check if the product is in Stock
   * @param productNum The product number
   */
  public void doCheck(String productNum )
  {
    theBasket.clear();                          // Clear s. list
    String theAction = "";
    pn  = productNum.trim();                    // Product no.
    int    amount  = 1;                         //  & quantity
    try
    {
      if ( theStock.exists( pn ) )              // Stock Exists?
      {                                         // T
        Product pr = theStock.getDetails( pn ); //  Product
        if ( pr.getQuantity() >= amount )       //  In stock?
        { 
          theAction =                           //   Display 
            String.format( "%s : %7.2f (%2d) ", //
              pr.getDescription(),              //    description
              pr.getPrice(),                    //    price
              pr.getQuantity() );               //    quantity
          pr.setQuantity( amount );             //   Require 1
          theBasket.add( pr );                  //   Add to basket
          thePic = theStock.getImage( pn );     //    product
        } else {                                //  F
          theAction =                           //   Inform
            pr.getDescription() +               //    product not
            " not in stock" ;                   //    in stock
        }
      } else {                                  // F
        theAction =                             //  Inform Unknown
          "Unknown product number " + pn;       //  product number
      }
    } catch( StockException e )
    {
      DEBUG.error("CustomerClient.doCheck()\n%s",
      e.getMessage() );
    }
    setChanged(); notifyObservers(theAction);
  }

  /**
   * Clear the products from the basket
   */
  public void doClear()
  {
    String theAction = "";
    theBasket.clear();                        // Clear s. list
    theAction = "Enter Product Number";       // Set display
    thePic = null;                            // No picture
    setChanged(); notifyObservers(theAction);
  }
  
  /**
   * Return a picture of the product
   * @return An instance of an ImageIcon
   */ 
  public ImageIcon getPicture()
  {
    return thePic;
  }
  
  /**
   * ask for update of view callled at start
   */
  private void askForUpdate()
  {
    setChanged(); notifyObservers("START only"); // Notify
  }

  /**
   * Make a new Basket
   * @return an instance of a new Basket
   */
  protected Basket makeBasket()
  {
    return new Basket();
  }
  
  public void addToOrder(String productNum) {
	    try {
	        if (theStock.exists(productNum)) {
	            Product pr = theStock.getDetails(productNum);
	            theBasket.add(pr); // Add product to the basket
	            setChanged();
	            notifyObservers("Added " + pr.getDescription() + " to the order.");
	        } else {
	            setChanged();
	            notifyObservers("Product not found.");
	        }
	    } catch (StockException e) {
	        DEBUG.error("CustomerModel.addToOrder\n" + e.getMessage());
	    }
	}

  public List<Product> getAvailableProducts() {
	    List<Product> products = new ArrayList<>();
	    try {
	        List<String> productNumbers = theStock.getAllProductNumbers();
	        for (String productNum : productNumbers) {
	            Product product = theStock.getDetails(productNum);
	            products.add(product);
	        }
	    } catch (StockException e) {
	        DEBUG.error("CustomerModel.getAvailableProducts\n" + e.getMessage());
	    }
	    return products;
	}
  
  public void addToBasket(String productNum, int quantity) {
	    try {
	        if (theStock.exists(productNum)) {
	            Product product = theStock.getDetails(productNum); // Fetch product from stock
	            product.setQuantity(quantity); // Set the quantity
	            theBasket.add(product); // Add product to the basket
	            System.out.println("Added to basket: " + product.getDescription() + " x " + quantity);
	            setChanged();
	            notifyObservers(); // Notify the view to update
	        } else {
	            System.out.println("Product not found in stock: " + productNum);
	        }
	    } catch (StockException e) {
	        e.printStackTrace();
	    }
	}

  
  public void removeProductFromBasket(String productNum) {
	    theBasket.removeIf(product -> product.getProductNum().equals(productNum));
	    setChanged();
	    notifyObservers(); // Notify view to update basket display
	}

  public void sendBasketToCashier() {
	    try {
	        if (!theBasket.isEmpty()) {
	            System.out.println("Sending basket to cashier...");
	            // Logic to send the basket to the cashier (e.g., via an OrderProcessor)
	            theBasket.clear(); // Clear basket after sending
	            setChanged();
	            notifyObservers(); // Notify view to clear basket display
	        } else {
	            System.out.println("Basket is empty. Nothing to send.");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
  
  public void clearBasket() {
	    theBasket.clear();
	    setChanged();
	    notifyObservers(); // Update the view to reflect the empty basket
	}


  

}

