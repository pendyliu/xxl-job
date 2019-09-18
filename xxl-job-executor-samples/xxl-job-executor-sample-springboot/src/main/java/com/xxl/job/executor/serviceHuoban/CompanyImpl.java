package com.xxl.job.executor.serviceHuoban;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.executor.Models.Company;
import com.xxl.job.executor.Models.HbTablesId;
import com.xxl.job.executor.Models.KeyValueModel;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

import static com.xxl.job.executor.service.jobhandler.PersonHbJobHandler.tableStuckCache;


/**
 * 组织结构公司实现类
 */
public class CompanyImpl extends BaseHuoBanServ implements IHuoBanService<Company> {
    static Company company = new Company();

    @Override
    public void createFieldsIdMap() {
        if (tableStuckCache.get("company") == null) {
            JSONObject jsonObject = getTables(HbTablesId.comany);
            setFildsMap(jsonObject, company);
            /**
             * 将表结构对象存放到缓存当中
             */
            tableStuckCache.put("company", company);
        }
    }

    @Override
    public String getItemId(JSONObject paramJson) {
        return null;
    }

    @Override
    public List<Company> readStringXml(String xml) {
        List<Company> companies = new ArrayList<Company>();
        return companies;
    }

    @Override
    public JSONObject insertTable(Element element) {
        String tableId = HbTablesId.comany;
        Company company = (Company) tableStuckCache.get("company");
        JSONObject paramJson = JSONUtil.createObj().put("fields", JSONUtil.createObj().
                put(company.getCompany_code().getField_id(), element.elementText("BRANCH"))
                .put(company.getCompany_name().getField_id(), element.elementText("BRANCH_DESCRIPTION"))
                .put(company.getCompany_leaders().getField_id(), element.elementText("")));
        JSONObject reuslt = insertTable(paramJson, tableId);
        return reuslt;
    }

    @Override
    public int updateTable(JSONObject jsonObject, Element element) {
        Company company = (Company) tableStuckCache.get("company");
        String itemId = ((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("item_id").toString();
        JSONObject paramJson = JSONUtil.createObj().put("item_ids", itemId).put("filter", JSONUtil.createObj().put("and",
                JSONUtil.createArray().put(JSONUtil.createObj().put("field", company.getCompany_code().getField_id())
                        .put("query", JSONUtil.createObj().put("eq", element.elementText("BRANCH"))))))
                .put("data", JSONUtil.createObj().put(company.getCompany_name().getField_id(), element.elementText("BRANCH_DESCRIPTION")));
        return updateTable(HbTablesId.comany, paramJson);
    }

    /**
     * 设置公司字段名称与字段ID的映射关系
     *
     * @param jsonObject
     * @param company
     */
    void setFildsMap(JSONObject jsonObject, Company company) {
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
