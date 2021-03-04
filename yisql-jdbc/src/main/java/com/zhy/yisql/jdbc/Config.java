package com.zhy.yisql.jdbc;

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-02-27
 *  \* Time: 10:47
 *  \* Description: 
 *  \
 */
public class Config {
  public static final String METHOD_NOT_SUPPORTED_STRING = "Method not supported";

  public final static String YiSQL_URL_PREFIX = "jdbc:yisql://";
  public static final String JDBC_DRIVER_NAME = "com.zhy.yisql.jdbc.YiSQLDriver";
  public static int MAX_SIZE = 1024;
  public static int MAJOR_VERSION = 1;
  public static int MINOR_VERSION = 0;

  public static String DRIVER_NAME = "YISQL";
  public static final String YISQL_DEFAULT_HOST = "localhost";
  public static final String YISQL_DEFAULT_PORT = "6049";
  public static final String DEFAULT_USER = "admin";

  public static final String HOST = "host";
  public static final String PORT = "port";
  public static final String PATH = "path";

  public static final String AUTH_USER = "user";
  public static final String DEFAULT_PATH_PREFIX = "defaultPathPrefix";
  public static final String TIME_ZONE = "timeZone";
  public static final String DEFAULT_TIME_ZONE = "UTC";
  public static final String TIMESTAMP_FORMAT = "timestampFormat";
  public static final String DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
  public static final String DATE_FORMAT = "dateFormat";
  public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
}
