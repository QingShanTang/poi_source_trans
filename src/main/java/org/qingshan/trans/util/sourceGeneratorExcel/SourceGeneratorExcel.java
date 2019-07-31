package org.qingshan.trans.util.sourceGeneratorExcel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.qingshan.trans.util.ExcelUtils;
import org.qingshan.trans.util.excelGenerateSql.DataTable;
import org.qingshan.trans.util.excelGenerateSql.TableField;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SourceGeneratorExcel {

    public static List<DataTable> linkSource(DataSourceInfo info) {
        System.out.println("读取数据库中...");
        List<DataTable> tables = new ArrayList<>();
        Connection conn = DBMSMetaUtil.getDBConnection(DBMSMetaUtil.concatUrl(info));
        List<Map<String, Object>> tableList = DBMSMetaUtil.listTables(conn, info);
        for (int i = 0; i < tableList.size(); i++) {
            DataTable table = new DataTable();
            List<TableField> tableFieldList = new ArrayList<>();
            String tableName = getValue(tableList.get(i), "TABLE_NAME");
            String tableComment = getValue(tableList.get(i), "REMARKS");
            info.setTableName(tableName);
            List<Map<String, Object>> columnList = DBMSMetaUtil.listColumns(conn, info);
            List<Map<String, Object>> keyList = DBMSMetaUtil.listKeys(conn, info);
            List<String> keys = getKeys(keyList);
            for (int j = 0; j < columnList.size(); j++) {
                Map<String, Object> column = columnList.get(j);
                TableField tableField = new TableField();
                tableField = TableField.TableFieldBuilder.aTableField()
                        .setFieldName(getValue(column, "COLUMN_NAME"))
                        .setFieldType(getValue(column, "TYPE_NAME"), getValue(column, "COLUMN_SIZE"), getValue(column, "DECIMAL_DIGITS"))
                        .setNotNull(getValue(column, "IS_NULLABLE"))
                        .setPrimaryKey(keys)
                        .setAutoIncrement(column.get("IS_AUTOINCREMENT").toString())
                        .setDefaultValue(getValue(column, "COLUMN_DEF"))
                        .setComment(column.get("REMARKS").toString())
                        .build();
                tableFieldList.add(tableField);
            }
            table.setTableName(tableName);
            table.setTableComment(tableComment);
            table.setFields(tableFieldList);
            tables.add(table);
        }
        DBMSMetaUtil.close(conn);
        System.out.println("读取完成!");
        return tables;
    }

    public static String exportToExcel(String fileName, String rootFolder, List<DataTable> tableList) {
        System.out.println("导出excel中。。。");
        // 声明一个工作薄
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        for (int i = 0; i < tableList.size(); i++) {
            DataTable table = tableList.get(i);
            String sheetName = table.getTableName();
            String[] headers = {"字段名", "类型", "不是空(Y/N)", "主键(Y/N)", "自增(Y/N)", "默认值", "注释"};
            SXSSFWorkbook wb = ExcelUtils.generateWB(workbook, sheetName, null, table.getTableName(), table.getTableComment(), Arrays.asList(headers), table.getFields(), null);
        }
        String url = ExcelUtils.generateExcelByWB(workbook, fileName, rootFolder);
        System.out.println("导出excel成功,路径是:"+url);
        return url;
    }

    private static List<String> getKeys(List<Map<String, Object>> keyList) {
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < keyList.size(); i++) {
            Map<String, Object> key = keyList.get(i);
            keys.add(key.get("COLUMN_NAME").toString());
        }
        return keys;
    }

    private static String getValue(Map<String, Object> entity, String key) {
        if (null == entity.get(key)) {
            return "";
        } else {
            return entity.get(key).toString().trim();
        }
    }
}
