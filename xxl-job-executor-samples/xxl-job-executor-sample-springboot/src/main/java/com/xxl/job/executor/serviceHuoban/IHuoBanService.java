package com.xxl.job.executor.serviceHuoban;

import cn.hutool.json.JSONObject;
import org.dom4j.Element;

import java.util.List;

/*** 
* @Description: 通过表结构接口创建字段映射 
* @Param:  
* @return:  
* @Author: pendy
* @Date:  
*/
public interface IHuoBanService<T> {
    /**
     * 从伙伴接口获取表结构并创建放到本地缓存中
     * @param jsonObject
     */
    void createFieldsIdMap(JSONObject jsonObject);

    /**
     * 填充本地缓存中的数据表
     * @param paramJson
     */
    String getItemId(JSONObject paramJson);

    /**
     * 从万古接口中读取数据
     * @param xml
     * @return
     */
    List<T> readStringXml(String xml);

    /**
     * 向伙伴系统创建数据
     * @return
     */
    JSONObject insertTable(Element element);

    /**
     * 批量更新伙伴系统数据
     * @param jsonObject
     * @return
     */
    int updateTable(JSONObject jsonObject,Element element);

}
