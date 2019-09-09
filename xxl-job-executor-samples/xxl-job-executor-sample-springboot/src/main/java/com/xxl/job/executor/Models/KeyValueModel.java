package com.xxl.job.executor.Models;

import lombok.Data;

/**
 * 字段基类
 */
@Data
public class KeyValueModel implements Cloneable {
    String field_id;
    Object field_value;

    @Override
    public Object clone() throws CloneNotSupportedException {
        KeyValueModel valueModel=null;
        valueModel= (KeyValueModel) super.clone();
        return valueModel;
    }
}
