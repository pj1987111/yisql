package com.zhy.yisql.jdbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zhy.yisql.common.utils.http.HttpClientCrawler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
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
      return HttpClientCrawler.requestByMethodJson(String.format("http://%s:%s/sql/run", params.getHost(), params.getPort()),
          "POST", params.toJson(sql), false);
    } catch (JsonProcessingException e) {
      throw new YiSQLException(e.getMessage());
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
