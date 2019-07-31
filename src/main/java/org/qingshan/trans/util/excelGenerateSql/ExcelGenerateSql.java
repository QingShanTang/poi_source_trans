package org.qingshan.trans.util.excelGenerateSql;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExcelGenerateSql {
    private static int tableNum = 0;
    private static List<Integer> ignoreLines = Arrays.asList(1);

    public static List<DataTable> readXlsx(String path) {
        List<DataTable> list = new ArrayList<>();
        System.out.println("读取...");
        System.out.println();
        InputStream in = null;
        try {
            in = new FileInputStream(path);
            XSSFWorkbook wb = new XSSFWorkbook(in);

            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                XSSFSheet sheet = wb.getSheetAt(i);
                if (sheet == null)
                    continue;

                for (int j = 0; j <= sheet.getLastRowNum(); j++) {
                    XSSFRow row = sheet.getRow(j);
                    if (row != null) {
                        //如果此行忽略,则跳过
                        if (ignoreLines.contains(j)) {
                            continue;
                        }
                        /*
                            CellTypeEnum       类型        值
                             NUMERIC           数值型       0
                             STRING            字符串型     1
                             FORMULA           公式型       2
                             BLANK             空值         3
                             BOOLEAN           布尔型       4
                             ERROR             错误         5
                         */

                        //如果是首行,则读取关于表名相关消息
                        if (j == 0) {
                            ++tableNum;

                            String name = getStringCellValue(row, 0).toLowerCase();
                            String comment = getStringCellValue(row, 1);

                            list.add(new DataTable(name, comment));
                            System.out.println("----------------------Table-------------------");
                            System.out.println("表名：" + name + ",注释：" + comment);
                            continue;
                        }

                        list.get(list.size() - 1).getFields().add(new TableField(
                                getStringCellValue(row, 0),
                                getStringCellValue(row, 1),
                                getStringCellValue(row, 2),
                                getStringCellValue(row, 3),
                                getStringCellValue(row, 4),
                                getStringCellValue(row, 5),
                                getStringCellValue(row, 6)
                        ));

                    }

                }

            }
            in.close();
            System.out.println();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    public static void exportToSQL(List<DataTable> list, String sqlPath) throws IOException {
        final List<String> sql = new ArrayList<>();
        for (DataTable e : list
                ) {
            List<String> keys = new ArrayList<>();
            StringBuilder sb = new StringBuilder("");
            sb.append(Keys.TABLE_PRE + "`" + e.getTableName() + "`;\n");
            sb.append(Keys.C_T + "`" + e.getTableName() + "` (\n");
            List<TableField> fieldList = e.getFields();
            Integer countField = 0;
            for (TableField c : fieldList
                    ) {
                if (StringUtils.isBlank(c.getFieldName())) {
                    continue;
                } else {
                    if (countField != 0) {
                        sb.append(",\n");
                    }
                }
                countField++;
                String fieldNameInfo = "";
                String fieldTypeInfo = "";
                String notNullInfo = "";
                String autoIncrement = "";
                String defaultValueInfo = "";
                String commentInfo = "";
                fieldNameInfo = "`" + c.getFieldName().toLowerCase() + "`";
                fieldTypeInfo = c.getFieldType().toLowerCase();
                //判断是否为空
                if ("Y".equals(c.getNotNull())) {
                    notNullInfo = Keys.N_N;
                } else {
                    notNullInfo = Keys.D_N;
                }

                //判断是否是主键
                if ("Y".equals(c.getPrimaryKey())) {
                    keys.add("`" + c.getFieldName() + "`");
                }

                if ("Y".equals(c.getAutoIncrement())) {
                    autoIncrement = Keys.A_I;
                }
                //判断是否是有默认值
                if (StringUtils.isNotBlank(c.getDefaultValue())) {
                    defaultValueInfo = Keys.DEFAULT + "'" + c.getDefaultValue() + "'";
                }

                //判断是否有注释
                if (StringUtils.isNotBlank(c.getComment())) {
                    commentInfo = Keys.COMMENT + "'" + c.getComment() + "'";
                }

                sb.append(fieldNameInfo + " " + fieldTypeInfo + notNullInfo + autoIncrement + defaultValueInfo + commentInfo);
            }
            if (keys.size() > 0) {
                sb.append(",\n" + Keys.P_K + "(" + StringUtils.join(keys, ",") + ")\n" +
                        ") " + Keys.TABLE_SUFF + "'" + e.getTableComment() + "';\n\n");
            } else {
                sb.append("\n" +
                        ") " + Keys.TABLE_SUFF + "'" + e.getTableComment() + "';\n\n");
            }

            System.out.println(sb.toString());
            sql.add(sb.toString());

        }

        File file = new File(sqlPath);
        FileOutputStream out = new FileOutputStream(file);
        sql.forEach(s -> {
            try {
                out.write(s.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        out.close();
    }

    public static int getTableNum() {
        return tableNum;
    }

    private static XSSFCell getCellPreventNull(XSSFRow row, Integer cellnum) {
        if (row.getCell(cellnum) == null) {
            row.createCell(cellnum).setCellValue("");
        }
        return row.getCell(cellnum);
    }

    private static String getStringCellValue(XSSFRow row, Integer cellnum) {
        return getCellPreventNull(row, cellnum).toString().trim();
    }
}
