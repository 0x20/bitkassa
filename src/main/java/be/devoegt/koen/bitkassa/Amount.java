package be.devoegt.koen.bitkassa;

import java.math.BigDecimal;

import org.json.JSONObject;

public class Amount {
	
	/**																																																																																																																																																																																																																																																																																																																																																																																																																																																												
	 * Either Euro (EUR) or Bitcoin (BTC)
	 * **/
	private Currency currency;
	/**
	 * The amount as it was entered by the user
	 */
	private BigDecimal amount;
	/**
	 * The amount as it is used internally aka Euro's in cents or Bitcoin in Satoshis
	 * 0,01 EUR = 1 Eurocent
	 * 0,00000001 BTC = 1 Satoshis
	 */
	private long internalAmount;
	
	public Amount(BigDecimal amount, Currency currency) {
		setAmount(amount);
		setCurrency(currency);
		if (currency.equals(Currency.EUR)) {
			internalAmount = amount.multiply(new BigDecimal(100)).longValue();
		} else if (currency.equals(Currency.BTC)) {
			internalAmount = amount.multiply(new BigDecimal(100000000)).longValue();
		}
	}

	public JSONObject toJson() {
		JSONObject amountJSON = new JSONObject();
		amountJSON.put("currency", currency.toString());
		amountJSON.put("amount", internalAmount);
		return amountJSON;
	}
	
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public long getInternalAmount() {
		return internalAmount;
	}

	public void setInternalAmount(long internalAmount) {
		this.internalAmount = internalAmount;
	}
	
}
