package be.devoegt.koen.bitkassa;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

public class Transaction {

	private String apiKey;
	private Payment payment;

	public Transaction(String apiKey, Payment payment) {
		this.apiKey = apiKey;
		this.payment = payment;
	}

	private URLConnection sendJSON(JSONObject jsonMessage) throws IOException {
		long unixtimestamp = System.currentTimeMillis() / 1000L;

		Base64 base64 = new Base64();
		String encodedVersion = new String(base64.encode(jsonMessage.toString()
				.getBytes()));

		String sha256hex = DigestUtils.sha256Hex(apiKey
				+ jsonMessage.toString() + unixtimestamp);

		URL url = new URL("https://www.bitkassa.nl/api/v1?p=" + encodedVersion
				+ "&a=" + sha256hex + unixtimestamp);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Content-Type", "application/json");
		urlConnection.setDoOutput(true);

		OutputStreamWriter wr = new OutputStreamWriter(
				urlConnection.getOutputStream());
		wr.write(jsonMessage.toString());
		wr.flush();

		wr.close();

		return urlConnection;
	}

	private JSONObject receiveJSON(URLConnection urlConnection)
			throws IOException {
		InputStream is = urlConnection.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);

		int numCharsRead;
		char[] charArray = new char[1024];
		StringBuffer sb = new StringBuffer();
		while ((numCharsRead = isr.read(charArray)) > 0) {
			sb.append(charArray, 0, numCharsRead);
		}

		JSONObject response = new JSONObject(sb.toString());
		is.close();

		return response;
	}

	public void start() throws IOException {

		JSONObject paymentJSON = payment.startJson();
		URLConnection urlConnection = sendJSON(paymentJSON);
		JSONObject response = receiveJSON(urlConnection);

		boolean success = (Boolean) response.get("success");
		if (success) {
			payment.setID(response.getString("payment_id"));
			payment.setURL(response.getString("payment_url"));
			payment.setAddress(response.getString("address"));
			payment.setBTCamount(response.getString("amount"));
			payment.setBtcUrl(response.getString("bitcoin_url"));
			payment.setExpire(response.getString("expire"));
		}

	}

	public void checkStatus() throws IOException {

		JSONObject statusJson = payment.getStatusJson();
		URLConnection urlConnection = sendJSON(statusJson);
		JSONObject response = receiveJSON(urlConnection);

		boolean success = (Boolean) response.get("success");
		if (success) {
			payment.setStatus(Status.valueOf(response.getString(
					"payment_status").toUpperCase()));
		}

	}

}
