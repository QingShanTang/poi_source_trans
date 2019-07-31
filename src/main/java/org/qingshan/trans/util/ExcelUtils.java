package org.qingshan.trans.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.qingshan.trans.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelUtils {

    private static Logger Log = LoggerFactory.getLogger(ExcelUtils.class);
    private static DateFormat df = new SimpleDateFormat("yyyyMMdd");
    private static DateFormat excelDf = new SimpleDateFormat("yyyy-MM-dd");

    //---------------------------------------------------------------------------------------------------------
    //导出excel数据
    //---------------------------------------------------------------------------------------------------------

    //根据文件名称生成表格存储路径
    public static String generateFilePath(String fileName, String excelRootFolder) {
        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        String fileSystemName = UUID.randomUUID().toString().replaceAll("-", "");
        if (StringUtils.isNotBlank(fileName)) {
            StringBuilder fileNameBuilder = new StringBuilder(fileName);
            fileSystemName = fileNameBuilder.insert(fileName.indexOf("."), "_" + fileSystemName).toString();
        }
        StringBuffer filePath = new StringBuffer();
        filePath.append(excelRootFolder).append(File.separator).append(df.format(currentDate));
        File _folder = new File(filePath.toString());
        if (!_folder.exists()) {
            _folder.mkdir();
        }
        filePath.append(File.separator).append(c.get(Calendar.HOUR_OF_DAY));
        _folder = new File(filePath.toString());
        if (!_folder.exists()) {
            _folder.mkdir();
        }
        filePath.append(File.separator).append(fileSystemName);
        return filePath.toString();
    }

    //获取HSSFWorkbook对象输出为excel表格,返回存储路径
    public static String generateExcelByWB(SXSSFWorkbook wb, String fileName, String excelRootFolder) {
        String filePath = generateFilePath(fileName, excelRootFolder);
        if (fileName == null) {
            filePath += ".xlsx";
        } else if (!fileName.matches("^.*\\.(?:xls|xlsx)$")) {
            Log.debug("fileName参数错误，非表格文件名！");
        }
        File file = new File(filePath);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            wb.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            Log.debug(e.getMessage());
        } catch (IOException e) {
            Log.debug(e.getMessage());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.debug(e.getMessage());
                }
            }
        }
        return filePath;
    }


    //生成XSSFWorkbook对象
    public static <T> SXSSFWorkbook generateWB(SXSSFWorkbook workbook, String sheetName, String title, String tableName, String tableComment, List<String> headers,
                                               Collection<T> dataset, String pattern) {
        // 生成一个表格
        Sheet sheet = workbook.createSheet(sheetName);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) Constants.DigitalConstant.CONSTANT_15);
        sheet.setDefaultRowHeight((short) Constants.DigitalConstant.CONSTANT_400);

        //生成表头样式
        CellStyle titleStyle = workbook.createCellStyle();
        //水平居中
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //垂直居中
        titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        // 生成标题行样式
        CellStyle style = workbook.createCellStyle();
        // 设置这些样式
        //先设置填充色
        style.setFillForegroundColor(HSSFColor.LIME.index);
        //设置填充图案
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        // 生成一个字体
        Font font = workbook.createFont();
        //设置字体颜色
        font.setColor(HSSFColor.WHITE.index);
        //设置字体高度
        font.setFontHeightInPoints((short) Constants.DigitalConstant.CONSTANT_12);
        //设置粗体显示
        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);

        // 把字体应用到当前的样式
        style.setFont(font);

        // 生成并设置数据行样式
        CellStyle style2 = workbook.createCellStyle();
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        // 生成并设置数据行样式
        CellStyle style3 = workbook.createCellStyle();
        style2.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        // 生成日期单元格样式
        CellStyle styleDate = workbook.createCellStyle();
        styleDate.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleDate.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        DataFormat format = workbook.createDataFormat();
        styleDate.setDataFormat(format.getFormat("@"));

        // 生成另一个字体
        Font font2 = workbook.createFont();
        //设置字体颜色
        font.setColor(HSSFColor.BLACK.index);
        //设置字体高度
        font.setFontHeightInPoints((short) Constants.DigitalConstant.CONSTANT_10);
        font2.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);

        // 把字体应用到当前的样式
        style2.setFont(font2);

        // 声明一个画图的顶级管理器
        // HSSFPatriarch patriarch = sheet.createDrawingPatriarch();

        //数据起始行
        int dataIndex = 0;
        if (StringUtils.isNotBlank(title)) {
            //产生表头
            Row titleRow = sheet.createRow(0);
            Cell titleRowCell = titleRow.createCell(0);
            titleRowCell.setCellStyle(titleStyle);
            XSSFRichTextString titleText = new XSSFRichTextString(title);
            titleRowCell.setCellValue(titleText);
            CellRangeAddress region = new CellRangeAddress(0, 0, 0, headers.size() - 1);
            sheet.addMergedRegion(region);
            dataIndex++;
        }

        if (StringUtils.isNotBlank(tableName) || StringUtils.isNotBlank(tableComment)) {
            Row titleRow = sheet.createRow(0);
            Cell tableNameCell = titleRow.createCell(0);
            tableNameCell.setCellStyle(style);
            tableNameCell.setCellValue(tableName);
            Cell tableCommentCell = titleRow.createCell(1);
            tableCommentCell.setCellStyle(style);
            tableCommentCell.setCellValue(tableComment);
            CellRangeAddress region = new CellRangeAddress(0, 0, 1, headers.size() - 1);
            sheet.addMergedRegion(region);
            dataIndex++;
        }

        if (headers != null && headers.size() != 0) {
            // 产生表格标题行
            Row row = sheet.createRow(dataIndex);
            for (short i = 0; i < headers.size(); i++) {
                Cell cell = row.createCell(i);
                cell.setCellStyle(style);
                XSSFRichTextString text = new XSSFRichTextString(headers.get(i));
                cell.setCellValue(text);
            }
            dataIndex++;
        }


        // 遍历集合数据，产生数据行
        Iterator<T> it = dataset.iterator();
        while (it.hasNext()) {
            Row row = sheet.createRow(dataIndex);
            T t = (T) it.next();
            // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
            Field[] fields = t.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellStyle(style2);

                Field field = fields[i];
                String fieldName = field.getName();
                String getMethodName = "get"
                        + fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1);
                try {
                    Class tCls = t.getClass();
                    Method getMethod = tCls.getMethod(getMethodName,
                            new Class[]
                                    {});
                    Object value = getMethod.invoke(t, new Object[]
                            {});
                    // 判断值的类型后进行强制类型转换
                    String textValue = null;
                    if (value == null) {
                        textValue = "";
                    } else if (value instanceof Integer) {
                        int intValue = (Integer) value;
                        cell.setCellValue(intValue);
                    } else if (value instanceof Long) {
                        long longValue = (Long) value;
                        cell.setCellValue(longValue);
                    } else if (value instanceof Float) {
                        float fValue = (Float) value;
                        cell.setCellValue(fValue);
                    } else if (value instanceof Double) {
                        double dValue = (Double) value;
                        cell.setCellValue(dValue);
                    } else if (value instanceof Date) {
                        Date date = (Date) value;
                        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                        cell.setCellStyle(styleDate);
                        cell.setCellValue(sdf.format(date));
                    } else {

                        // 其它数据类型都当作字符串简单处理
                        textValue = value.toString();
                        textValue = textValue.replace("<br/>", "");
                    }
                    // 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
                    if (textValue != null) {
                        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
                        Matcher matcher = p.matcher(textValue);
                        if (matcher.matches()) {
                            // 是数字当作double处理
                            cell.setCellValue(Double.parseDouble(textValue));
                        } else {
                            XSSFRichTextString richString = new XSSFRichTextString(textValue);
                            cell.setCellValue(richString);
                        }
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            dataIndex++;
        }
        return workbook;

    }


    //---------------------------------------------------------------------------------------------------------
    //导入excel数据
    //---------------------------------------------------------------------------------------------------------

    //解析excel反射生成对象
    //装载流
    public static List<? extends Object> importMappingExcel(InputStream inputStream, Integer sheetIndex, Integer dataStartLine, Class objClazz) {
        List<Object> objList = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
            for (int i = dataStartLine; i <= sheet.getLastRowNum(); i++) {
                Object obj = objClazz.newInstance();
                XSSFRow row = sheet.getRow(i);
                if (null != row) {
                    obj = dataObj(obj, row);
                    objList.add(obj);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return objList;
    }

    //拼装单个obj
    private static Object dataObj(Object obj, XSSFRow row) {
        try {
            Class objClazz = obj.getClass();
            Field[] fields = objClazz.getDeclaredFields();
            if (fields == null || fields.length < 1) {
                return null;
            }
            for (int i = 0; i < fields.length; i++) {
                if (!"serialVersionUID".equals(fields[i].getName())) {
                    Object fieldvalue = getCellValue(row.getCell(i));
                    PropertyDescriptor pd = new PropertyDescriptor(fields[i].getName(), objClazz);
                    Class fieldClazz = pd.getPropertyType();
                    Method setMethod = pd.getWriteMethod();
                    if ("java.lang.String".equals(fieldClazz.getName())) {
                        setMethod.invoke(obj, fieldvalue.toString());
                    } else if ("java.lang.Integer".equals(fieldClazz.getName())) {
                        setMethod.invoke(obj, Integer.parseInt(fieldvalue.toString()));
                    } else if ("java.math.BigDecimal".equals(fieldClazz.getName())) {
                        setMethod.invoke(obj, new BigDecimal(fieldvalue.toString()));
                    } else if ("java.util.Date".equals(fieldClazz.getName())) {
                        setMethod.invoke(obj, excelDf.parse(fieldvalue.toString()));
                    }
                }
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return obj;
    }

    //获取excel cell中的值
    private static Object getCellValue(XSSFCell cell) {
        Object val = "";
        try {
            if (null != cell) {
                if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
                    val = cell.getStringCellValue();
                } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    val = (new BigDecimal(cell.getNumericCellValue())).toString();
                }
            }
        } catch (Exception e) {
            return val;
        }
        return val;
    }
}
