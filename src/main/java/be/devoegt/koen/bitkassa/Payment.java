package be.devoegt.koen.bitkassa;

import java.time.LocalDate;

import org.json.JSONObject;

/**
 * @author Koen De Voegt
 *
 */
public class Payment {

	// Constants
	private static final String startAction = "start_payment";
	private static final String checkAction = "get_payment_status";
	
	// Variables
	/**
	 * The current status of this payment can be either OPEN, CANCELLED, EXPIRED, PENDING or PAID
	 * **/
	private Status status;
	/**
	 * An auto generated information string that is send along with the transaction for easy recognition
	 */
	private String metaInfo;
	/**
	 * The currency and amount of this payment
	 */
	private Amount amount;
	/**
	 * A description that is attached to this payment
	 */
	private String description;
	/**
	 * The Merchant ID of whom is being payed
	 */
	private String merchantId;
	/**
	 * The payment ID assigned by bitkassa.nl
	 */
	private String ID;
	/**
	 * The URL in case you want to allow users to pay trough the bitkassa.nl website
	 */
	private String URL;
	/**
	 * The bitcoin address of this payment
	 */
	private String address;
	/**
	 * The amount to pay in Bitcoin, as converted by bitkassa.nl if applicable
	 */
	private String BTCamount;
	/**
	 * The Bitcoin url for this payment
	 */
	private String btcUrl;
	/**
	 * The time this payment expires
	 */
	private String expire;

	public Payment(String merchantId, Amount amount, String description) {
		setMerchantId(merchantId);
		setAmount(amount);
		setDescription(description.trim());
		
		// Generate the metainfo String
		LocalDate date = LocalDate.now();
		metaInfo = String.format("BTC%s%02d%02d", date.getYear(), date.getMonthValue(), date.getDayOfMonth());

	}

	public JSONObject startJson() {
		JSONObject paymentJSON = amount.toJson();
		paymentJSON.put("action", startAction);
		paymentJSON.put("merchant_id", merchantId);
		paymentJSON.put("meta_info", metaInfo);
		return paymentJSON;
	}
	
	public JSONObject getStatusJson() {
		JSONObject statusJson = new JSONObject();
		statusJson.put("action", checkAction);
		statusJson.put("merchant_id", merchantId);
		statusJson.put("payment_id", ID);
		return statusJson;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}

	public Amount getAmount() {
		return amount;
	}

	public void setAmount(Amount amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMetaInfo() {
		return metaInfo;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBTCamount() {
		return BTCamount;
	}

	public void setBTCamount(String bTCamount) {
		BTCamount = bTCamount;
	}
	
	public String getBtcUrl() {
		return btcUrl;
	}

	public void setBtcUrl(String btcUrl) {
		this.btcUrl = btcUrl;
	}
	
	public String getExpire() {
		return expire;
	}

	public void setExpire(String expire) {
		this.expire = expire;
	}
	
}
