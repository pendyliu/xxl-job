package com.xxl.job.executor.serviceHuoban;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.xxl.job.executor.Models.Company;
import com.xxl.job.executor.Models.KeyValueModel;
import com.xxl.job.executor.service.jobhandler.PersonHbJobHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * 组织结构公司实现类
 */
public class CompanyImpl extends BaseHuoBanServ implements IFieldsMap<Company> {
    static Company company = new Company();

    @Override
    public void createFieldsIdMap(JSONObject jsonObject) {
        setFildsMap(jsonObject, company);
        /**
         * 将表结构对象存放到缓存当中
         */
        PersonHbJobHandler.tableStuckCache.put("company", company);
    }

    @Override
    public String getItemId(JSONObject paramJson) {
       return getCacheItemsId(paramJson);
    }

    @Override
    public List<Company> readStringXml(String xml) {
        List<Company> companies = new ArrayList<Company>();
        return companies;
    }

    /**
     * 设置公司字段名称与字段ID的映射关系
     *
     * @param jsonObject
     * @param company
     */
    static void setFildsMap(JSONObject jsonObject, Company company) {
        for (int i = 0; i < ((JSONArray) jsonObject.get("field_layout")).size(); i++) {
            for (Object objects : (JSONArray) jsonObject.get("fields")
                    ) {
                JSONArray field_layout = ((JSONArray) jsonObject.get("field_layout"));
                JSONObject field_id = ((JSONObject) objects);
                if (field_id.get("field_id").equals(field_layout.get(i))) {
                    KeyValueModel valueModel = new KeyValueModel();
                    valueModel.setField_id(field_id.get("field_id").toString());
                    switch (field_id.get("name").toString()) {
                        case "公司名称":
                            company.setCompany_name(valueModel);
                            break;
                        case "公司代码":
                            company.setCompany_code(valueModel);
                            break;
                        case "公司负责人(成员)":
                            company.setCompany_leaders(valueModel);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
}
