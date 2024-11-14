import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;  // Import URI for the payment URL
import java.awt.Desktop;  // Import Desktop for opening a browser
import java.util.Timer;
import java.util.TimerTask;


public class CarRentalSystemApp extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/car_rental_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Sanjana@123";  // Update to your MySQL password

    public CarRentalSystemApp() {
        setTitle("Car Rental System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create a custom JPanel with a background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image background = new ImageIcon("C:\\Users\\thanu\\Downloads\\homepage1.jpg").getImage();
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);  // Set background panel as content pane

        JLabel welcomeLabel = new JLabel("Welcome to the Car Rental System", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 42));
        backgroundPanel.add(welcomeLabel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);  // Make top panel transparent to see background
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        topPanel.add(loginButton);
        topPanel.add(registerButton);
        backgroundPanel.add(topPanel, BorderLayout.NORTH);

        loginButton.addActionListener(e -> new LoginWindow());
        registerButton.addActionListener(e -> new RegisterWindow());

        setVisible(true);
    }


    private static class LoginWindow extends JFrame {
        public LoginWindow() {
            setTitle("Login");
            setSize(300, 200);
            setLayout(new BorderLayout());
            setLocationRelativeTo(null);

            JPanel centerPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            JTextField usernameField = new JTextField(15);
            JPasswordField passwordField = new JPasswordField(15);
            JButton loginButton = new JButton("Login");

            gbc.gridx = 0;
            gbc.gridy = 0;
            centerPanel.add(new JLabel("Username:"), gbc);
            gbc.gridx = 1;
            centerPanel.add(usernameField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            centerPanel.add(new JLabel("Password:"), gbc);
            gbc.gridx = 1;
            centerPanel.add(passwordField, gbc);
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            centerPanel.add(loginButton, gbc);

            loginButton.addActionListener(e -> loginUser(usernameField.getText(), new String(passwordField.getPassword())));
            add(centerPanel, BorderLayout.CENTER);
            setVisible(true);
        }

        private void loginUser(String username, String password) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login Successful! Welcome, " + username);
                    new ProfileWindow(username);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password.");
                }
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static class RegisterWindow extends JFrame {
        public RegisterWindow() {
            setTitle("Register");
            setSize(300, 300);
            setLayout(new BorderLayout());
            setLocationRelativeTo(null);

            JPanel centerPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            JTextField usernameField = new JTextField(15);
            JPasswordField passwordField = new JPasswordField(15);
            JTextField emailField = new JTextField(15);
            JButton registerButton = new JButton("Register");

            gbc.gridx = 0;
            gbc.gridy = 0;
            centerPanel.add(new JLabel("Username:"), gbc);
            gbc.gridx = 1;
            centerPanel.add(usernameField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            centerPanel.add(new JLabel("Password:"), gbc);
            gbc.gridx = 1;
            centerPanel.add(passwordField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 2;
            centerPanel.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1;
            centerPanel.add(emailField, gbc);
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            centerPanel.add(registerButton, gbc);

            registerButton.addActionListener(e -> registerUser(usernameField.getText(), new String(passwordField.getPassword()), emailField.getText()));
            add(centerPanel, BorderLayout.CENTER);
            setVisible(true);
        }

        private void registerUser(String username, String password, String email) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)");
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setString(3, email);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Registration Successful!");
                stmt.close();
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062) {
                    JOptionPane.showMessageDialog(this, "Username or Email already exists.");
                } else {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ProfileWindow extends JFrame {
        public ProfileWindow(String username) {
            setTitle("Profile - " + username);
            setSize(600, 300);
            setLocationRelativeTo(null);

            // Create a JPanel with a background image for the profile page
            JPanel profileBackgroundPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Image background = new ImageIcon("C:\\Users\\thanu\\Downloads\\profile.png").getImage();
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                }
            };
            profileBackgroundPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
            setContentPane(profileBackgroundPanel);  // Set profile background panel as content pane

            JButton calendarManagementButton = new JButton("Calendar Management");
            JButton feedbackButton = new JButton("Feedback");
            JButton carManagementButton = new JButton("Car Management");
            JButton paymentButton = new JButton("Make Payment");

            // Set preferred button size for better UI
            calendarManagementButton.setPreferredSize(new Dimension(160, 60));
            feedbackButton.setPreferredSize(new Dimension(160, 60));
            carManagementButton.setPreferredSize(new Dimension(160, 60));
            paymentButton.setPreferredSize(new Dimension(160, 60));

            calendarManagementButton.addActionListener(e -> new CalendarManagementWindow());
            feedbackButton.addActionListener(e -> new FeedbackWindow());
            carManagementButton.addActionListener(e -> new carmanagement());

            // Action for the payment button
            paymentButton.addActionListener(e -> {
                try {
                    // Redirect to the dummy Stripe page (replace with your actual Stripe link)
                    String url = "https://checkout.stripe.dev/preview"; // Dummy link for Stripe
                    URI uri = new URI(url);  // Create URI
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().browse(uri);  // Open the URL in default browser
                    } else {
                        JOptionPane.showMessageDialog(this, "Desktop not supported, can't open browser.");
                    }

                    // Simulate payment completion after 3 seconds
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // Display a confirmation message after payment completion
                            JOptionPane.showMessageDialog(ProfileWindow.this, "Payment Successful!\nThank you for your purchase.");
                        }
                    }, 3000);  // 3 seconds delay
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "An error occurred while processing the payment.");
                    ex.printStackTrace();
                }
            });

            // Add buttons to the profile panel
            profileBackgroundPanel.add(calendarManagementButton);
            profileBackgroundPanel.add(feedbackButton);
            profileBackgroundPanel.add(carManagementButton);
            profileBackgroundPanel.add(paymentButton);  // Add the payment button

            setVisible(true);
        }
    }


    private static class CalendarManagementWindow extends JFrame {
        public CalendarManagementWindow() {
            setTitle("Calendar Management");
            setSize(400, 400);
            setLocationRelativeTo(null);
            setLayout(new FlowLayout());

            JLabel dateLabel = new JLabel("Enter Date (YYYY-MM-DD): ");
            JTextField dateField = new JTextField(10);
            JButton searchButton = new JButton("Search");

            // Create labels and buttons for each car
            JLabel availableCarsLabel = new JLabel("");  // Label for displaying available cars
            JButton hondaBookButton = new JButton("Book Honda");
            JButton marutiBookButton = new JButton("Book Maruti");
            JButton innovaBookButton = new JButton("Book Innova");

            // Initially set the buttons to be invisible
            hondaBookButton.setVisible(false);
            marutiBookButton.setVisible(false);
            innovaBookButton.setVisible(false);

            searchButton.addActionListener(e -> {
                String date = dateField.getText();
                if (date.isEmpty()) {
                    availableCarsLabel.setText("Please enter a date."); // Display message if date is empty
                } else {
                    // Display available cars
                    availableCarsLabel.setText("Cars available on " + date + ": Honda, Maruti, Innova");

                    // Show the individual book buttons for each car
                    hondaBookButton.setVisible(true);
                    marutiBookButton.setVisible(true);
                    innovaBookButton.setVisible(true);

                    // Hide date input and search button after search
                    dateLabel.setVisible(false);
                    dateField.setVisible(false);
                    searchButton.setVisible(false);
                }
            });

            // Action listeners for each book button
            hondaBookButton.addActionListener(e -> 
                JOptionPane.showMessageDialog(this, "Booking confirmed for Honda on " + dateField.getText() + "!")
            );
            marutiBookButton.addActionListener(e -> 
                JOptionPane.showMessageDialog(this, "Booking confirmed for Maruti on " + dateField.getText() + "!")
            );
            innovaBookButton.addActionListener(e -> 
                JOptionPane.showMessageDialog(this, "Booking confirmed for Innova on " + dateField.getText() + "!")
            );

            add(dateLabel);
            add(dateField);
            add(searchButton);
            add(availableCarsLabel);
            add(hondaBookButton);
            add(marutiBookButton);
            add(innovaBookButton);

            setVisible(true);
        }
    }

    private static class FeedbackWindow extends JFrame {
        public FeedbackWindow() {
            setTitle("Feedback");
            setSize(400, 300);
            setLocationRelativeTo(null);

            JLabel feedbackLabel = new JLabel("Enter your feedback:");
            JTextArea feedbackArea = new JTextArea(10, 30);
            JButton submitButton = new JButton("Submit");

            submitButton.addActionListener(e -> {
                String feedback = feedbackArea.getText();
                if (feedback.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter your feedback.");
                } else {
                    JOptionPane.showMessageDialog(this, "Thank you for your feedback!");
                    dispose();
                }
            });

            setLayout(new FlowLayout());
            add(feedbackLabel);
            add(new JScrollPane(feedbackArea));
            add(submitButton);

            setVisible(true);
        }
    }

    private static class carmanagement extends JFrame {
        public carmanagement() {
            setTitle("Car Management");
            setSize(600, 400);
            setLocationRelativeTo(null);
            setLayout(new FlowLayout());

            JButton registerButton = new JButton("Register Car");

            // Action listener for Register Car button
            registerButton.addActionListener(e -> new RegisterCarWindow());

            // Add the Register Car button to the window
            add(registerButton);

            setVisible(true);
        }
    }


    private static class RegisterCarWindow extends JFrame {
        public RegisterCarWindow() {
            setTitle("Register Car");
            setSize(400, 300);
            setLocationRelativeTo(null);
            setLayout(new GridBagLayout());
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            // Create input fields
            JTextField carNameField = new JTextField(15);
            JTextField carNumberField = new JTextField(15);
            JButton submitButton = new JButton("Register Car");

            // Add labels and text fields to the panel
            gbc.gridx = 0;
            gbc.gridy = 0;
            add(new JLabel("Car Name:"), gbc);
            gbc.gridx = 1;
            add(carNameField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            add(new JLabel("Car Number:"), gbc);
            gbc.gridx = 1;
            add(carNumberField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            add(submitButton, gbc);

            // Action for Register button
            submitButton.addActionListener(e -> {
                String carName = carNameField.getText().trim();
                String carNumber = carNumberField.getText().trim();

                if (carName.isEmpty() || carNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill in both fields.");
                } else {
                    // Here you can add code to save the car to the database if needed
                    // For now, we'll show a success message
                    JOptionPane.showMessageDialog(this, "Car Registered Successfully!");

                    // Close the window after registration
                    dispose();
                }
            });

            setVisible(true);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(CarRentalSystemApp::new);
    }
}