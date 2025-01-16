package clients.customer;

import javax.swing.JOptionPane;

import catalogue.Basket;
import middle.OrderException;
import orders.Order;

/**
 * The Customer Controller
 */

public class CustomerController
{
  private CustomerModel model = null;
  private CustomerView  view  = null;
  private Order theOrder = null;
  /**
   * Constructor
   * @param model The model 
   * @param view  The view from which the interaction came
   */
  public CustomerController( CustomerModel model, CustomerView view )
  {
    this.view  = view;
    this.model = model;
  }

  /**
   * Check interaction from view
   * @param pn The product number to be checked
   */
  public void doCheck( String pn )
  {
    model.doCheck(pn);
  }

  /**
   * Clear interaction from view
   */
  public void doClear()
  {
    model.doClear();
  }
  
  public void addToOrder(String productNum, int quantity) {
	    if (quantity <= 0) {
	        System.out.println("Invalid quantity specified: " + quantity);
	        return;
	    }
	    model.addToBasket(productNum, quantity);
	}
  
  public void removeFromBasket(String productNum) {
      model.removeProductFromBasket(productNum);
  }
  
  public void sendBasket() {
	    Basket basket = model.getBasket();

	    if (basket == null || basket.isEmpty()) {
	        JOptionPane.showMessageDialog(null, "Your basket is empty.", "Error", JOptionPane.ERROR_MESSAGE);
	        return;
	    }

	    // Build the string to display
	    StringBuilder details = new StringBuilder("Order Details:\n\n");
	    basket.forEach(product -> {
	        details.append(product.getProductNum())
	               .append(" - ")
	               .append(product.getDescription())
	               .append(" x ")
	               .append(product.getQuantity())
	               .append("\n");
	    });

	    // Display the pop-up with the basket details
	    JOptionPane.showMessageDialog(null, details.toString(), "Basket Sent", JOptionPane.INFORMATION_MESSAGE);

	    // Clear the basket after sending
	    model.clearBasket();
	}


  

  

  
}

