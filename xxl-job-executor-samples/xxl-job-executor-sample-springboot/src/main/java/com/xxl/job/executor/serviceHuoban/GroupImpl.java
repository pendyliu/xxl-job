package com.xxl.job.executor.serviceHuoban;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.executor.Models.Group;
import com.xxl.job.executor.Models.HbTablesId;
import com.xxl.job.executor.Models.KeyValueModel;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xxl.job.executor.service.jobhandler.PersonHbJobHandler.tableStuckCache;

public class GroupImpl extends BaseHuoBanServ implements IHuoBanService {
    Group group = new Group();
    public static String groupItemsId = "groupItemsId";
    public static String groupStruc = "group";

    @Override
    public void saveItemsId(Map itemMap, String field_code) {
        //将以组织编码为Key，Map对象为Value的键值存放到缓存中
        ((Map) tableStuckCache.get(groupItemsId)).put(field_code, itemMap);
    }

    @Override
    public void createFieldsIdMap() {
        if (tableStuckCache.get(groupStruc) == null) {
            JSONObject jsonObject = getTables(HbTablesId.group);
            setFildsMap(jsonObject, group);
            tableStuckCache.put(groupStruc, group);
        }
        if (tableStuckCache.get(groupItemsId) == null) {
            tableStuckCache.put(groupItemsId, new HashMap<>());
        }
    }

    @Override
    public Map<String, Object> getItemId(JSONObject paramJson, Element element) {
        Object itemId = ((Map<String, Object>) tableStuckCache.get(groupItemsId)).get(paramJson.get("field_value"));
        Map<String, Object> itemFieldAndCnName = (Map<String, Object>) itemId;
//        try {
//            JSONArray andWhere = JSONArray.class.newInstance().put(JSONUtil.createObj().put("field", paramJson.get("field_id"))
//                    .put("query", JSONUtil.createObj().put("eq", paramJson.get("field_value"))));
//            paramJson.put("where", JSONUtil.createObj().put("and", andWhere)).remove("field_id");
//        } catch (Exception e) {
//            XxlJobLogger.log(e.getMessage());
//        }
//        return getItemsId(paramJson, this, element);
        return itemFieldAndCnName;
    }

    @Override
    public String getCacheItemId(Element element) {
        String cityItemId = getCacheItemsId(JSONUtil.createObj().put("tableId", HbTablesId.group)
                .put("field_id", ((Group) tableStuckCache.get("keClass")).getGroup_code().getField_id())
                .put("field_value", getOrgNodeName(element, "CITY")), this, element);
        return cityItemId;
    }

    @Override
    public List readStringXml(String xml) {
        return null;
    }

    @Override
    public JSONObject insertTable(Element element) {
        String tableId = HbTablesId.group;
        String firDepartCode = getOrgNodeName(element, "DEPARTMENT");
        String sec_depart_code = getOrgNodeName(element, "SECTION");
        String subSectionCode = getOrgNodeName(element, "SUB_SECTION");
        group = (Group) tableStuckCache.get(groupStruc);
        JSONObject paramJson = JSONUtil.createObj().put("fields", JSONUtil.createObj().
                put(group.getCompany_name().getField_id(),
                        JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(CompanyImpl.companyItemsId))).get(element.elementText("BRANCH"))).get("itemId")))
                .put(group.getFir_depart().getField_id(), JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(FirDepartMentImpl.firDpartItemsId))).get(firDepartCode)).get("itemId")))
                .put(group.getSec_depart().getField_id(), JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(Sec_DepartImpl.secDepartItemsId))).get(sec_depart_code)).get("itemId")))
                .put(group.getKe_class().getField_id(), JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(KeClassImpl.keClassItemsId))).get(subSectionCode)).get("itemId")))
                .put(group.getGroup_name().getField_id(), getOrgNodeName(element, "CITY_DESCRIPTION"))
                .put(group.getGroup_code().getField_id(), getOrgNodeName(element, "CITY"))
                .put(group.getLeaders().getField_id(), element.elementText("")));
        JSONObject reuslt = insertTable(paramJson, tableId);
        return reuslt;
    }

    @Override
    public JSONObject updateTable(JSONObject jsonObject, Element element) {
        JSONObject resultJson = JSONUtil.createObj();
        group = (Group) tableStuckCache.get(groupStruc);
        List<JSONObject> jsonArray = (List<JSONObject>) ((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("fields");
        String cnName = ((JSONObject) ((JSONArray) jsonArray.stream().filter(p -> p.getStr("field_id").
                equals(group.getGroup_name().getField_id())).findFirst().get().get("values")).get(0)).get("value").toString();
        String fieldCnName = getOrgNodeName(element, "CITY_DESCRIPTION");
        resultJson.put("fieldCnName", fieldCnName);
        if (!cnName.equals(fieldCnName)) {
            String itemId = ((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("item_id").toString();
            JSONObject paramJson = JSONUtil.createObj().put("item_ids", itemId).put("filter", JSONUtil.createObj().put("and",
                    JSONUtil.createArray().put(JSONUtil.createObj().put("field", group.getGroup_code().getField_id())
                            .put("query", JSONUtil.createObj().put("eq", jsonObject.getStr("field_code"))))))
                    .put("data", JSONUtil.createObj().put(group.getGroup_name().getField_id(), fieldCnName));
            //更新数据
            resultJson.put("rspStatus", updateTable(HbTablesId.group, paramJson));
        }
        return resultJson;
    }

    /**
     * 班的数据结构字段映射表
     *
     * @param tableStrucResult
     * @param group
     */
    void setFildsMap(JSONObject tableStrucResult, Group group) {
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
                            group.setCompany_name(valueModel);
                            break;
                        case "一级部门":
                            group.setFir_depart(valueModel);
                            break;
                        case "二级部门":
                            group.setSec_depart(valueModel);
                            break;
                        case "课":
                            group.setKe_class(valueModel);
                            break;
                        case "班名称":
                            group.setGroup_name(valueModel);
                            break;
                        case "班代码":
                            group.setGroup_code(valueModel);
                            break;
                        case "负责人":
                            group.setLeaders(valueModel);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
}
