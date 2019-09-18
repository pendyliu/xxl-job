package com.xxl.job.executor.serviceHuoban;

import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.core.config.HuoBanConfig;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Map;

import static com.xxl.job.executor.service.jobhandler.PersonHbJobHandler.tableStuckCache;

public abstract class BaseHuoBanServ {
    /**
     * 从伙伴接口数据内容表中获取itemsId     *
     *
     * @param paramJson 有几个Key{ tableId、field_id、field_value}
     * @return
     */
    public String getItemsId(JSONObject paramJson, IHuoBanService iHuoBanService, Element element) {
        String tableId = paramJson.getStr("tableId");
        String field_code = paramJson.get("field_value").toString();
        try {
            JSONArray andWhere = JSONArray.class.newInstance().put(JSONUtil.createObj().put("field", paramJson.get("field_id"))
                    .put("query", JSONUtil.createObj().put("eq", paramJson.get("field_value"))));
            paramJson.put("where", JSONUtil.createObj().put("and", andWhere)).remove("field");
            paramJson.remove("query");
            paramJson.remove("tableId");
            paramJson.remove("field_value");
        } catch (Exception e) {
            XxlJobLogger.log(e.getMessage());
        }

        JSONObject result = JSONUtil.parseObj(UnicodeUtil.toString(HttpRequest.post(StrUtil.format(
                HuoBanConfig.props.getProperty("HuoBanBaseURL") + "v2/item/table/{}/find", tableId))
                .header(Header.CONTENT_TYPE, "application/json")
                .header("X-Huoban-Ticket", HuoBanConfig.getTicketJson().get("ticket").toString())
                .body(paramJson.toString())
                .execute().body()));
        String itemId = ((JSONArray) result.get("items")).size() > 0 ?
                ((JSONObject) ((JSONArray) result.get("items")).get(0)).get("item_id").toString() : "";
        if (itemId.length() > 0) {
            String cnName = StrUtil.split(((JSONObject) ((JSONArray) result.get("items")).get(0)).get("title").toString(), "")[0];
            if (!cnName.equals(element.elementText("BRANCH_DESCRIPTION"))) {
                //如果中文名称不一样的话就去更新伙伴系统数据
               if ( iHuoBanService.updateTable(result, element)==200){
                   XxlJobLogger.log(element.elementText("BRANCH")+"组织名称更新为："+element.elementText("BRANCH_DESCRIPTION"));
               }
            }
            ((Map) tableStuckCache.get("itemsId")).put(field_code, itemId);
        } else {
            result = iHuoBanService.insertTable(element);
            itemId = ((JSONObject) ((JSONArray) result.get("items")).get(0)).get("item_id").toString();
        }
        return itemId;
    }

    /**
     * 获取缓存中的组织编码对应的itemId
     *
     * @param paramJson
     * @return 如果缓存中存在值就返回缓存内容，如果缓存中没有就直接从接口里面去获取数据
     */
    public String getCacheItemsId(JSONObject paramJson, IHuoBanService iHuoBanService, Element element) {
        if (tableStuckCache.get("itemsId") == null) {
            Map<String, Object> itemIds = new HashMap<>();
            tableStuckCache.put("itemsId", itemIds);
        }
        Object itemId = ((Map<String, Object>) tableStuckCache.get("itemsId")).get(paramJson.get("field_value"));
        return itemId == null ? getItemsId(paramJson, iHuoBanService, element) : itemId.toString();
    }

    /**
     * 向伙伴系统创建数据
     *
     * @param paramJson
     * @return
     */
    public JSONObject insertTable(JSONObject paramJson, String tableId) {
        JSONObject reuslt = JSONUtil.parseObj(UnicodeUtil.toString(HttpRequest.post(
                StrUtil.format(HuoBanConfig.props.getProperty("HuoBanBaseURL") + "v2/item/table/{}", tableId))
                .header(Header.CONTENT_TYPE, "application/json")
                .header("X-Huoban-Ticket", HuoBanConfig.getTicketJson().get("ticket").toString())
                .body(paramJson.toString()).execute().body()));
        return reuslt;
    }

    /**
     * 批量更新伙伴系统数据
     *
     * @param tableId
     * @param paramJson
     * @return
     */
    public int updateTable(String tableId, JSONObject paramJson) {
        String url = StrUtil.format(HuoBanConfig.props.getProperty("HuoBanBaseURL") + "/v2/item/table/{}/update", tableId);
        HttpResponse response = HttpRequest.post(url).
                header(Header.CONTENT_TYPE, "application/json").
                header("X-Huoban-Ticket", HuoBanConfig.getTicketJson().get("ticket").toString()).
                body(paramJson.toString()).execute();
        return response.getStatus();
    }

}
