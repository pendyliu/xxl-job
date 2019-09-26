package com.xxl.job.executor.serviceHuoban;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.executor.Models.Fir_Depart;
import com.xxl.job.executor.Models.HbTablesId;
import com.xxl.job.executor.Models.KeyValueModel;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xxl.job.executor.service.jobhandler.PersonHbJobHandler.tableStuckCache;

public class FirDepartMentImpl extends BaseHuoBanServ implements IHuoBanService {

    Fir_Depart fir_depart = new Fir_Depart();
    public static String firDpartItemsId = "firDpartItemsId";

    @Override
    public void createFieldsIdMap() {
        if (tableStuckCache.get("fir_depart") == null) {
            JSONObject jsonObject = getTables(HbTablesId.depment);
            setFildsMap(jsonObject, fir_depart);
            tableStuckCache.put("fir_depart", fir_depart);
        }
        if (tableStuckCache.get(firDpartItemsId) == null) {
            tableStuckCache.put(firDpartItemsId, new HashMap<>());
        }
    }

    @Override
    public String getCacheItemId(Element element) {
        String firDepartMentItemId = getCacheItemsId(JSONUtil.createObj().put("tableId", HbTablesId.depment)
                .put("field_id", ((Fir_Depart) tableStuckCache.get("fir_depart")).getDepart_code().getField_id())
                .put("field_value", getOrgNodeName(element, "DEPARTMENT"))
                .put("fieldCnName",getOrgNodeName(element,"DEPARTMENT_DESCRIPT")), this, element);
        return firDepartMentItemId;
    }

    @Override
    public Map<String, Object> getItemId(JSONObject paramJson, Element element) {
        Object itemId = ((Map<String, Object>) tableStuckCache.get(firDpartItemsId)).get(paramJson.get("field_value"));
        Map<String, Object> itemFieldAndCnName = (Map<String, Object>) itemId;
//        String result;
//        if (itemFieldAndCnName == null || !itemFieldAndCnName.get("fieldCnName").equals(paramJson.get("fieldCnName"))) {
//            try {
//                JSONArray andWhere = JSONArray.class.newInstance().put(JSONUtil.createObj().put("field", paramJson.get("field_id"))
//                        .put("query", JSONUtil.createObj().put("eq", paramJson.get("field_value"))))
//                        .put(JSONUtil.createObj().put("field", fir_depart.getFir_depart().getField_id())
//                                .put("query", JSONUtil.createObj().put("eq", getOrgNodeName(element, "DEPARTMENT_DESCRIPT"))));
//                paramJson.put("where", JSONUtil.createObj().put("and", andWhere)).remove("field_id");
//            } catch (Exception e) {
//                XxlJobLogger.log(e.getMessage());
//            }
//            result = getItemsId(paramJson, this, element);
//        } else {
//            result = itemFieldAndCnName.get("itemId").toString();
//        }
        return itemFieldAndCnName;
    }

    @Override
    public List readStringXml(String xml) {
        return null;
    }

    @Override
    public JSONObject insertTable(Element element) {
        String tableId = HbTablesId.depment;
        fir_depart = (Fir_Depart) tableStuckCache.get("fir_depart");
        JSONObject paramJson = JSONUtil.createObj().put("fields", JSONUtil.createObj().
                put(fir_depart.getCompany_name().getField_id(),
                        JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(CompanyImpl.companyItemsId))).get(element.elementText("BRANCH"))).get("itemId")))
                .put(fir_depart.getDepart_code().getField_id(), getOrgNodeName(element, "DEPARTMENT"))
                .put(fir_depart.getFir_depart().getField_id(), getOrgNodeName(element, "DEPARTMENT_DESCRIPT"))
                .put(fir_depart.getLeaders().getField_id(), element.elementText("")));
        JSONObject reuslt = insertTable(paramJson, tableId);
        return reuslt;
    }

    @Override
    public void saveItemsId(Map itemMap, String field_code) {
        //将以组织编码为Key，Map对象为Value的键值存放到缓存中
        ((Map) tableStuckCache.get(firDpartItemsId)).put(field_code, itemMap);
    }

    @Override
    public JSONObject updateTable(JSONObject jsonObject, Element element) {
        JSONObject resultJson = JSONUtil.createObj();
        fir_depart = (Fir_Depart) tableStuckCache.get("fir_depart");
        List<JSONObject> jsonArray = (List<JSONObject>) ((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("fields");
        String cnName = ((JSONObject) ((JSONArray) jsonArray.stream().filter(p -> p.getStr("field_id").
                equals(fir_depart.getFir_depart().getField_id())).findFirst().get().get("values")).get(0)).get("value").toString();
        String fieldCnName = getOrgNodeName(element, "DEPARTMENT_DESCRIPT");
        resultJson.put("fieldCnName", fieldCnName);
        if (!cnName.equals(fieldCnName)) {
            String itemId = ((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("item_id").toString();
            JSONObject paramJson = JSONUtil.createObj().put("item_ids", itemId).put("filter", JSONUtil.createObj().put("and",
                    JSONUtil.createArray().put(JSONUtil.createObj().put("field", fir_depart.getDepart_code().getField_id())
                            .put("query", JSONUtil.createObj().put("eq", jsonObject.getStr("field_code"))))))
                    .put("data", JSONUtil.createObj().put(fir_depart.getFir_depart().getField_id(), fieldCnName));
            resultJson.put("rspStatus", updateTable(HbTablesId.depment, paramJson));
        }
        return resultJson;
    }

    /**
     * @param jsonObject
     * @param fir_depart
     */
    void setFildsMap(JSONObject jsonObject, Fir_Depart fir_depart) {
        for (int i = 0; i < ((JSONArray) jsonObject.get("field_layout")).size(); i++) {
            for (Object objects : (JSONArray) jsonObject.get("fields")
                    ) {
                JSONArray field_layout = ((JSONArray) jsonObject.get("field_layout"));
                JSONObject field_id = ((JSONObject) objects);
                if (field_id.get("field_id").equals(field_layout.get(i))) {
                    KeyValueModel valueModel = new KeyValueModel();
                    valueModel.setField_id(field_id.get("field_id").toString());
                    switch (field_id.get("name").toString()) {
                        case "公司":
                            fir_depart.setCompany_name(valueModel);
                            break;
                        case "一级部门名称":
                            fir_depart.setFir_depart(valueModel);
                            break;
                        case "一级部门代码":
                            fir_depart.setDepart_code(valueModel);
                            break;
                        case "负责人(关联)":
                            fir_depart.setLeaders(valueModel);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
}
