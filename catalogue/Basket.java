package catalogue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Formatter;
import java.util.Locale;

/**
 * A collection of products,
 * used to record the products that are to be wished to be purchased.
 * @author  Mike Smith University of Brighton
 * @version 2.2
 *
 */
public class Basket extends ArrayList<Product> implements Serializable
{
  private static final long serialVersionUID = 1;
  private int    theOrderNum = 0;          // Order number
  
  /**
   * Constructor for a basket which is
   *  used to represent a customer order/ wish list
   */
  public Basket()
  {
    theOrderNum  = 0;
  }
  
  /**
   * Set the customers unique order number
   * Valid order Numbers 1 .. N
   * @param anOrderNum A unique order number
   */
  public void setOrderNum( int anOrderNum )
  {
    theOrderNum = anOrderNum;
  }

  /**
   * Returns the customers unique order number
   * @return the customers order number
   */
  public int getOrderNum()
  {
    return theOrderNum;
  }
  
  /**
   * Add a product to the Basket.
   * Product is appended to the end of the existing products
   * in the basket.
   * @param pr A product to be added to the basket
   * @return true if successfully adds the product
   */
  // Will be in the Java doc for Basket
  @Override
  public boolean add(Product newProduct) {
      if (newProduct == null) {
          throw new IllegalArgumentException("Cannot add a null product to the basket.");
      }

      // Check if the product already exists in the basket
      for (Product product : this) {
          if (product.getProductNum().equals(newProduct.getProductNum())) {
              // Update the quantity of the existing product
              int updatedQuantity = product.getQuantity() + newProduct.getQuantity();
              product.setQuantity(updatedQuantity);
              return true; // Successfully updated
          }
      }

      // If the product doesn't exist, add it as a new entry
      return super.add(newProduct);
  }

  /**
   * Returns a description of the products in the basket suitable for printing.
   * @return a string description of the basket products
   */
  public String getDetails() {
	    StringBuilder details = new StringBuilder();
	    double totalPrice = 0.0;

	    for (Product product : this) {
	        double productTotal = product.getPrice() * product.getQuantity();
	        totalPrice += productTotal;
	        details.append(product.getDescription())
	               .append(" - £")
	               .append(product.getPrice())
	               .append(" x ")
	               .append(product.getQuantity())
	               .append(" = £")
	               .append(String.format("%.2f", productTotal))
	               .append("\n");
	    }

	    details.append("\nTotal Price: £").append(String.format("%.2f", totalPrice));
	    return details.toString();
	}

}
