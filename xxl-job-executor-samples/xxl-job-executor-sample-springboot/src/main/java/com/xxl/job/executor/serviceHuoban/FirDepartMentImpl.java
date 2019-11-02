package com.xxl.job.executor.serviceHuoban;

import cn.hutool.core.util.StrUtil;
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
    String companyItemId;

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
    public String getCacheItemId(Element element, Boolean isDelete) {
        getOrgItemsId(element, isDelete);
        fir_depart = (Fir_Depart) tableStuckCache.get("fir_depart");
        JSONObject paramJson = JSONUtil.createObj().put("tableId", HbTablesId.depment)
                .put("field_id", ((Fir_Depart) tableStuckCache.get("fir_depart")).getDepart_code().getField_id())
                .put("field_value", getOrgNodeName(element, "DEPARTMENT"))
                .put("fieldCnName", getOrgNodeName(element, "DEPARTMENT_DESCRIPTION"))
                .put("isDelete", isDelete);
        Map<String, Object> itemFieldAndCnName = getLocalItemId(paramJson, element);
        JSONArray andWhere = getWhereAndJson(element);
        String firDepartMentItemId = getRemoteItem(element, paramJson, itemFieldAndCnName, andWhere, this, false);
        return firDepartMentItemId;
    }

    @Override
    public Map<String, Object> getLocalItemId(JSONObject paramJson, Element element) {
        Object itemId = ((Map<String, Object>) tableStuckCache.get(firDpartItemsId)).get(paramJson.get("field_value"));
        Map<String, Object> itemFieldAndCnName = (Map<String, Object>) itemId;
//        String result;
//        if (itemFieldAndCnName == null || !itemFieldAndCnName.get("fieldCnName").equals(paramJson.get("fieldCnName"))) {
//            try {
//                JSONArray andWhere = JSONArray.class.newInstance().put(JSONUtil.createObj().put("field", paramJson.get("field_id"))
//                        .put("query", JSONUtil.createObj().put("eq", paramJson.get("field_value"))))
//                        .put(JSONUtil.createObj().put("field", fir_depart.getFir_depart().getField_id())
//                                .put("query", JSONUtil.createObj().put("eq", getOrgNodeName(element, "DEPARTMENT_DESCRIPTION"))));
//                paramJson.put("where", JSONUtil.createObj().put("and", andWhere)).remove("field_id");
//            } catch (Exception e) {
//                XxlJobLogger.log(e.getMessage());
//            }
//            result = getItemsIdFromRemote(paramJson, this, element);
//        } else {
//            result = itemFieldAndCnName.get("itemId").toString();
//        }
        return itemFieldAndCnName;
    }

    /**
     * 获取所有父节点的ItemsID
     *
     * @param element
     */
    public void getOrgItemsId(Element element, Boolean isDelete) {
        if (StrUtil.isAllBlank(element.elementText("STAFF_NO"))) {
            companyItemId = new CompanyImpl().getCacheItemId(element, isDelete);
        } else {
            companyItemId = ((Map) ((Map) (tableStuckCache.get(CompanyImpl.companyItemsId))).
                    get(element.elementText("BRANCH"))).get("itemId").toString();
        }
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
                .put(fir_depart.getFir_depart().getField_id(), getOrgNodeName(element, "DEPARTMENT_DESCRIPTION"))
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
    public boolean deleteTable(Element element) {
        boolean res = false;
        if (isEndOrg(element)) {
            getOrgItemsId(element, true);
            fir_depart = (Fir_Depart) tableStuckCache.get("fir_depart");
            JSONObject paramJson = JSONUtil.createObj();
            JSONArray andWhere = getWhereAndJson(element);
            paramJson.put("where", JSONUtil.createObj().put("and", andWhere))
                    .put("isDelete", true);
            System.out.println(String.format("正在删除一级部门节点：{0}", element.elementText("DEPARTMENT")));
            res = deleteJsonObject(paramJson, HbTablesId.depment);
        }
        return res;
    }

    private JSONArray getWhereAndJson(Element element) {
        return JSONUtil.createArray().put(JSONUtil.createObj().put("field", fir_depart.getDepart_code().getField_id())
                .put("query", JSONUtil.createObj().put("eq", JSONUtil.createArray().put(getOrgNodeName(element, "DEPARTMENT")))))
                .put(JSONUtil.createObj().put("field", fir_depart.getCompany_name().getField_id()).put("query", JSONUtil.createObj()
                        .put("eq", JSONUtil.createArray().put(Long.valueOf(companyItemId)))));
    }


    @Override
    public JSONObject updateTable(JSONObject jsonObject, Element element) {
        JSONObject resultJson = JSONUtil.createObj();
        String fieldCnName = jsonObject.getStr("fieldCnName");
        resultJson.put("fieldCnName", fieldCnName);
        //如果是获取人员信息的时候来查询组织就不需要去更新组织，直接返回即可
        if (!StrUtil.isAllBlank(element.elementText("STAFF_NO"))) {
            return resultJson.put("rspStatus", 0);
        }
        String companyItemId = ((Map) ((Map) (tableStuckCache.get(CompanyImpl.companyItemsId))).
                get(element.elementText("BRANCH"))).get("itemId").toString();
        fir_depart = (Fir_Depart) tableStuckCache.get("fir_depart");
        JSONObject dataJson = JSONUtil.createObj().put(fir_depart.getFir_depart().getField_id(), fieldCnName)
                .put(fir_depart.getCompany_name().getField_id(), JSONUtil.createArray().put(companyItemId));
        String itemId = ((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("item_id").toString();
        //如果本节点是最后一个组织节点，更新本组织负责人员
//        if (isEndOrg(element) && !StrUtil.isBlank(element.elementText("Function_Leader"))) {
//            String leaderItemId = getLeaderItemId(element);
//            dataJson.put(fir_depart.getLeaders().getField_id(), JSONUtil.createArray().put(leaderItemId));
//        }
        JSONObject paramJson = JSONUtil.createObj().put("item_ids", itemId).put("filter", JSONUtil.createObj().put("and",
                JSONUtil.createArray().put(JSONUtil.createObj().put("field", fir_depart.getDepart_code().getField_id())
                        .put("query", JSONUtil.createObj().put("eq", jsonObject.getStr("field_code"))))))
                .put("data", dataJson);
        resultJson.put("rspStatus", updateTable(HbTablesId.depment, paramJson));

        return resultJson;
    }

    private boolean isEndOrg(Element element) {
        boolean cityIsNull = "A9999".equals(element.elementText("CITY")) || "".equals(element.elementText("CITY"));
        boolean rankIsNull = "A9999".equals(element.elementText("RANK")) || "".equals(element.elementText("RANK"));
        boolean subsectionIsNull = "A9999".equals(element.elementText("SUB_SECTION")) || "".equals(element.elementText("SUB_SECTION"));
        boolean secDepartIsNull = ("A9999".equals(element.elementText("SECTION")) || "".equals(element.elementText("SECTION")));
        boolean firDepartIsNotNull = !("A9999".equals(element.elementText("DEPARTMENT")) || "".equals(element.elementText("DEPARTMENT")));
        return cityIsNull && rankIsNull && subsectionIsNull && secDepartIsNull && firDepartIsNotNull;
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
