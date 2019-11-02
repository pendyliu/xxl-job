package com.xxl.job.executor.serviceHuoban;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.executor.Models.Company;
import com.xxl.job.executor.Models.HbTablesId;
import com.xxl.job.executor.Models.KeyValueModel;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xxl.job.executor.service.jobhandler.PersonHbJobHandler.tableStuckCache;

/**
 * 组织结构公司实现类
 */
public class CompanyImpl extends BaseHuoBanServ implements IHuoBanService<Company> {
    static Company company = new Company();
    public static String companyItemsId = "companyItemsId";
    public static String tbCacheStruc = "company";

    @Override
    public void createFieldsIdMap() {
        if (tableStuckCache.get(tbCacheStruc) == null) {
            JSONObject jsonObject = getTables(HbTablesId.comany);
            setFildsMap(jsonObject, company);
            /**
             * 将表结构对象存放到缓存当中
             */
            tableStuckCache.put(tbCacheStruc, company);
        }
        if (tableStuckCache.get(companyItemsId) == null) {
            tableStuckCache.put(companyItemsId, new HashMap<>());
        }
    }

    @Override
    public Map<String, Object> getLocalItemId(JSONObject paramJson, Element element) {
        Object itemId = ((Map<String, Object>) tableStuckCache.get(companyItemsId)).get(paramJson.get("field_value"));
        Map<String, Object> itemFieldAndCnName = (Map<String, Object>) itemId;
        return itemFieldAndCnName;
    }

    @Override
    public String getCacheItemId(Element element, Boolean isDelete) {
        JSONObject paramJson = JSONUtil.createObj().put("tableId", HbTablesId.comany).
                put("field_id", ((Company) tableStuckCache.get("company")).getCompany_code().getField_id()).
                put("field_value", element.elementText("BRANCH"))
                .put("fieldCnName", element.elementTextTrim("BRANCH_DESCRIPTION"))
                .put("isDelete", isDelete);
        Map<String, Object> itemFieldAndCnName = getLocalItemId(paramJson, element);
        JSONArray andWhere = JSONUtil.createArray().put(JSONUtil.createObj().put("field", paramJson.get("field_id"))
                .put("query", JSONUtil.createObj().put("eq", paramJson.get("field_value"))));
        String companyItemId = getRemoteItem(element, paramJson, itemFieldAndCnName, andWhere, this, false);
        return companyItemId;
    }

    @Override
    public List<Company> readStringXml(String xml) {
        List<Company> companies = new ArrayList<Company>();
        return companies;
    }

    @Override
    public JSONObject insertTable(Element element) {
        String tableId = HbTablesId.comany;
        Company company = (Company) tableStuckCache.get(tbCacheStruc);
        JSONObject paramJson = JSONUtil.createObj().put("fields", JSONUtil.createObj().
                put(company.getCompany_code().getField_id(), element.elementText("BRANCH"))
                .put(company.getCompany_name().getField_id(), element.elementText("BRANCH_DESCRIPTION"))
                .put(company.getCompany_leaders().getField_id(), element.elementText("")));
        JSONObject reuslt = insertTable(paramJson, tableId);
        return reuslt;
    }

    @Override
    public void saveItemsId(Map itemMap, String field_code) {
        //将以组织编码为Key，Map对象为Value的键值存放到缓存中
        ((Map) tableStuckCache.get(companyItemsId)).put(field_code, itemMap);
    }

    @Override
    public boolean deleteTable(Element element) {
        boolean res = false;
        if (isEndOrg(element)) {
            company = (Company) tableStuckCache.get("company");
            JSONObject paramJson = JSONUtil.createObj();
            JSONArray andWhere = JSONUtil.createArray().put(JSONUtil.createObj().put("field", company.getCompany_code().getField_id())
                    .put("query", JSONUtil.createObj().put("eq", element.elementText("BRANCH"))));
            paramJson.put("where", JSONUtil.createObj().put("and", andWhere));
            paramJson.put("where", JSONUtil.createObj().put("and", andWhere))
                    .put("isDelete", true);
            System.out.println(String.format("正在删除一级部门节点：{0}", element.elementText("DEPARTMENT")));
            res = deleteJsonObject(paramJson, HbTablesId.depment);
        }
        return res;
    }

    private boolean isEndOrg(Element element) {
        boolean cityIsNull = "A9999".equals(element.elementText("CITY")) || "".equals(element.elementText("CITY"));
        boolean rankIsNull = "A9999".equals(element.elementText("RANK")) || "".equals(element.elementText("RANK"));
        boolean subsectionIsNull = "A9999".equals(element.elementText("SUB_SECTION")) || "".equals(element.elementText("SUB_SECTION"));
        boolean secDepartIsNull = ("A9999".equals(element.elementText("SECTION")) || "".equals(element.elementText("SECTION")));
        boolean firDepartIsNotNull = "A9999".equals(element.elementText("DEPARTMENT")) || "".equals(element.elementText("DEPARTMENT"));
        boolean companyIsNotNull = !("A9999".equals(element.elementText("BRANCH")) || "".equals(element.elementText("BRANCH")));
        return cityIsNull && rankIsNull && subsectionIsNull && secDepartIsNull && firDepartIsNotNull && companyIsNotNull;
    }

    @Override
    public JSONObject updateTable(JSONObject jsonObject, Element element) {
        JSONObject resultJson = JSONUtil.createObj();
        String fieldCnName=jsonObject.getStr("fieldCnName");
        resultJson.put("fieldCnName", fieldCnName);
        //如果是获取人员信息的时候来查询组织就不需要去更新组织，直接返回即可
        if (!StrUtil.isAllBlank(element.elementText("STAFF_NO"))){
            return resultJson.put("rspStatus",0);
        }
        String cnName = StrUtil.split(((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("title").toString(), "")[0];
        if (!cnName.equals(fieldCnName)) {
            Company company = (Company) tableStuckCache.get("company");
            String itemId = ((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("item_id").toString();
            JSONObject paramJson = JSONUtil.createObj().put("item_ids", itemId).put("filter", JSONUtil.createObj().put("and",
                    JSONUtil.createArray().put(JSONUtil.createObj().put("field", company.getCompany_code().getField_id())
                            .put("query", JSONUtil.createObj().put("eq", element.elementText("BRANCH"))))))
                    .put("data", JSONUtil.createObj().put(company.getCompany_name().getField_id(), element.elementText("BRANCH_DESCRIPTION")));
            resultJson.put("rspStatus", updateTable(HbTablesId.comany, paramJson));
        }
        return resultJson;
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
