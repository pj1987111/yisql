package com.zhy.yisql.jdbc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-02-27
 *  \* Time: 11:32
 *  \* Description: 
 *  \
 */
public class Utils {
  static final Pattern URL_PATTERN = Pattern.compile("([^:]+):([0-9]{1,5})/?");
  private static ObjectMapper mapper = new ObjectMapper();

  static {
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  /**
   * Parse JDBC connection URL The only supported format of the URL is:
   * jdbc:yisql://localhost:6049/.
   */
  public static YiSQLConnectionParam parseUrl(String url, Properties info) throws YiSQLException {
    Map<String, String> mergeP = parseMergeProperties(url, info);
    YiSQLConnectionParam params = new YiSQLConnectionParam(url);
    if (url.trim().equalsIgnoreCase(Config.YiSQL_URL_PREFIX)) {
      return params;
    }
    if (!url.startsWith(Config.YiSQL_URL_PREFIX)) {
      throw new YiSQLException(
          "Error url format, url should be jdbc:yisql://anything:port/ or jdbc:yisql://anything:port");
    }
    params.setHost(mergeP.get(Config.HOST));
    params.setPort(Integer.parseInt(mergeP.get(Config.PORT)));

    if (mergeP.containsKey(Config.AUTH_USER)) {
      params.setUsername(mergeP.get(Config.AUTH_USER));
    }
    if (mergeP.containsKey(Config.DEFAULT_PATH_PREFIX)) {
      params.setDefaultPathPrefix(mergeP.get(Config.DEFAULT_PATH_PREFIX));
    }
    if (mergeP.containsKey(Config.TIME_ZONE)) {
      params.setTimeZone(mergeP.get(Config.TIME_ZONE));
    }
    return params;
  }

  public static String internalExecuteQuery(String sql, YiSQLConnectionParam params) throws YiSQLException {
    try {
      return requestByMethodJson(String.format("http://%s:%s/sql/run", params.getHost(), params.getPort()),
          "POST", params.toJson(sql));
    } catch (Exception e) {
      throw new YiSQLException(e.getMessage());
    }
  }

  public static String requestByMethodJson(String url, String method, String paramsJson) throws Exception {
    Map<String, String> params = mapper.readValue(paramsJson, new TypeReference<Map<String, String>>() {
    });
    return requestByMethod(url, method, params);
  }

  private static CloseableHttpClient client() {
    return HttpClients.custom().build();
  }

  public static String requestByMethod(String url, String method, Map<String, String> params) throws Exception {

    CloseableHttpResponse response = null;
    CloseableHttpClient hc = client();
    try {
      HttpUriRequest request = null;
      switch (method.toLowerCase()) {
        case "get": {
          URIBuilder builder = new URIBuilder(url);
          params.forEach((s1, s2) -> builder.setParameter(s1, s2));
          request = new HttpGet(builder.build());
          break;
        }
        case "post": {
          List<BasicNameValuePair> newParams = new ArrayList<>();
          params.forEach((s1, s2) -> newParams.add(new BasicNameValuePair(s1, s2)));
          UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(newParams, Charset.forName("utf-8"));
          HttpPost httpPost = new HttpPost(url);
          httpPost.setEntity(urlEncodedFormEntity);
          request = httpPost;
          break;
        }
        default:
      }
      response = hc.execute(request);
      HttpEntity entity = response.getEntity();
      String res = null;
      if (entity != null) {
        res = EntityUtils.toString(entity);
      }
      return res;
    } finally {
      if (response != null) {
        response.close();
      }

    }
  }

  public static Map<String, String> parseMergeProperties(String url, Properties prop) throws YiSQLException {
    URI uri = null;
    try {
      uri = new URI(url.substring("jdbc:".length()));
    } catch (URISyntaxException e) {
      throw new YiSQLException(e.getMessage());
    }
    String uriPath = (uri.getPath() == null) ? "" : uri.getPath();
    Map<String, String> res = new HashMap<>();
    res.put(Config.HOST, uri.getHost());
    res.put(Config.PORT, (uri.getPort() == -1) ? Config.YISQL_DEFAULT_PORT : String.valueOf(uri.getPort()));
    res.put(Config.PATH, uriPath.replaceFirst("/", ""));

    if (uri.getQuery() != null) {
      String[] vals = uri.getQuery().split("&");
      for (String val : vals) {
        String[] parts = val.split("=");
        res.put(parts[0], parts[1]);
      }
    }

    if (prop != null) {
      for (Map.Entry<Object, Object> entry : prop.entrySet()) {
        res.put(entry.getKey().toString(), entry.getValue().toString());
      }
    }
    return res;
  }
}
