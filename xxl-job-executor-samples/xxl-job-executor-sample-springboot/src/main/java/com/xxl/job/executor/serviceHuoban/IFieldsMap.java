package com.xxl.job.executor.serviceHuoban;

import cn.hutool.json.JSONObject;
import com.xxl.job.executor.Models.Team_Name;

import java.util.List;

/*** 
* @Description: 通过表结构接口创建字段映射 
* @Param:  
* @return:  
* @Author: pendy
* @Date:  
*/
public interface IFieldsMap {
    /**
     * 从伙伴接口获取表结构并创建放到本地缓存中
     * @param jsonObject
     */
    void createFieldsIdMap(JSONObject jsonObject);

    /**
     * 填充本地缓存中的数据表
     * @param paramJson
     */
    void checkOrgIsExists(JSONObject paramJson);

    /**
     * 从万古接口中读取数据
     * @param xml
     * @return
     */
    List<Team_Name> readStringXml(String xml);

}
