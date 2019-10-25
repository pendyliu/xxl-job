package com.xxl.job.executor.serviceHuoban;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.Models.HbTablesId;
import com.xxl.job.executor.Models.KeClass;
import com.xxl.job.executor.Models.KeyValueModel;
import com.xxl.job.executor.Models.Sec_Depart;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xxl.job.executor.service.jobhandler.PersonHbJobHandler.tableStuckCache;

public class Sec_DepartImpl extends BaseHuoBanServ implements IHuoBanService {
    Sec_Depart sec_depart = new Sec_Depart();
    public static String secDepartItemsId = "secDepartItemsId";
    String companyItemId;
    String firDepartItemId;

    @Override
    public void createFieldsIdMap() {
        if (tableStuckCache.get("sec_depart") == null) {
            JSONObject tableStrucResult = getTables(HbTablesId.sec_depart);
            setFildsMap(tableStrucResult, sec_depart);
            tableStuckCache.put("sec_depart", sec_depart);
        }
        if (tableStuckCache.get(secDepartItemsId) == null) {
            tableStuckCache.put(secDepartItemsId, new HashMap<>());
        }
    }

    @Override
    public String getCacheItemId(Element element) {
        String sectionItemId = getCacheItemsId(JSONUtil.createObj().put("tableId", HbTablesId.sec_depart)
                        .put("field_id", ((Sec_Depart) tableStuckCache.get("sec_depart")).getDepart_code().getField_id())
                        .put("field_value", getOrgNodeName(element, "SECTION"))
                        .put("fieldCnName", getOrgNodeName(element, "SECTION_DESCRIPTION")),
                this, element, false);
        return sectionItemId;
    }


    /**
     * 获取所有父节点的ItemsID
     *
     * @param element
     */
    public void getOrgItemsId(Element element) {
        companyItemId = ((Map) ((Map) (tableStuckCache.get(CompanyImpl.companyItemsId))).
                get(element.elementText("BRANCH"))).get("itemId").toString();
        firDepartItemId = ((Map) ((Map) (tableStuckCache.get(FirDepartMentImpl.firDpartItemsId))).
                get(getOrgNodeName(element, "DEPARTMENT"))).get("itemId").toString();
    }

    @Override
    public Map<String, Object> getLocalItemId(JSONObject paramJson, Element element) {
        Object itemId = ((Map<String, Object>) tableStuckCache.get(secDepartItemsId)).get(paramJson.get("field_value"));
        Map<String, Object> itemFieldAndCnName = (Map<String, Object>) itemId;
//        String result;
//        if (itemFieldAndCnName == null || !itemFieldAndCnName.get("fieldCnName").equals(paramJson.get("fieldCnName"))) {
//            try {
//                JSONArray andWhere = JSONArray.class.newInstance().put(JSONUtil.createObj().put("field", paramJson.get("field_id"))
//                        .put("query", JSONUtil.createObj().put("eq", paramJson.get("field_value"))))
//                        .put(JSONUtil.createObj().put("field",sec_depart.getSec_depart().getField_id())
//                        .put("query",JSONUtil.createObj().put("eq",getOrgNodeName(element,"SECTION_DESCRIPTION"))));
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

    @Override
    public List readStringXml(String xml) {
        return null;
    }

    @Override
    public JSONObject insertTable(Element element) {
        String tableId = HbTablesId.sec_depart;
        String firDepartCode = getOrgNodeName(element, "DEPARTMENT");
        sec_depart = (Sec_Depart) tableStuckCache.get("sec_depart");
        JSONObject paramJson = JSONUtil.createObj().put("fields", JSONUtil.createObj().
                put(sec_depart.getCompany_name().getField_id(),
                        JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(CompanyImpl.companyItemsId))).get(element.elementText("BRANCH"))).get("itemId")))
                .put(sec_depart.getFir_depart().getField_id(), JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(FirDepartMentImpl.firDpartItemsId))).get(firDepartCode)).get("itemId")))
                .put(sec_depart.getSec_depart().getField_id(), getOrgNodeName(element, "SECTION_DESCRIPTION"))
                .put(sec_depart.getDepart_code().getField_id(), getOrgNodeName(element, "SECTION"))
                .put(sec_depart.getLeaders().getField_id(), element.elementText("")));
        JSONObject reuslt = insertTable(paramJson, tableId);
        return reuslt;

    }

    @Override
    public void saveItemsId(Map itemMap, String field_code) {
        //将以组织编码为Key，Map对象为Value的键值存放到缓存中
        ((Map) tableStuckCache.get(secDepartItemsId)).put(field_code, itemMap);
    }

    @Override
    public boolean deleteTable(Element element) {
        boolean res = false;
        if (isEndOrg(element)) {
            getOrgItemsId(element);
            sec_depart = (Sec_Depart) tableStuckCache.get("sec_depart");
            JSONObject paramJson = JSONUtil.createObj();
            JSONArray andWhere = JSONUtil.createArray().put(JSONUtil.createObj().put("field", sec_depart.getSec_depart().getField_id())
                    .put("query", JSONUtil.createObj().put("eq", JSONUtil.createArray().put(element.elementText("SECTION")))))
                    .put(JSONUtil.createObj().put("field", sec_depart.getCompany_name().getField_id()).put("query", JSONUtil.createObj()
                            .put("eq", JSONUtil.createArray().put(companyItemId))))
                    .put(JSONUtil.createObj().put("field", sec_depart.getFir_depart().getField_id()).put("query", JSONUtil.createObj()
                            .put("eq", JSONUtil.createArray().put(firDepartItemId))));
            paramJson.put("where", JSONUtil.createObj().put("and", andWhere));
            System.out.println(String.format("正在删除二级部门节点：{0}",element.elementText("SECTION")));
            res= deleteJsonObject(paramJson, HbTablesId.sec_depart);
        }
        return res;
    }

    @Override
    public JSONObject updateTable(JSONObject jsonObject, Element element) {
        JSONObject resultJson = null;
        String companyItemId = ((Map) ((Map) (tableStuckCache.get(CompanyImpl.companyItemsId))).
                get(element.elementText("BRANCH"))).get("itemId").toString();
        String firDepartItemId = ((Map) ((Map) (tableStuckCache.get(FirDepartMentImpl.firDpartItemsId))).
                get(getOrgNodeName(element, "DEPARTMENT"))).get("itemId").toString();
        try {
            resultJson = JSONUtil.createObj();
            sec_depart = (Sec_Depart) tableStuckCache.get("sec_depart");
            String fieldCnName = getOrgNodeName(element, "SECTION_DESCRIPTION");
            resultJson.put("fieldCnName", fieldCnName);
            String itemId = ((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("item_id").toString();
            JSONObject dataJson = JSONUtil.createObj().put(sec_depart.getSec_depart().getField_id(), fieldCnName)
                    .put(sec_depart.getCompany_name().getField_id(),JSONUtil.createArray().put(companyItemId))
                    .put(sec_depart.getFir_depart().getField_id(),JSONUtil.createArray().put(firDepartItemId));
            //如果本节点是最后一个组织节点，更新本组织负责人员
            if (isEndOrg(element) && !StrUtil.isBlank(element.elementText("Function_Leader"))) {
                String leaderItemId = getLeaderItemId(element);
                dataJson.put(sec_depart.getLeaders().getField_id(), JSONUtil.createArray().put(leaderItemId));
            }
            JSONObject paramJson = JSONUtil.createObj().put("item_ids", itemId).put("filter", JSONUtil.createObj().put("and",
                    JSONUtil.createArray().put(JSONUtil.createObj().put("field", sec_depart.getDepart_code().getField_id())
                            .put("query", JSONUtil.createObj().put("eq", jsonObject.getStr("field_code"))))))
                    .put("data", dataJson);
            //更新数据
            resultJson.put("rspStatus", updateTable(HbTablesId.sec_depart, paramJson));
        } catch (Exception e) {
            e.printStackTrace();
            XxlJobLogger.log(e.getMessage());
        }
        return resultJson;
    }

    private boolean isEndOrg(Element element) {
        boolean cityIsNull = "A9999".equals(element.elementText("CITY")) || "".equals(element.elementText("CITY"));
        boolean rankIsNull = "A9999".equals(element.elementText("RANK")) || "".equals(element.elementText("RANK"));
        boolean subsectionIsNull = "A9999".equals(element.elementText("SUB_SECTION")) || "".equals(element.elementText("SUB_SECTION"));
        boolean secDepartIsNotNull = !("A9999".equals(element.elementText("SECTION")) || "".equals(element.elementText("SECTION")));
        return cityIsNull && rankIsNull && subsectionIsNull && secDepartIsNotNull;
    }

    /**
     * 从伙伴接口获取二级部门表结构
     *
     * @param tableStrucResult
     * @param sec_depart
     */
    void setFildsMap(JSONObject tableStrucResult, Sec_Depart sec_depart) {
        for (int i = 0; i < ((JSONArray) tableStrucResult.get("field_layout")).size(); i++) {
            for (Object objects : (JSONArray) tableStrucResult.get("fields")
                    ) {
                JSONArray field_layout = ((JSONArray) tableStrucResult.get("field_layout"));
                JSONObject field_id = ((JSONObject) objects);
                if (field_id.get("field_id").equals(field_layout.get(i))) {
                    KeyValueModel valueModel = new KeyValueModel();
                    valueModel.setField_id(field_id.get("field_id").toString());
                    switch (field_id.get("name").toString()) {
                        case "公司":
                            sec_depart.setCompany_name(valueModel);
                            break;
                        case "一级部门":
                            sec_depart.setFir_depart(valueModel);
                            break;
                        case "二级部门名称":
                            sec_depart.setSec_depart(valueModel);
                            break;
                        case "二级部门代码":
                            sec_depart.setDepart_code(valueModel);
                            break;
                        case "负责人(关联)":
                            sec_depart.setLeaders(valueModel);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

}
