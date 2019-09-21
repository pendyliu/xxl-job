package com.xxl.job.executor.serviceHuoban;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.core.log.XxlJobLogger;
import org.dom4j.Element;

import java.util.List;

public class GroupImpl extends BaseHuoBanServ implements IHuoBanService {
    @Override
    public void createFieldsIdMap() {

    }

    @Override
    public String getItemId(JSONObject paramJson, Element element) {
        try {
            JSONArray andWhere = JSONArray.class.newInstance().put(JSONUtil.createObj().put("field", paramJson.get("field_id"))
                    .put("query", JSONUtil.createObj().put("eq", paramJson.get("field_value"))));
            paramJson.put("where", JSONUtil.createObj().put("and", andWhere)).remove("field_id");
        } catch (Exception e) {
            XxlJobLogger.log(e.getMessage());
        }
        return getItemsId(paramJson, this, element);
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
    public JSONObject updateTable(JSONObject jsonObject, Element element) {
        return null;
    }
}
