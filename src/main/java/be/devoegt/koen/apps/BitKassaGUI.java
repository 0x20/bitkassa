package be.devoegt.koen.apps;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import be.devoegt.koen.bitkassa.Amount;
import be.devoegt.koen.bitkassa.Currency;
import be.devoegt.koen.bitkassa.Payment;
import be.devoegt.koen.bitkassa.Transaction;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

public class BitKassaGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = -7612989623028609802L;

	// API Settings
	private static String apiKey = "";
	private static String merchantId = "";
	
	// the panels
	private JPanel firstPanel;
	private JPanel secondPanel;

	// the components
	private JButton payButton;
	private JButton quitButton;
	private JButton cancelButton;
	private JTextField amountField;
	private JComboBox<String> currencyField;
	
	// Right panel components
	private BufferedImage myPicture;
	private JLabel picLabel;

	public BitKassaGUI() throws IOException {

		// Get supported currencies
		Currency[] s = Currency.values();
		String[] currencyStrings = new String[s.length];
		int counter = 0;

		for (Currency c : s) {
			currencyStrings[counter++] = c.name();
		}

		// First panel
		firstPanel = new JPanel();
		firstPanel.setLayout(new GridLayout(0, 2));
		firstPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		// First panel components
		JLabel amount = new JLabel("Amount");
		JLabel currency = new JLabel("Currency");
		amountField = new JTextField(4);
		currencyField = new JComboBox<String>(currencyStrings);
		currencyField.setSelectedIndex(0);
		payButton = new JButton("Pay");
		payButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);

		firstPanel.add(Box.createVerticalGlue());
		firstPanel.add(Box.createVerticalGlue());
		firstPanel.add(amount);
		firstPanel.add(currency);
		firstPanel.add(amountField);
		firstPanel.add(currencyField);
		firstPanel.add(Box.createVerticalGlue());
		firstPanel.add(Box.createVerticalGlue());
		firstPanel.add(cancelButton);
		firstPanel.add(payButton);
		firstPanel.add(Box.createVerticalGlue());
		firstPanel.add(Box.createVerticalGlue());

		secondPanel = new JPanel();
		JButton button = new JButton();
		secondPanel.add(button);
		secondPanel.setVisible(false);

		// Right panel
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setBorder(new EmptyBorder(50, 0, 50, 50));

		// Right panel components
		// BufferedImage
		myPicture = ImageIO.read(new File("logo-0x20.png"));
		// JLabel
		picLabel = new JLabel(new ImageIcon(myPicture));

		rightPanel.add(Box.createVerticalGlue());
		rightPanel.add(picLabel);
		rightPanel.add(Box.createVerticalGlue());

		// Bottom panel
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		bottomPanel.add(Box.createHorizontalGlue());

		// Bottom panel components
		quitButton = new JButton("Quit");
		quitButton.addActionListener(this);

		bottomPanel.add(quitButton);

		// Add panels to window
		add(firstPanel, BorderLayout.LINE_START);
		add(rightPanel, BorderLayout.LINE_END);
		add(bottomPanel, BorderLayout.PAGE_END);

		pack();

		// Window settings
		setTitle("Whitespace BitKassa GUI");
		setSize(400, 250);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {

				BitKassaGUI ex;
				try {
					ex = new BitKassaGUI();
					ex.setVisible(true);
				} catch (IOException e) {
					System.err.println("IOException");
					e.printStackTrace();
				}
			}
		});
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == payButton) {
			try {
				BigDecimal amountValue = new BigDecimal(amountField.getText());
				Amount amount = new Amount(amountValue, Currency.valueOf(currencyField.getSelectedItem().toString()));
				Payment payment = new Payment(merchantId, amount, "BitKassaGUI");

				// Start transaction
				Transaction transaction = new Transaction(apiKey, payment);
				try {
					transaction.start();
				} catch (IOException ee) {
					System.out.println("Unable to connected to API");
				}
				myPicture = ImageIO.read(QRCode.from(payment.getBtcUrl())
						.to(ImageType.PNG).file());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			picLabel.setIcon(new ImageIcon(myPicture));
		} else if (e.getSource() == quitButton) {
			System.exit(0);
		} else if (e.getSource() == cancelButton) {
			currencyField.setSelectedIndex(0);
			amountField.setText("");
		}
	}
}
