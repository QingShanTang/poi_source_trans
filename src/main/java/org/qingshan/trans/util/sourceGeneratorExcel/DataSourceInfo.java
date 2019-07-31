package org.qingshan.trans.util.sourceGeneratorExcel;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.sql.DataSource;

public class DataSourceInfo {


    /**
     * 数据库类型
     */
    private DBMSMetaUtil.DATABASETYPE dbType;
    private String ip;
    private String port;
    private String dbName;
    private String username;
    private String password;
    private String tableName;
    private String url;

    public DataSourceInfo() {
    }

    public DataSourceInfo(DBMSMetaUtil.DATABASETYPE dbType, String ip, String port, String dbName, String username, String password, String tableName) {
        this.dbType = dbType;
        this.ip = ip;
        this.port = port;
        this.dbName = dbName;
        this.username = username;
        this.password = password;
        this.tableName = tableName;
    }


    public DBMSMetaUtil.DATABASETYPE getDbType() {
        return dbType;
    }

    public void setDbType(DBMSMetaUtil.DATABASETYPE dbType) {
        this.dbType = dbType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
