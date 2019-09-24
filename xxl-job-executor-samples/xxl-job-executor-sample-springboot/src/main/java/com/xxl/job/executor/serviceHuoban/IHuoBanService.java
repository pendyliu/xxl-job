package com.xxl.job.executor.serviceHuoban;

import cn.hutool.json.JSONObject;
import org.dom4j.Element;

import java.util.List;
import java.util.Map;

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
     * @param
     */
    void createFieldsIdMap();

    /**
     * 填充本地缓存中的数据表
     * @param paramJson
     */
    Map getItemId(JSONObject paramJson,Element element);

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
    JSONObject updateTable(JSONObject jsonObject,Element element);

    /**
     * 因为每个组织节点在每个表中是不一样的，所以每个节点都来实现自己保存到本地缓存中
     * @param itemMap
     * @param field_code 组织节点的编码 对应他的 itemId 和 组织节点的中文名称
     */
    void saveItemsId(Map itemMap,String field_code);

}
