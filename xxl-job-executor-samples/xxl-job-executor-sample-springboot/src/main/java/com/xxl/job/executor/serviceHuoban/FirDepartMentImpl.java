package com.xxl.job.executor.serviceHuoban;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.executor.Models.Fir_Depart;
import com.xxl.job.executor.Models.HbTablesId;
import com.xxl.job.executor.Models.KeyValueModel;
import org.dom4j.Element;

import java.util.List;
import java.util.Map;

import static com.xxl.job.executor.service.jobhandler.PersonHbJobHandler.tableStuckCache;

public class FirDepartMentImpl extends BaseHuoBanServ implements IHuoBanService {

    Fir_Depart fir_depart = new Fir_Depart();

    @Override
    public void createFieldsIdMap() {
        if (tableStuckCache.get("fir_depart") == null) {
            JSONObject jsonObject = getTables(HbTablesId.depment);
            setFildsMap(jsonObject, fir_depart);
            tableStuckCache.put("fir_depart", fir_depart);
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
        String tableId = HbTablesId.depment;
        fir_depart = (Fir_Depart) tableStuckCache.get("fir_depart");
        JSONObject paramJson = JSONUtil.createObj().put("fields", JSONUtil.createObj().
                put(fir_depart.getCompany_name().getField_id(),
                        JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get("itemsId"))).get(element.elementText("BRANCH"))).get("itemId")))
                .put(fir_depart.getDepart_code().getField_id(), "A9999".equals(element.elementText("DEPARTMENT")) ?
                        element.elementText("BRANCH") + "b1" : element.elementText("DEPARTMENT"))
                .put(fir_depart.getFir_depart().getField_id(), element.elementText("DEPARTMENT_DESCRIPT") == null ? element.elementText("BRANCH_DESCRIPTION") :
                        element.elementText("DEPARTMENT_DESCRIPT"))
                .put(fir_depart.getLeaders().getField_id(), element.elementText("")));
        JSONObject reuslt = insertTable(paramJson, tableId);
        return reuslt;
    }

    @Override
    public JSONObject updateTable(JSONObject jsonObject, Element element) {
        JSONObject resultJson = JSONUtil.createObj();
        fir_depart = (Fir_Depart) tableStuckCache.get("fir_depart");
        List<JSONObject> jsonArray = (List<JSONObject>) ((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("fields");
        String cnName = ((JSONObject) ((JSONArray) jsonArray.stream().filter(p -> p.getStr("field_id").
                equals(fir_depart.getFir_depart().getField_id())).findFirst().get().get("values")).get(0)).get("value").toString();
        String fieldCnName = element.elementText("DEPARTMENT_DESCRIPT") == null ? element.elementText("BRANCH_DESCRIPTION") :
                element.elementText("DEPARTMENT_DESCRIPT");
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
