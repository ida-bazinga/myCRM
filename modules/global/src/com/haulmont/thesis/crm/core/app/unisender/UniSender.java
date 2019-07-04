/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.unisender;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.thesis.crm.core.app.unisender.entity.Campaign;
import com.haulmont.thesis.crm.core.app.unisender.entity.FieldData;
import com.haulmont.thesis.crm.core.app.unisender.entity.LogMessage;
import com.haulmont.thesis.crm.core.app.unisender.entity.MailList;
import com.haulmont.thesis.crm.core.app.unisender.exceptions.MethodExceptionCode;
import com.haulmont.thesis.crm.core.app.unisender.exceptions.UniSenderConnectException;
import com.haulmont.thesis.crm.core.app.unisender.exceptions.UniSenderInvalidResponseException;
import com.haulmont.thesis.crm.core.app.unisender.exceptions.UniSenderMethodException;
import com.haulmont.thesis.crm.core.app.unisender.requests.ExportContactsRequest;
import com.haulmont.thesis.crm.core.app.unisender.requests.GetCampaignDeliveryStatsRequest;
import com.haulmont.thesis.crm.core.app.unisender.requests.ImportContactsRequest;
import com.haulmont.thesis.crm.core.app.unisender.responses.GetCampaignDeliveryStatsResponse;
import com.haulmont.thesis.crm.core.app.unisender.responses.ImportContactsResponse;
import com.haulmont.thesis.crm.core.app.unisender.utils.MapUtils;
import com.haulmont.thesis.crm.core.app.unisender.utils.URLEncodedUtils;
import com.haulmont.thesis.crm.core.config.CrmConfig;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Ivanov D.A.
 */

public class UniSender {
	
	private String apiKey;
	private String language;
	private Boolean useHttps;


	static final String API_HOST = "api.unisender.com";
	static final String API_ENCODING = "UTF-8";
	
	
	
	public UniSender() {
		this("ru", true);
	}

	public UniSender(String language, boolean useHttps) {
		super();
		this.apiKey = AppBeans.get(Configuration.class).getConfig(CrmConfig.class).getUniKey();
		this.language = language;
		this.useHttps = useHttps;

	}

    private URL makeURL(String method) {
		return makeURL(this.language, method);
	}

	private URL makeURL(String language, String method) {
		String file = String.format("/%s/api/%s?format=json", language,	method);
		try {
			return new URL(useHttps ? "https" : "http", API_HOST, file);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String makeQuery(Map<String, String> args) {
		if (args == null){
			args = new LinkedHashMap<String, String>(1);
		}
		
		args.put("api_key", this.apiKey);
		
		return URLEncodedUtils.formatQuery(args, API_ENCODING);
	}

	private void checkErrors(JsonNode response)  throws UniSenderInvalidResponseException{
		if (response.has("error")){
			try {
				String errorMsg = response.get("error").asText();
				String code = response.get("code").asText();
				
				MethodExceptionCode mec = MethodExceptionCode.UNKNOWN;
				if ("unspecified".equals(code)){
					mec = MethodExceptionCode.UNSPECIFIED;
				} else if ("invalid_api_key".equals(code)) {
					mec = MethodExceptionCode.INVALID_API_KEY;
				} else if ("access_denied".equals(code)) {
					mec = MethodExceptionCode.ACCESS_DENIED;
				} else if ("unknown_method".equals(code)) {
					mec = MethodExceptionCode.UNKNOWN_METHOD;
				} else if ("invalid_arg".equals(code)) {
					mec = MethodExceptionCode.INVALID_ARG;
				} else if ("not_enough_money".equals(code)) {
					mec = MethodExceptionCode.NOT_ENOUGH_MONEY;
				} else if ("too_many_double_optins".equals(code)) {
                    mec = MethodExceptionCode.TOO_MANY_DOUBLE_OPTINS;
                }
				throw new UniSenderMethodException(mec, errorMsg);
				
			} catch (Exception e) {
				throw new UniSenderInvalidResponseException(e);
			}
		}
	}
	
	protected JsonNode executeMethod(String method, Map<String, String> args)
					throws UniSenderConnectException, UniSenderInvalidResponseException {
		URL url = makeURL(method);
		String output = execute(url, makeQuery(args));
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode response = mapper.readValue(output, JsonNode.class);
			checkErrors(response);
			return response;
		} catch (Exception e) {
			throw new UniSenderInvalidResponseException(e, output);
		}
	}

	protected String execute(URL url, String postQuery) throws UniSenderConnectException {
		HttpsURLConnection urlc = null;
		try {

			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
				public void checkClientTrusted(X509Certificate[] certs, String authType) { }
				public void checkServerTrusted(X509Certificate[] certs, String authType) { }

			} };

			SSLContext sc = SSLContext.getInstance("TLSv1.2");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			urlc = (HttpsURLConnection) url.openConnection();
			urlc.setUseCaches(false);
			urlc.setInstanceFollowRedirects(false);

			urlc.setRequestMethod("POST");
			urlc.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			urlc.setRequestProperty("Content-Length", "" + postQuery.getBytes().length);
			urlc.setRequestProperty("Accept", "application/json, text/html, text/plain, text/javascript");

			urlc.setDoOutput(true);
			urlc.setDoInput(true);

			DataOutputStream os = new DataOutputStream(getOutputStream(urlc));
			os.writeBytes(postQuery);
			os.flush();
			os.close();

			InputStreamReader isr = new InputStreamReader(getInputStream(urlc));
			BufferedReader rd = new BufferedReader(isr);

			char[] buffer = new char[255];
			int read = 0;
			StringBuilder sb = new StringBuilder();
			while ((read = rd.read(buffer)) != -1) {
				sb.append(buffer, 0, read);
			}
			rd.close();

			return sb.toString();
		} catch (NoSuchAlgorithmException | KeyManagementException | IOException e){
			throw new UniSenderConnectException(e);
		}
		finally {
			if (urlc != null){
				urlc.disconnect();
			}
		}
	}

    protected InputStream getInputStream(HttpURLConnection urlc) throws IOException {
        return urlc.getInputStream();
    }

    protected OutputStream getOutputStream(HttpURLConnection urlc) throws IOException {
        return urlc.getOutputStream();
    }

    private Map<String, String> createMap(){
		return new LinkedHashMap<String, String>();
	}
	
	private Map<String, String> createMap(String argName, String argVal){
		Map<String, String> m = createMap();
		m.put(argName, argVal);
		return m;
	}
		
	public ImportContactsResponse importContacts(ImportContactsRequest ic) throws UniSenderConnectException, UniSenderInvalidResponseException {
		Map<String, String> map = createMap();
		FieldData fd = ic.getFieldData(); 
		
		int filedsC = fd.getFiledCount();
		for (int i = 0; i < filedsC; ++i)
		{
			map.put("field_names[" + i + "]", fd.getField(i));
		}
		
		int dataC = fd.getDataCount();
		for (int i = 0; i < dataC; ++i)
		{
			List<String> data = fd.getData(i);
			int dn = 0;
			for (String d: data){
				map.put(String.format(
						"data[%s][%s]", i, dn),
						d);
				++dn;
			}
		}
		
		MapUtils.putIfNotNull(map, "double_optin", ic.getDoubleOptin());
		MapUtils.putIfNotNull(map, "overwrite_tags", ic.getOverwriteTags());
		MapUtils.putIfNotNull(map, "overwrite_lists", ic.getOverwriteLists());

		JsonNode response = executeMethod("importContacts", map);
		try {
			List<LogMessage> logs = null;
			JsonNode res = response.get("result");
			JsonNode log  = res.get("log");
			
			if (log != null){
				logs = new ArrayList<LogMessage>();
				for (int i = 0; i < log.size(); ++i)
				{
					JsonNode jso = log.get(i);
					logs.add( new LogMessage(
						jso.get("index").asInt(),
						jso.get("code").asText(),
						jso.get("message").asText()
					));
				}
			}
			
			return new ImportContactsResponse(
					res.get("total").asInt(),
					res.get("inserted").asInt(),
					res.get("updated").asInt(),
					res.get("deleted").asInt(),
					res.get("new_emails").asInt(),
					logs
			);
		} catch (Exception e) {
			throw new UniSenderInvalidResponseException(e);
		}
	}

	public MailList createList(MailList list) throws UniSenderConnectException, UniSenderInvalidResponseException {
		Map<String, String> p = createMap("title", list.getTitle());

		JsonNode response = executeMethod("createList", p);
		try {
			JsonNode result = response.get("result");
			list.setId(result.get("id").asInt());
		} catch (Exception e) {
			throw new UniSenderInvalidResponseException(e);
		}
		return list;
	}

	public GetCampaignDeliveryStatsResponse getCampaignDeliveryStats(GetCampaignDeliveryStatsRequest sr) throws UniSenderConnectException, UniSenderInvalidResponseException {
		Map<String, String> map = createMap();
		MapUtils.putIfNotNull(map, "campaign_id", sr.getCampaign().getId());
		MapUtils.putIfNotNull(map, "changed_since", sr.getChanged_since());

		JsonNode response = executeMethod("getCampaignDeliveryStats", map);
		try {
			final JsonNode res = response.get("result");
			final List<String> fields = new ArrayList<String>();
			final List<List<String>> data = new ArrayList<List<String>>();

			final JsonNode jsFields = res.get("fields");
			for (int i = 0; i < jsFields.size(); ++i)
			{
				fields.add(jsFields.get(i).asText());
			}
			JsonNode jsData = res.get("data");
			for (int i = 0; i < jsData.size(); ++i)
			{
				ArrayList<String> info = new ArrayList<String>();
				data.add(info);
				JsonNode jsDataFields = jsData.get(i);

				for (int j = 0; j < jsDataFields.size(); ++j)
				{
					info.add(jsDataFields.get(j).asText());
				}
			}

			return new GetCampaignDeliveryStatsResponse(fields, data);
		} catch (Exception e) {
			throw new UniSenderInvalidResponseException(e);
		}

	}

	public ArrayList<Campaign> getCampaigns() throws UniSenderConnectException, UniSenderInvalidResponseException {
		Map<String, String> map = createMap();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Calendar calendar = Calendar.getInstance();
		//Date to = calendar.getTime();
		calendar.roll(Calendar.MONTH, -1);
		calendar.set(Calendar.HOUR, 9);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date from = calendar.getTime();

		MapUtils.putIfNotNull(map, "from", dateFormat.format(from));
		//MapUtils.putIfNotNull(map, "to", dateFormat.format(to));

		JsonNode response = executeMethod("getCampaigns", map);
		ArrayList<Campaign> campaigns = new ArrayList<>();
		try {
			final JsonNode result = response.get("result");
			ObjectMapper mapper = new ObjectMapper();
			for (int i = 0; i < result.size(); ++i)
			{
				campaigns.add(mapper.readValue(result.get(i).traverse(), Campaign.class));
			}

			return campaigns;

		} catch (Exception e) {
			throw new UniSenderInvalidResponseException(e);
		}
	}

	public FieldData exportContacts(ExportContactsRequest ecr) throws UniSenderConnectException, UniSenderInvalidResponseException {
		Map<String, String> map = createMap();

		MailList mailList = ecr.getListId();
		if (mailList != null){
			MapUtils.putIfNotNull(map, "list_id", mailList.getId());
		}

		List<String> fields = ecr.getFieldNames();
		if (fields != null){
			int count = 0;
			for (String field: fields){
				MapUtils.putIfNotNull(map, "field_names["+count+"]", field);
				++count;
			}
		}

		MapUtils.putIfNotNull(map, "offset", ecr.getOffset());
		MapUtils.putIfNotNull(map, "limit", ecr.getLimit());

		JsonNode response = executeMethod("exportContacts", map);
		try {
			JsonNode res = response.get("result");

			final FieldData fd = new FieldData();

			JsonNode jfields = res.get("field_names");
			for (int i = 0; i < jfields.size(); ++i)
			{
				final String field = jfields.get(i).asText();
				fd.addField(field);
			}

			final JsonNode jdatas = res.get("data");
			for (int i = 0; i < jdatas.size(); ++i){
				JsonNode jdata = jdatas.get(i);
				final List<String> values = new ArrayList<>();
				for (int j = 0; j < jdata.size(); ++j){
					values.add(jdata.get(j).asText());
				}
				fd.addValues(values);
			}
			return fd;

		} catch (Exception e) {
			throw new UniSenderInvalidResponseException(e);
		}
	}

	public GetCampaignDeliveryStatsResponse getVisitedLinks(GetCampaignDeliveryStatsRequest sr) throws UniSenderConnectException, UniSenderInvalidResponseException {

		Map<String, String> map = createMap();
		MapUtils.putIfNotNull(map, "campaign_id", sr.getCampaign().getId());
		MapUtils.putIfNotNull(map, "group", sr.isGroup());

		JsonNode response = executeMethod("getVisitedLinks", map);

		try {
			final JsonNode res = response.get("result");
			final List<String> fields = new ArrayList<String>();
			final List<List<String>> data = new ArrayList<List<String>>();

			final JsonNode jsFields = res.get("fields");
			for (int i = 0; i < jsFields.size(); ++i)
			{
				fields.add(jsFields.get(i).asText());
			}
			JsonNode jsData = res.get("data");
			for (int i = 0; i < jsData.size(); ++i)
			{
				ArrayList<String> info = new ArrayList<String>();
				data.add(info);
				JsonNode jsDataFields = jsData.get(i);

				for (int j = 0; j < jsDataFields.size(); ++j)
				{
					info.add(jsDataFields.get(j).asText());
				}
			}

			return new GetCampaignDeliveryStatsResponse(fields, data);
		} catch (Exception e) {
			throw new UniSenderInvalidResponseException(e);
		}

	}


}
