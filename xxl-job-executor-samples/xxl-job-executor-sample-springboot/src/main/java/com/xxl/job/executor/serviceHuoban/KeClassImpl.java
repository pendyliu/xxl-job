package com.xxl.job.executor.serviceHuoban;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.executor.Models.HbTablesId;
import com.xxl.job.executor.Models.KeClass;
import com.xxl.job.executor.Models.KeyValueModel;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xxl.job.executor.service.jobhandler.PersonHbJobHandler.tableStuckCache;

public class KeClassImpl extends BaseHuoBanServ implements IHuoBanService {
    KeClass keClass = new KeClass();
    public static String keClassItemsId = "keClassItemsId";
    String keClassStruc = "keClass";

    @Override
    public void createFieldsIdMap() {
        if (tableStuckCache.get("keClass") == null) {
            JSONObject tableStrucResult = getTables(HbTablesId.sub_secdepart);
            setFildsMap(tableStrucResult, keClass);
            tableStuckCache.put("keClass", keClass);
        }
        if (tableStuckCache.get(keClassItemsId) == null) {
            tableStuckCache.put(keClassItemsId, new HashMap<>());
        }
    }

    @Override
    public void saveItemsId(Map itemMap, String field_code) {
        //将以组织编码为Key，Map对象为Value的键值存放到缓存中
        ((Map) tableStuckCache.get(keClassItemsId)).put(field_code, itemMap);
    }

    @Override
    public String getCacheItemId(Element element) {
        String sub_sectionItemId = getCacheItemsId(JSONUtil.createObj().put("tableId", HbTablesId.sub_secdepart)
                        .put("field_id", ((KeClass) tableStuckCache.get("keClass")).getClass_code().getField_id())
                        .put("field_value", getOrgNodeName(element, "SUB_SECTION"))
                        .put("fieldCnName", getOrgNodeName(element, "SUB_DESCRIPTION")),
                this, element, false);
        return sub_sectionItemId;
    }

    @Override
    public Map<String, Object> getItemId(JSONObject paramJson, Element element) {
        Object itemId = ((Map<String, Object>) tableStuckCache.get(keClassItemsId)).get(paramJson.get("field_value"));
        Map<String, Object> itemFieldAndCnName = (Map<String, Object>) itemId;
//        String result;
//        keClass = (KeClass) tableStuckCache.get("keClass");
//        if (itemFieldAndCnName == null || !itemFieldAndCnName.get("fieldCnName").equals(paramJson.get("fieldCnName"))) {
//            try {
//                JSONArray andWhere = JSONArray.class.newInstance().put(JSONUtil.createObj().put("field", paramJson.get("field_id"))
//                        .put("query", JSONUtil.createObj().put("eq", paramJson.get("field_value"))))
//                        .put(JSONUtil.createObj().put("field", keClass.getClass_name().getField_id())
//                                .put("query", JSONUtil.createObj().put("eq", getOrgNodeName(element, "SUB_DESCRIPTION"))));
//                paramJson.put("where", JSONUtil.createObj().put("and", andWhere)).remove("field_id");
//            } catch (Exception e) {
//                XxlJobLogger.log(e.getMessage());
//            }
//            result = getItemsId(paramJson, this, element);
//        } else {
//            result = itemFieldAndCnName.get(keClassItemsId).toString();
//        }
        return itemFieldAndCnName;
    }

    @Override
    public List readStringXml(String xml) {
        return null;
    }

    @Override
    public JSONObject insertTable(Element element) {
        String tableId = HbTablesId.sub_secdepart;
        String firDepartCode = getOrgNodeName(element, "DEPARTMENT");
        String sec_depart_code = getOrgNodeName(element, "SECTION");
        keClass = (KeClass) tableStuckCache.get("keClass");
        JSONObject paramJson = JSONUtil.createObj().put("fields", JSONUtil.createObj().
                put(keClass.getCompany_name().getField_id(),
                        JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(CompanyImpl.companyItemsId))).get(element.elementText("BRANCH"))).get("itemId")))
                .put(keClass.getFir_depart().getField_id(), JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(FirDepartMentImpl.firDpartItemsId))).get(firDepartCode)).get("itemId")))
                .put(keClass.getSec_depart().getField_id(), JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(Sec_DepartImpl.secDepartItemsId))).get(sec_depart_code)).get("itemId")))
                .put(keClass.getClass_name().getField_id(), getOrgNodeName(element, "SUB_DESCRIPTION"))
                .put(keClass.getClass_code().getField_id(), getOrgNodeName(element, "SUB_SECTION"))
                .put(keClass.getLeaders().getField_id(), element.elementText("")));
        JSONObject reuslt = insertTable(paramJson, tableId);
        return reuslt;
    }

    @Override
    public JSONObject updateTable(JSONObject jsonObject, Element element) {
        JSONObject resultJson = JSONUtil.createObj();
        keClass = (KeClass) tableStuckCache.get("keClass");
        String companyItemId = ((Map) ((Map) (tableStuckCache.get(CompanyImpl.companyItemsId))).
                get(element.elementText("BRANCH"))).get("itemId").toString();
        String secDepartItemId = ((Map) ((Map) (tableStuckCache.get(Sec_DepartImpl.secDepartItemsId))).
                get(getOrgNodeName(element, "SECTION"))).get("itemId").toString();
        String firDepartItemId = ((Map) ((Map) (tableStuckCache.get(FirDepartMentImpl.firDpartItemsId))).
                get(getOrgNodeName(element, "DEPARTMENT"))).get("itemId").toString();
//        List<JSONObject> jsonArray = (List<JSONObject>) ((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("fields");
//        String cnName = ((JSONObject) ((JSONArray) jsonArray.stream().filter(p -> p.getStr("field_id").
//                equals(keClass.getClass_name().getField_id())).findFirst().get().get("values")).get(0)).get("value").toString();
        String fieldCnName = getOrgNodeName(element, "SUB_DESCRIPTION");
        resultJson.put("fieldCnName", fieldCnName);

        String itemId = ((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("item_id").toString();
        JSONObject dataJson = JSONUtil.createObj().put(keClass.getClass_name().getField_id(), fieldCnName)
                .put(keClass.getCompany_name().getField_id(), JSONUtil.createArray().put(companyItemId))
                .put(keClass.getFir_depart().getField_id(), JSONUtil.createArray().put(firDepartItemId))
                .put(keClass.getSec_depart().getField_id(), JSONUtil.createArray().put(secDepartItemId));
        //如果本节点是最后一个组织节点，更新本组织负责人员
        if (isEndOrg(element) && !StrUtil.isBlank(element.elementText("Function_Leader"))) {
            String leaderItemId = getLeaderItemId(element);
            dataJson.put(keClass.getLeaders().getField_id(), JSONUtil.createArray().put(leaderItemId));
        }
        JSONObject paramJson = JSONUtil.createObj().put("item_ids", itemId).put("filter", JSONUtil.createObj().put("and",
                JSONUtil.createArray().put(JSONUtil.createObj().put("field", keClass.getClass_code().getField_id())
                        .put("query", JSONUtil.createObj().put("eq", jsonObject.getStr("field_code"))))))
                .put("data", dataJson);
        //更新数据
        resultJson.put("rspStatus", updateTable(HbTablesId.sub_secdepart, paramJson));

        return resultJson;
    }

    private boolean isEndOrg(Element element) {
        boolean cityIsNull = "A9999".equals(element.elementText("CITY")) || "".equals(element.elementText("CITY"));
        boolean rankIsNull = "A9999".equals(element.elementText("RANK")) || "".equals(element.elementText("RANK"));
        boolean subsectionIsNotNull = !("A9999".equals(element.elementText("SUB_SECTION")) || "".equals(element.elementText("SUB_SECTION")));
        return cityIsNull && rankIsNull && subsectionIsNotNull;
    }

    /**
     * 从伙伴接口获取课表结构
     *
     * @param tableStrucResult
     * @param keClass
     */
    void setFildsMap(JSONObject tableStrucResult, KeClass keClass) {
        for (Object objects : (JSONArray) tableStrucResult.get("fields")
                ) {
            JSONObject field_id = ((JSONObject) objects);
            KeyValueModel valueModel = new KeyValueModel();
            valueModel.setField_id(field_id.get("field_id").toString());
            switch (field_id.get("alias").toString()) {
                case "class.company_name":
                    keClass.setCompany_name(valueModel);
                    break;
                case "class.fir_depart":
                    keClass.setFir_depart(valueModel);
                    break;
                case "class.sec_depart":
                    keClass.setSec_depart(valueModel);
                    break;
                case "class.class_name":
                    keClass.setClass_name(valueModel);
                    break;
                case "class.class_code":
                    keClass.setClass_code(valueModel);
                    break;
                case "class.leaders":
                    keClass.setLeaders(valueModel);
                    break;
                default:
                    break;
            }
        }
    }
}
