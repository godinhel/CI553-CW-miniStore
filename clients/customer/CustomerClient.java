package clients.customer;

import clients.customer.CustomerController;
import clients.customer.CustomerModel;
import clients.customer.CustomerView;
import debug.DEBUG;
import middle.MiddleFactory;
import middle.Names;
import middle.RemoteMiddleFactory;
import middle.StockException;
import middle.StockReader;

import java.util.List;
import java.util.ArrayList;

import javax.swing.*;

import catalogue.Product;

/**
 * The standalone Customer Client
 */
public class CustomerClient
{
    private StockReader theStock;

    public CustomerClient(MiddleFactory mf) throws StockException {
        if (mf == null) {
            throw new IllegalArgumentException("MiddleFactory cannot be null!");
        }
        this.theStock = mf.makeStockReader(); // Initialise StockReader
        if (this.theStock == null) {
            throw new IllegalStateException("Failed to initialise StockReader!");
        }
        System.out.println("StockReader initialised successfully.");
    }


    public static void main(String[] args) {
        try {
            String stockURL = args.length < 1 ? Names.STOCK_R : args[0];
            RemoteMiddleFactory mrf = new RemoteMiddleFactory();
            mrf.setStockRInfo(stockURL);

            System.out.println("Starting the application...");
            displayGUI(mrf); // Call to initialise the GUI
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Application failed to start: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private static void displayGUI(MiddleFactory mf) {
        try {
            System.out.println("Initialising displayGUI...");
            JFrame window = new JFrame("Customer Application");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Create MVC components
            CustomerModel model = new CustomerModel(mf);
            CustomerView view = new CustomerView(window, mf, 0, 0);
            CustomerController controller = new CustomerController(model, view);

            // Initialize the CustomerClient
            CustomerClient client = new CustomerClient(mf);
            System.out.println("CustomerClient successfully created.");

            // Pass the controller and client to the view
            view.setController(controller);
            view.setClient(client);
            System.out.println("setClient() successfully called.");

            // Attach the view to observe the model
            model.addObserver(view);

            // Make the window visible
            window.setVisible(true);
            System.out.println("Application window displayed.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error initialising displayGUI: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public List<Product> getAvailableProducts() {
        List<Product> products = new ArrayList<>();
        try {
            if (theStock == null) {
                throw new IllegalStateException("StockReader is not initialized!");
            }
            // Assume `theStock.getAllProductNumbers()` returns a list of product IDs
            List<String> productNumbers = theStock.getAllProductNumbers();
            for (String productNum : productNumbers) {
                Product product = theStock.getDetails(productNum);
                products.add(product);
            }
        } catch (StockException e) {
            DEBUG.error("CustomerClient.getAvailableProducts\n" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
}
