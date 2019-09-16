package com.xxl.job.executor.Models;

import lombok.Data;

/**
 * 字段基类
 */
@Data
public class KeyValueModel implements Cloneable {
    /**
     * 字段ID
     */
    String field_id;
    /**
     * 字段值
     */
    Object field_value;
    /**
     * 关联ID
     */
    Object item_id;

    @Override
    public Object clone() throws CloneNotSupportedException {
        KeyValueModel valueModel=null;
        valueModel= (KeyValueModel) super.clone();
        return valueModel;
    }
}
