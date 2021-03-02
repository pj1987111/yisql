package com.zhy.yisql.jdbc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-02-27
 *  \* Time: 11:21
 *  \* Description: 
 *  \
 */
public class YiSQLConnectionParam {
  private static ObjectMapper mapper = new ObjectMapper();

  static {
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  private String host = Config.YISQL_DEFAULT_HOST;
  private int port = Integer.parseInt(Config.YISQL_DEFAULT_PORT);
  private String jdbcUriString;
  private String username = Config.DEFAULT_USER;
  private String defaultPathPrefix;
  private String timeZone = Config.DEFAULT_TIME_ZONE;
  private String timestampFormat = Config.DEFAULT_TIMESTAMP_FORMAT;
  private String dateFormat = Config.DEFAULT_DATE_FORMAT;

  public YiSQLConnectionParam(String url) {
    this.jdbcUriString = url;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getJdbcUriString() {
    return jdbcUriString;
  }

  public void setJdbcUriString(String jdbcUriString) {
    this.jdbcUriString = jdbcUriString;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setDefaultPathPrefix(String defaultPathPrefix) {
    this.defaultPathPrefix = defaultPathPrefix;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  public String getTimestampFormat() {
    return timestampFormat;
  }

  public void setTimestampFormat(String timestampFormat) {
    this.timestampFormat = timestampFormat;
  }

  public String getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }

  public String toJson() throws JsonProcessingException {
    return toJson(null);
  }

  public String toJson(String sql) throws JsonProcessingException {
    Map<String, String> res = new HashMap<>();
    if (StringUtils.isNoneBlank(username)) {
      res.put("owner", username);
    }
    if (StringUtils.isNoneBlank(defaultPathPrefix)) {
      res.put("defaultPathPrefix", defaultPathPrefix);
    }
    if (StringUtils.isNoneBlank(sql)) {
      res.put("sql", sql);
    }
    res.put("sessionPerUser", "true");
    res.put("includeSchema", "true");
    return mapper.writeValueAsString(res);
  }
}
