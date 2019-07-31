package org.qingshan.trans.util.excelGenerateSql;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * 表字段
 */
public class TableField {

    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 字段类型
     */
    private String fieldType;
    /**
     * 是否为空
     */
    private String notNull;
    /**
     * 主键
     */
    private String primaryKey;
    /**
     * 自增
     */
    private String autoIncrement;
    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 注释
     */
    private String comment;

    public TableField() {
    }

    public TableField(String fieldName, String fieldType, String notNull, String primaryKey, String autoIncrement, String defaultValue, String comment) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.notNull = notNull;
        this.primaryKey = primaryKey;
        this.autoIncrement = autoIncrement;
        this.defaultValue = defaultValue;
        this.comment = comment;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getNotNull() {
        return notNull;
    }

    public void setNotNull(String notNull) {
        this.notNull = notNull;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(String autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public static final class TableFieldBuilder {
        private String fieldName;
        private String fieldType;
        private String notNull;
        private String primaryKey;
        private String autoIncrement;
        private String defaultValue;
        private String comment;

        private TableFieldBuilder() {
        }

        public static TableFieldBuilder aTableField() {
            return new TableFieldBuilder();
        }

        public TableFieldBuilder setFieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public TableFieldBuilder setFieldType(String typeName, String columnSize, String decimalDigits) {
            //不知为何,datetime类型数据库长度是0但是返回是26
            if ("26".equals(columnSize)){
                columnSize = "6";
            }
            if (StringUtils.isBlank(decimalDigits)) {
                this.fieldType = typeName.toLowerCase() + "(" + columnSize + ")";
            } else {
                this.fieldType = typeName.toLowerCase() + "(" + columnSize + "," + decimalDigits + ")";
            }
            return this;
        }

        public TableFieldBuilder setNotNull(String isNullable) {
            if ("YES".equals(isNullable)) {
                this.notNull = "N";
            } else {
                this.notNull = "Y";
            }
            return this;
        }

        public TableFieldBuilder setPrimaryKey(List<String> keys) {
            if (keys.contains(fieldName)) {
                this.primaryKey = "Y";
            } else {
                this.primaryKey = "N";
            }
            return this;
        }

        public TableFieldBuilder setAutoIncrement(String autoIncrement) {
            if ("YES".equals(autoIncrement)) {
                this.autoIncrement = "Y";
            } else {
                this.autoIncrement = "N";
            }
            return this;
        }

        public TableFieldBuilder setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public TableFieldBuilder setComment(String comment) {
            this.comment = comment;
            return this;
        }


        public TableField build() {
            TableField tableField = new TableField();
            tableField.setFieldName(fieldName);
            tableField.setFieldType(fieldType);
            tableField.setNotNull(notNull);
            tableField.setPrimaryKey(primaryKey);
            tableField.setAutoIncrement(autoIncrement);
            tableField.setDefaultValue(defaultValue);
            tableField.setComment(comment);
            return tableField;
        }
    }
}