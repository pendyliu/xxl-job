package com.xxl.job.executor.serviceHuoban;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.xxl.job.executor.Models.Fir_Depart;
import com.xxl.job.executor.Models.HbTablesId;
import com.xxl.job.executor.Models.KeyValueModel;
import org.dom4j.Element;

import java.util.List;

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
        return null;
    }

    @Override
    public int updateTable(JSONObject jsonObject, Element element) {
        return 0;
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
