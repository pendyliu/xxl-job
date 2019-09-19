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
        String field_code = paramJson.getStr("field_value");
        String fieldCnName=paramJson.getStr("fieldCnName");
        try {
            JSONArray andWhere = JSONArray.class.newInstance().put(JSONUtil.createObj().put("field", paramJson.get("field_id"))
                    .put("query", JSONUtil.createObj().put("eq", paramJson.get("field_value"))));
            paramJson.put("where", JSONUtil.createObj().put("and", andWhere)).remove("field_id");
            //移除掉接口不需要的参数
            paramJson.remove("query");
            paramJson.remove("tableId");
            paramJson.remove("field_value");
            paramJson.remove("fieldCnName");
        } catch (Exception e) {
            XxlJobLogger.log(e.getMessage());
        }

        JSONObject result = JSONUtil.parseObj(UnicodeUtil.toString(HttpRequest.post(StrUtil.format(
                HuoBanConfig.props.getProperty("HuoBanBaseURL") + "v2/item/table/{}/find", tableId))
                .header(Header.CONTENT_TYPE, "application/json")
                .header("X-Huoban-Ticket", HuoBanConfig.getTicketJson().get("ticket").toString())
                .body(paramJson.toString())
                .execute().body()));
        //定义一个存放itemId和中文名的Map对象
        Map<String, String> itemMap = new HashMap<>();
         String itemId = ((JSONArray) result.get("items")).size() > 0 ?
                ((JSONObject) ((JSONArray) result.get("items")).get(0)).get("item_id").toString() : "";
        String cnName = "";
        if (itemId.length() > 0) {
            cnName = StrUtil.split(((JSONObject) ((JSONArray) result.get("items")).get(0)).get("title").toString(), "")[0];
            if (!cnName.equals(fieldCnName)) {
                //如果中文名称不一样的话就去更新伙伴系统数据
                if (iHuoBanService.updateTable(result, element) == 200) {
                    XxlJobLogger.log(element.elementText("BRANCH") + "组织名称更新为：" + element.elementText("BRANCH_DESCRIPTION"));
                }
            }
        } else {
            result = iHuoBanService.insertTable(element);
            itemId = result.get("item_id").toString();
            cnName = StrUtil.split(result.get("title").toString(), "")[0];
        }
        //将中文名称放到Map对象中
        itemMap.put("fieldCnName", cnName);
        //将ItemId放到Map对象中
        itemMap.put("itemId", itemId);
        //将以组织编码为Key，Map对象为Value的键值存放到缓存中
        ((Map) tableStuckCache.get("itemsId")).put(field_code, itemMap);
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
        Map<String, Object> itemFieldAndCnName = (Map<String, Object>) itemId;
        return (itemFieldAndCnName == null || !itemFieldAndCnName.get("fieldCnName").equals(paramJson.get("fieldCnName"))) ?
                getItemsId(paramJson, iHuoBanService, element) : itemFieldAndCnName.get("itemId").toString();
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


    /**
     * 获取表结构
     *
     * @param tableId
     * @return
     */
    public static JSONObject getTables(String tableId) {
        String url = StrUtil.format(HuoBanConfig.props.getProperty("HuoBanBaseURL") + "v2/table/{}", tableId);
        JSONObject result = JSONUtil.parseObj(UnicodeUtil.toString(HttpRequest.get(url).header(Header.CONTENT_TYPE, "application/json")
                .header(Header.HOST, "api.huoban.com")
                .header(Header.ACCEPT_CHARSET, "utf-8")
                .header("X-Huoban-Ticket", HuoBanConfig.getTicketJson().getStr("ticket"))
                .execute().body()));
        return result;
    }

}
