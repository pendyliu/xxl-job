package com.xxl.job.executor.serviceHuoban;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.executor.Models.HbTablesId;
import com.xxl.job.executor.Models.KeyValueModel;
import com.xxl.job.executor.Models.Sec_Depart;
import org.dom4j.Element;

import java.util.List;
import java.util.Map;

import static com.xxl.job.executor.service.jobhandler.PersonHbJobHandler.tableStuckCache;

public class Sec_DepartImpl extends BaseHuoBanServ implements IHuoBanService {
    Sec_Depart sec_depart = new Sec_Depart();

    @Override
    public void createFieldsIdMap() {
        if (tableStuckCache.get("sec_depart") == null) {
            JSONObject tableStrucResult = getTables(HbTablesId.sec_depart);
            setFildsMap(tableStrucResult, sec_depart);
            tableStuckCache.put("sec_depart", sec_depart);
        }
    }

    @Override
    public String getItemId(JSONObject paramJson) {
        return null;
    }

    @Override
    public List readStringXml(String xml) {
        return null;
    }

    @Override
    public JSONObject insertTable(Element element) {
        String tableId = HbTablesId.sec_depart;
        String fir_depar_code = "A9999".equals(element.elementText("DEPARTMENT")) ? element.elementText("BRANCH") + "b1"
                : element.elementText("DEPARTMENT");
        sec_depart = (Sec_Depart) tableStuckCache.get("sec_depart");
        JSONObject paramJson = JSONUtil.createObj().put("fields", JSONUtil.createObj().
                put(sec_depart.getCompany_name().getField_id(),
                        JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get("itemsId"))).get(element.elementText("BRANCH"))).get("itemId")))
                .put(sec_depart.getFir_depart().getField_id(), JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get("itemsId"))).get(fir_depar_code)).get("itemId")))
                .put(sec_depart.getSec_depart().getField_id(), element.elementText("SECTION_DESCRIPTION") == null ? element.elementText("BRANCH_DESCRIPTION") :
                        element.elementText("SECTION_DESCRIPTION"))
                .put(sec_depart.getDepart_code().getField_id(), "A9999".equals(element.elementText("SECTION")) ? element.elementText("BRANCH") + "b2"
                        : element.elementText("SECTION"))
                .put(sec_depart.getLeaders().getField_id(), element.elementText("")));
        JSONObject reuslt = insertTable(paramJson, tableId);
        return reuslt;

    }

    @Override
    public JSONObject updateTable(JSONObject jsonObject, Element element) {
        JSONObject resultJson = JSONUtil.createObj();
        sec_depart = (Sec_Depart) tableStuckCache.get("sec_depart");
        List<JSONObject> jsonArray = (List<JSONObject>) ((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("fields");
        String cnName = ((JSONObject) ((JSONArray) jsonArray.stream().filter(p -> p.getStr("field_id").
                equals(sec_depart.getSec_depart().getField_id())).findFirst().get().get("values")).get(0)).get("value").toString();
        String fieldCnName = element.elementText("SECTION_DESCRIPTION") == null ? element.elementText("BRANCH_DESCRIPTION") :
                element.elementText("SECTION_DESCRIPTION");
        resultJson.put("fieldCnName", fieldCnName);
        if (!cnName.equals(fieldCnName)) {
            String itemId = ((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("item_id").toString();
            JSONObject paramJson = JSONUtil.createObj().put("item_ids", itemId).put("filter", JSONUtil.createObj().put("and",
                    JSONUtil.createArray().put(JSONUtil.createObj().put("field", sec_depart.getDepart_code().getField_id())
                            .put("query", JSONUtil.createObj().put("eq", jsonObject.getStr("field_code"))))))
                    .put("data", JSONUtil.createObj().put(sec_depart.getSec_depart().getField_id(), fieldCnName));
            //更新数据
            resultJson.put("rspStatus", updateTable(HbTablesId.sec_depart, paramJson));
        }
        return resultJson;
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
