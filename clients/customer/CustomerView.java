package clients.customer;

import catalogue.Basket;
import catalogue.Product;
import middle.MiddleFactory;
import middle.StockException;
import middle.StockReader;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Implements the Customer view with a sleek UI.
 */
public class CustomerView implements Observer {

    private final JTextArea basketTextArea = new JTextArea(); // For displaying basket details
    private final JPanel productPanel = new JPanel(new GridLayout(0, 2));

    private StockReader theStock = null;
    private CustomerController cont = null;
    private CustomerClient client = null;

    /**
     * Construct the view
     *
     * @param rpc   Window in which to construct
     * @param mf    Factory to deliver order and stock objects
     * @param x     x-coordinate of position of window on screen
     * @param y     y-coordinate of position of window on screen
     */
    public CustomerView(RootPaneContainer rpc, MiddleFactory mf, int x, int y) {
        try {
            theStock = mf.makeStockReader(); // Database Access
            client = new CustomerClient(mf); // Initialize client
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error initializing CustomerView: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        Container cp = rpc.getContentPane();
        cp.setLayout(new BorderLayout()); // Use BorderLayout for cleaner organization

        // Add header
        JPanel headerPanel = createHeaderPanel("Customer Shopping Application");
        cp.add(headerPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel mainContent = new JPanel(new BorderLayout());
        cp.add(mainContent, BorderLayout.CENTER);

        // Basket area
        basketTextArea.setEditable(false);
        basketTextArea.setLineWrap(true);
        basketTextArea.setWrapStyleWord(true);
        basketTextArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane basketScrollPane = new JScrollPane(basketTextArea);
        basketScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainContent.add(basketScrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton sendBasketButton = createModernButton("Send Basket");
        sendBasketButton.addActionListener(e -> cont.sendBasket());
        JButton selectProductsButton = createModernButton("Select Products");
        selectProductsButton.addActionListener(e -> {
            try {
                openProductPanel();
            } catch (StockException ex) {
                ex.printStackTrace();
            }
        });
        buttonsPanel.add(sendBasketButton);
        buttonsPanel.add(selectProductsButton);
        mainContent.add(buttonsPanel, BorderLayout.SOUTH);

        // Set window properties
        ((Container) rpc).setSize(600, 400);
        ((Container) rpc).setLocation(x, y);
        ((Container) rpc).setVisible(true);
    }

    /**
     * The controller object, used so that an interaction can be passed to the controller
     *
     * @param c The controller
     */
    public void setController(CustomerController c) {
        cont = c;
    }

    /**
     * The client object, used to access available products.
     *
     * @param cl The client
     */
    public void setClient(CustomerClient cl) {
        this.client = cl;
        System.out.println("Client set in CustomerView: " + (this.client != null));
    }

    /**
     * Update the view
     *
     * @param modelC The observed model
     * @param arg    Specific args
     */
    public void update(Observable modelC, Object arg) {
        CustomerModel model = (CustomerModel) modelC;
        Basket basket = model.getBasket();

        if (basket != null && !basket.isEmpty()) {
            // Populate the JTextArea with the basket details
            basketTextArea.setText(basket.getDetails());
        } else {
            // Show a placeholder message when the basket is empty
            basketTextArea.setText("Your basket is empty.");
        }
    }

    /**
     * Displays the product panel.
     *
     * @throws StockException
     */
    private void openProductPanel() throws StockException {
        if (client == null) {
            JOptionPane.showMessageDialog(null, "Client is not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFrame productFrame = new JFrame("Product Selection");
        productFrame.setSize(600, 400);
        productFrame.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(productPanel);
        productFrame.add(scrollPane, BorderLayout.CENTER);
        productFrame.setVisible(true);

        List<Product> products = client.getAvailableProducts();
        if (products.isEmpty()) {
            JOptionPane.showMessageDialog(productFrame, "No products available.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            displayProducts(products); // Fetch and display products from CustomerClient
        }
    }

    public void displayProducts(List<Product> products) throws StockException {
        productPanel.removeAll(); // Clear previous products

        for (Product product : products) {
            JPanel productCard = new JPanel(new BorderLayout());

            JLabel productImage = new JLabel();
            ImageIcon image = theStock.getImage(product.getProductNum());
            if (image != null) {
                productImage.setIcon(image);
            } else {
                productImage.setText("No Image Available");
            }

            JLabel productDetails = new JLabel(product.getDescription() + " - £" + product.getPrice());

            JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, product.getQuantity(), 1));
            JPanel quantityPanel = new JPanel();
            quantityPanel.add(new JLabel("Quantity:"));
            quantityPanel.add(quantitySpinner);

            JButton addButton = createModernButton("Add to Basket");
            addButton.addActionListener(e -> {
                int quantity = (int) quantitySpinner.getValue();
                cont.addToOrder(product.getProductNum(), quantity); // Pass product number and quantity
            });

            productCard.add(productImage, BorderLayout.CENTER);
            productCard.add(productDetails, BorderLayout.NORTH);
            productCard.add(quantityPanel, BorderLayout.EAST);
            productCard.add(addButton, BorderLayout.SOUTH);

            productPanel.add(productCard);
        }

        productPanel.revalidate();
        productPanel.repaint();
    }

    private JPanel createHeaderPanel(String title) {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, Color.DARK_GRAY, getWidth(), getHeight(), Color.LIGHT_GRAY);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(600, 50));
        JLabel headerLabel = new JLabel(title, SwingConstants.CENTER);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.add(headerLabel);
        return header;
    }

    private JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(70, 130, 180)); // Steel Blue
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false); // Remove focus border
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // Padding
        return button;
    }
}
