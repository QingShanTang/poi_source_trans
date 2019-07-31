package org.qingshan.trans.util;

import org.junit.Test;
import org.qingshan.trans.util.excelGenerateSql.DataTable;
import org.qingshan.trans.util.excelGenerateSql.ExcelGenerateSql;

import java.io.IOException;
import java.util.List;

public class TestPoiGenerateSql {
    @Test
    public void TestPoiGenerateSql() throws IOException {
        List<DataTable> tables = ExcelGenerateSql.readXlsx("/Users/qingshan/Desktop/ss/20190731/10/测试_8555663c64b641569049ea176cdc1b29.xlsx");
        ExcelGenerateSql.exportToSQL(tables, "/Users/qingshan/Desktop/xx.sql");
        System.out.println("TableNumber : " + ExcelGenerateSql.getTableNum());
    }
}
