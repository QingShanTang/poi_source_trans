package org.qingshan.trans.util;

import org.junit.Before;
import org.junit.Test;
import org.qingshan.trans.util.excelGenerateSql.DataTable;
import org.qingshan.trans.util.sourceGeneratorExcel.DBMSMetaUtil;
import org.qingshan.trans.util.sourceGeneratorExcel.DataSourceInfo;
import org.qingshan.trans.util.sourceGeneratorExcel.SourceGeneratorExcel;

import java.sql.Connection;
import java.util.List;

public class TestSourceGeneratorExcel {
    private static DataSourceInfo info = new DataSourceInfo(
            DBMSMetaUtil.DATABASETYPE.MYSQL,//目前系统只支持mysql
            "127.0.0.1",
            "3306",
            "decathlon",
            "root",
            "123456",
            ""//选填:如果需导出库中所有table,不填
    );
    private static Connection conn;

    @Test
    public void TestSourceGeneratorExcel() {
        //先配置info
        List<DataTable> tableList = SourceGeneratorExcel.linkSource(info);
        String url = SourceGeneratorExcel.exportToExcel("测试.xlsx", "/Users/qingshan/Desktop/ss", tableList);
    }
}
