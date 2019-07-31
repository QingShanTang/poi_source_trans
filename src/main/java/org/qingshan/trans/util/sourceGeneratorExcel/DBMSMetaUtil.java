package org.qingshan.trans.util.sourceGeneratorExcel;

import org.apache.commons.lang3.StringUtils;
import org.qingshan.trans.util.excelGenerateSql.DataTable;

import java.sql.*;
import java.util.*;

public class DBMSMetaUtil {

    /**
     * 数据库类型,枚举
     */
    public static enum DATABASETYPE {
        ORACLE, MYSQL, SQLSERVER, SQLSERVER2005, DB2, INFORMIX, SYBASE, OTHER, EMPTY
    }


    /**
     * 根据IP,端口,以及数据库名字,拼接连接字符串
     *
     * @param info
     * @return
     */
    public static DataSourceInfo concatUrl(DataSourceInfo info) {
        StringBuilder urlBuilder = new StringBuilder();
        if (DATABASETYPE.MYSQL.equals(info.getDbType())) {
            urlBuilder.append("jdbc:mysql://").append(info.getIp()).append(":").append(info.getPort()).append("/").append(info.getDbName());
        }
        info.setUrl(urlBuilder.toString());
        return info;
    }

    /**
     * 获取一个jdbc连接
     *
     * @param info
     * @return
     */
    public static Connection getDBConnection(DataSourceInfo info) {
        Connection conn = null;
        try {
            // 不需要加载Driver. Servlet 2.4规范开始容器会自动载入
            // conn = DriverManager.getConnection(url, username, password);
            Properties properties = new Properties();
            properties.put("user", info.getUsername());
            properties.put("password", info.getPassword());
            properties.put("useInformationSchema", "true");
            conn = DriverManager.getConnection(info.getUrl(), properties);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static List<Map<String, Object>> listTables(Connection conn, DataSourceInfo info) {
        ResultSet rs = null;
        List<Map<String, Object>> result = null;
        try {
            //获取Meta元对象
            DatabaseMetaData meta = conn.getMetaData();
            //对于mysql而言,catalog相当于库名,如果要导出这个数据库中所有库中的表,则为null。如果导出某个具体库中的所有表,则需赋值具体库名。
            String catalog = info.getDbName();
            //对于mysql来说,无意义,为null
            String schemaPattern = null;
            //对应table名,可以使用匹配规则
            String tableNamePattern = null;
            if (StringUtils.isNotBlank(info.getTableName())) {
                tableNamePattern = info.getTableName();
            }
            String[] types = {"TABLE"};
            rs = meta.getTables(catalog, schemaPattern, tableNamePattern, types);

            result = parseResultSetToMapList(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs);
        }
        return result;
    }

    public static List<Map<String, Object>> listColumns(Connection conn, DataSourceInfo info) {
        ResultSet rs = null;
        List<Map<String, Object>> result = null;
        try {
            DatabaseMetaData meta = conn.getMetaData();
            String catalog = info.getDbName();
            String schemaPattern = null;
            String tableNamePattern = info.getTableName();
            String columnNamePattern = null;
            rs = meta.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
            result = parseResultSetToMapList(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs);
        }
        return result;
    }

    public static List<Map<String, Object>> listKeys(Connection conn, DataSourceInfo info) {
        ResultSet rs = null;
        List<Map<String, Object>> result = null;
        try {
            DatabaseMetaData meta = conn.getMetaData();
            String catalog = info.getDbName();
            String schemaPattern = null;
            String tableNamePattern = info.getTableName();
            rs = meta.getPrimaryKeys(catalog, schemaPattern, tableNamePattern);
            result = parseResultSetToMapList(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs);
        }
        return result;
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
                stmt = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将一个未处理的ResultSet解析为Map列表.
     *
     * @param rs
     * @return
     */
    public static List<Map<String, Object>> parseResultSetToMapList(ResultSet rs) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (null == rs) {
            return null;
        }
        try {
            while (rs.next()) {
                Map<String, Object> map = parseResultSetToMap(rs);
                if (null != map) {
                    result.add(map);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 解析ResultSet的单条记录,不进行 ResultSet 的next移动处理
     *
     * @param rs
     * @return
     */
    private static Map<String, Object> parseResultSetToMap(ResultSet rs) {
        if (null == rs) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int colNum = meta.getColumnCount();
            for (int i = 1; i <= colNum; i++) {
                // 列名
                String name = meta.getColumnLabel(i); // i+1
                Object value = rs.getObject(i);
                // 加入属性
                map.put(name, value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
}
