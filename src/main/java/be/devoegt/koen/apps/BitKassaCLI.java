package be.devoegt.koen.apps;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Scanner;

import be.devoegt.koen.bitkassa.Amount;
import be.devoegt.koen.bitkassa.Currency;
import be.devoegt.koen.bitkassa.Payment;
import be.devoegt.koen.bitkassa.Status;
import be.devoegt.koen.bitkassa.Transaction;

public class BitKassaCLI {

	public static void main(String[] args) throws InterruptedException {

		String currency = "";
		String description = "";
		BigDecimal inputAmount = BigDecimal.ZERO;
		Payment payment;
		Amount amount;
		Transaction transaction;

		Scanner input = new Scanner(System.in);
		input.useDelimiter("\\n");
		input.useLocale(Locale.ENGLISH);

		String apiKey = "";
		String merchantId = "";

		do {
			System.out
					.print("Please enter the currency you want to use(EUR/BTC): ");
			currency = input.nextLine();
			currency = currency.toUpperCase();
		} while (!(currency.equals(Currency.EUR.toString()) || currency
				.equals(Currency.BTC.toString())));

		do {
			System.out
					.print("Please enter the amount(. as decimal seperator): ");

			if (input.hasNextBigDecimal()) {
				inputAmount = input.nextBigDecimal();
			} else {
				input.nextLine();
			}
		} while (inputAmount == BigDecimal.ZERO);

		amount = new Amount(inputAmount, Currency.valueOf(currency));

		System.out.println("Enter a description:");
		description = input.next();

		payment = new Payment(merchantId, amount, description);

		String merchantCapitalized = merchantId.substring(0, 1).toUpperCase()
				+ merchantId.substring(1);

		System.out
				.printf("You want to pay %s %s to %s with description: %n%s%nPlease confirm(y/n): ",
						inputAmount, currency, merchantCapitalized, description);
		String answer = input.next();
		if (!answer.equals("y") && !answer.equals("Y")) {
			input.close();
			System.out.println("Payment aborted by user.");
			System.exit(0);
		}

		transaction = new Transaction(apiKey, payment);
		try {
			transaction.start();
		} catch (IOException e) {
			System.out.println("Unable to connected to API");
		}

		System.out
				.println("Please use one of these options to complete the payment.");
		System.out.println("Bitcoin url: " + payment.getBtcUrl());
		System.out.println("Payment through the bitkassa website: "
				+ payment.getURL());
		input.close();
		try {
			transaction.checkStatus();
		} catch (IOException e) {
			System.out.println("Unable to connected to API");
		}
		System.out.println("Payment status: " + payment.getStatus());
		while (payment.getStatus() == Status.OPEN) {
			Thread.sleep(300);
			try {
				transaction.checkStatus();
			} catch (IOException e) {
				System.out.println("Unable to connected to API");
			}
		}
		System.out.println("Payment status: " + payment.getStatus());
		if ((payment.getStatus() != Status.CANCELLED)
				&& (payment.getStatus() != Status.EXPIRED)) {
			while (payment.getStatus() == Status.PENDING) {
				Thread.sleep(300);
				try {
					transaction.checkStatus();
				} catch (IOException e) {
					System.out.println("Unable to connected to API");
				}
			}
			System.out.println("Payment status: " + payment.getStatus());
		}

	}

}
