package com.xxl.job.executor.serviceHuoban;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.executor.Models.*;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xxl.job.executor.service.jobhandler.PersonHbJobHandler.tableStuckCache;

public class PostNameImpl extends BaseHuoBanServ implements IHuoBanService {

    public static String postNameStruc="postNameStruc";
    public static String postNameItems="postNameItems";
    PostName postName=new PostName();

    @Override
    public String getCacheItemId(Element element) {

        String postItemId = getCacheItemsId(JSONUtil.createObj().put("tableId", HbTablesId.post_name).
                put("field_id", ((PostName) tableStuckCache.get(PostNameImpl.postNameStruc)).getPostCode().getField_id()).
                put("field_value", element.elementText("POSITION"))
                .put("fieldCnName", element.elementTextTrim("JOB_TITLE")), this, element,false);
        return postItemId;
    }

    @Override
    public void createFieldsIdMap() {
        if (tableStuckCache.get(postNameStruc) == null) {
            JSONObject jsonObject = getTables(HbTablesId.post_name);
            setFildsMap(jsonObject, postName);
            /**
             * 将表结构对象存放到缓存当中
             */
            tableStuckCache.put(postNameStruc, postName);
        }
        if (tableStuckCache.get(postNameItems) == null) {
            tableStuckCache.put(postNameItems, new HashMap<>());
        }
    }

    @Override
    public Map getLocalItemId(JSONObject paramJson, Element element) {
        return null;
    }

    @Override
    public List readStringXml(String xml) {
        return null;
    }

    @Override
    public JSONObject insertTable(Element element) {
        String tableId = HbTablesId.post_name;
        PostName postName = (PostName) tableStuckCache.get(postNameStruc);
        JSONObject reuslt=null;
        if ("".equals( StrUtil.nullToDefault(element.elementText("POSITION"),""))){
            JSONObject paramJson = JSONUtil.createObj().put("fields", JSONUtil.createObj().
                    put(postName.getPostCode().getField_id(), element.elementText("POSITION"))
                    .put(postName.getPostName().getField_id(), element.elementText("JOB_TITLE")));
            reuslt = insertTable(paramJson, tableId);
        }
        return reuslt;
    }

    @Override
    public JSONObject updateTable(JSONObject jsonObject, Element element) {
        JSONObject resultJson = JSONUtil.createObj();
        String cnName = StrUtil.split(((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("title").toString(), "")[0];
        String fieldCnName = jsonObject.getStr("fieldCnName") == null ? element.elementText("JOB_TITLE") : jsonObject.getStr("fieldCnName");
        resultJson.put("fieldCnName", fieldCnName);
        if (!cnName.equals(fieldCnName)) {
            PostName postName = (PostName) tableStuckCache.get(postNameStruc);
            String itemId = ((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("item_id").toString();
            JSONObject paramJson = JSONUtil.createObj().put("item_ids", itemId).put("filter", JSONUtil.createObj().put("and",
                    JSONUtil.createArray().put(JSONUtil.createObj().put("field", postName.getPostCode().getField_id())
                            .put("query", JSONUtil.createObj().put("eq", element.elementText("POSITION"))))))
                    .put("data", JSONUtil.createObj().put(postName.getPostName().getField_id(), element.elementText("JOB_TITLE")));
            resultJson.put("rspStatus", updateTable(HbTablesId.comany, paramJson));
        }
        return resultJson;
    }

    @Override
    public void saveItemsId(Map itemMap, String field_code) {
        //将以组织编码为Key，Map对象为Value的键值存放到缓存中
        ((Map) tableStuckCache.get(postNameItems)).put(field_code, itemMap);
    }

    @Override
    public boolean deleteTable(Element element) {
        return false;
    }

    /**
     * 获取岗位信息表字段映射
     * @param jsonObject
     * @param postName
     */
    static void setFildsMap(JSONObject jsonObject, PostName postName) {
        for (int i = 0; i < ((JSONArray) jsonObject.get("field_layout")).size(); i++) {
            for (Object objects : (JSONArray) jsonObject.get("fields")
                    ) {
                JSONArray field_layout = ((JSONArray) jsonObject.get("field_layout"));
                JSONObject field_id = ((JSONObject) objects);
                if (field_id.get("field_id").equals(field_layout.get(i))) {
                    KeyValueModel valueModel = new KeyValueModel();
                    valueModel.setField_id(field_id.get("field_id").toString());
                    switch (field_id.get("name").toString()) {
                        case "岗位编号":
                            postName.setPostCode(valueModel);
                            break;
                        case "岗位名称":
                            postName.setPostName(valueModel);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
}
