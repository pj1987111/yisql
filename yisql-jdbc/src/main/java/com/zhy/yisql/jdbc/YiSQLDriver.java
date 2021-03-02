package com.zhy.yisql.jdbc;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static com.zhy.yisql.jdbc.Config.YiSQL_URL_PREFIX;

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-02-27
 *  \* Time: 10:28
 *  \* Description: 
 *  \
 */
public class YiSQLDriver implements Driver {
  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(YiSQLDriver.class);

  static {
    try {
      DriverManager.registerDriver(new YiSQLDriver());
    } catch (SQLException e) {
      logger.error("Error occurs when registering YiSQL driver", e);
    }
  }

  @Override
  public Connection connect(String url, Properties info) throws SQLException {
    try {
      return acceptsURL(url) ? new YiSQLConnection(url, info) : null;
    } catch (YiSQLException e) {
      throw new SQLException(
          "Connection Error, please check whether the network is available or the server"
              + " has started.");
    }
  }

  @Override
  public boolean acceptsURL(String url) throws SQLException {
    return url.toLowerCase().startsWith(YiSQL_URL_PREFIX);
  }

  @Override
  public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
    return new DriverPropertyInfo[0];
  }

  @Override
  public int getMajorVersion() {
    return Config.MAJOR_VERSION;
  }

  @Override
  public int getMinorVersion() {
    return Config.MINOR_VERSION;
  }

  @Override
  public boolean jdbcCompliant() {
    return false;
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException("getParentLogger");
  }
}
