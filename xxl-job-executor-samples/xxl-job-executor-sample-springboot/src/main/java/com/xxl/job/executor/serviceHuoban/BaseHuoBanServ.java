package com.xxl.job.executor.serviceHuoban;

import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.Models.HbTablesId;
import com.xxl.job.executor.Models.Staff_Info;
import com.xxl.job.executor.core.config.HuoBanConfig;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Map;

import static com.xxl.job.executor.service.jobhandler.PersonHbJobHandler.tableStuckCache;
import static com.xxl.job.executor.serviceHuoban.StaffInfoImpl.staffInfoTbStruc;

public abstract class BaseHuoBanServ<T> {

    public String getRemoteItem(Element element, JSONObject paramJson, Map<String, Object> itemFieldAndCnName, JSONArray andWhere, IHuoBanService iHuoBanService, boolean isSuper) {
        String teamItemId = "";
        if (itemFieldAndCnName == null || !ObjectUtil.defaultIfNull(itemFieldAndCnName.get("fieldCnName"), "").equals(paramJson.get("fieldCnName"))) {
            try {
                paramJson.put("where", JSONUtil.createObj().put("and", andWhere)).remove("field_id");
                //当伙伴接口获取组织的中文名称与本地缓存的中文名称不一致时重新去接口中伙伴接口中获取
                teamItemId = getItemsIdFromRemote(paramJson, iHuoBanService, element, isSuper);
            } catch (Exception e) {
                XxlJobLogger.log(e.getMessage());
            }
        } else {
            teamItemId = itemFieldAndCnName.get("itemId").toString();
        }
        return teamItemId;
    }


    /**
     * 从伙伴接口数据内容表中获取itemsId     *
     *
     * @param paramJson 有几个Key{ tableId、field_id、field_value}
     * @return
     */
    public String getItemsIdFromRemote(JSONObject paramJson, IHuoBanService iHuoBanService, Element element, boolean isSuperior) {
        String tableId = paramJson.getStr("tableId");
        String field_code = paramJson.getStr("field_value");
        String fieldCnName = paramJson.getStr("fieldCnName");
        boolean isDelete = paramJson.getBool("isDelete") == null ? false : paramJson.getBool("isDelete");
//        try {
//            JSONArray andWhere = JSONArray.class.newInstance().put(JSONUtil.createObj().put("field", paramJson.get("field_id"))
//                    .put("query", JSONUtil.createObj().put("eq", paramJson.get("field_value"))));
//            paramJson.put("where", JSONUtil.createObj().put("and", andWhere)).remove("field_id");
//        } catch (Exception e) {
//            XxlJobLogger.log(e.getMessage());
//        }
        //移除掉接口不需要的参数
        paramJson.remove("query");
        paramJson.remove("tableId");
        paramJson.remove("field_value");
        paramJson.remove("fieldCnName");
        paramJson.remove("isDelete");
        JSONObject result = getJsonObject(paramJson, tableId, "find");

        //定义一个存放itemId和中文名的Map对象
        Map<String, String> itemMap = new HashMap<>();
        String itemId = ((JSONArray) result.get("items")).size() > 0 ?
                ((JSONObject) ((JSONArray) result.get("items")).get(0)).get("item_id").toString() : "";
        String cnName = "";
        //当不是获取上长并且不是删除组织节点的时候去更新或新增
        if (!isSuperior && !isDelete) {
            if (itemId.length() > 0) {
                JSONObject updateResult = iHuoBanService.updateTable(result.put("fieldCnName", fieldCnName).put("field_code", field_code), element);
                cnName = updateResult.getStr("fieldCnName");
                //去更新伙伴系统数据
                if (updateResult.get("rspStatus") != null && ((Integer) updateResult.get("rspStatus")) == 500) {
                    XxlJobLogger.log(element.elementText("BRANCH") + "组织更新失败！");
                }
            } else {
                //如果组织节点不存在就插入进去
                result = iHuoBanService.insertTable(element);
                if (result != null) {
                    itemId = result.get("item_id").toString();
                    cnName = StrUtil.split(result.get("title").toString(), "")[0];
                }
            }
            if (cnName != null) {
                //将中文名称放到Map对象中
                itemMap.put("fieldCnName", cnName);
                //将ItemId放到Map对象中
                itemMap.put("itemId", itemId);
                //将以组织编码为Key，Map对象为Value的键值存放到缓存中
                iHuoBanService.saveItemsId(itemMap, field_code);
            }
        }
        return itemId;
    }

    /**
     * 查询和删除通用post操作接口
     *
     * @param paramJson
     * @param tableId
     * @param opFlag    find 或 delete
     * @return
     */
    protected JSONObject getJsonObject(JSONObject paramJson, String tableId, String opFlag) {
        return JSONUtil.parseObj(UnicodeUtil.toString(HttpRequest.post(StrUtil.format(
                HuoBanConfig.props.getProperty("HuoBanBaseURL") + "v2/item/table/{}/{}", tableId, opFlag))
                .header(Header.CONTENT_TYPE, "application/json")
                .header("X-Huoban-Ticket", HuoBanConfig.getTicketJson().get("ticket").toString())
                .body(paramJson.toString())
                .execute().body()));
    }

    /**
     * 查询和删除通用post操作接口
     *
     * @param paramJson
     * @param tableId
     * @return
     */
    protected boolean deleteJsonObject(JSONObject paramJson, String tableId) {
        int res = HttpRequest.post(StrUtil.format(
                HuoBanConfig.props.getProperty("HuoBanBaseURL") + "v2/item/table/{}/delete", tableId))
                .header(Header.CONTENT_TYPE, "application/json")
                .header("X-Huoban-Ticket", HuoBanConfig.getTicketJson().get("ticket").toString())
                .body(paramJson.toString())
                .execute().getStatus();
        System.out.println("已经删除成功！");
        return res == 200 ? true : false;
    }

    /**
     * 获取缓存中的组织编码对应的itemId
     *
     * @param paramJson
     * @return 如果缓存中存在值就返回缓存内容，如果缓存中没有就直接从接口里面去获取数据
     */
    public String getCacheItemsId(JSONObject paramJson, IHuoBanService iHuoBanService, Element element, boolean isSuperior) {
        Map<String, Object> itemFieldAndCnName = (Map<String, Object>) iHuoBanService.getLocalItemId(paramJson, element);
        String result;
        if (itemFieldAndCnName == null || !itemFieldAndCnName.get("fieldCnName").equals(paramJson.get("fieldCnName"))) {
            //当伙伴接口获取组织的中文名称与本地缓存的中文名称不一致时重新去接口中伙伴接口中获取
            result = getItemsIdFromRemote(paramJson, iHuoBanService, element, isSuperior);
        } else {
            result = itemFieldAndCnName.get("itemId").toString();
        }
        return result;
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
        if (reuslt.size() < 10) {
            XxlJobLogger.log(reuslt.getStr("message"));
        }
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
        String url = StrUtil.format(HuoBanConfig.props.getProperty("HuoBanBaseURL") + "v2/item/table/{}/update", tableId);
        HttpResponse response = HttpRequest.post(url).
                header(Header.CONTENT_TYPE, "application/json").
                header("X-Huoban-Ticket", HuoBanConfig.getTicketJson().get("ticket").toString()).
                body(paramJson.toString()).execute();
        if (response.getStatus() == 500) {
            String resMsg = JSONUtil.parseObj(UnicodeUtil.toString(StrUtil.utf8Str(response.bodyBytes()))).getStr("message");
            XxlJobLogger.log(resMsg);
            System.out.println(resMsg);
        }
        return response.getStatus();
    }

    /**
     * 获取会员帐号
     *
     * @param phoneNumber
     * @return
     */
    public String getMemberId(String phoneNumber) {
        String url = HuoBanConfig.props.getProperty("HuoBanBaseURL") + "v2/company_members/company/" + HuoBanConfig.props.getProperty("companyId");
        JSONObject paramJson = JSONUtil.createObj();
        paramJson.put("limit", 1)
                .put("offset", 0)
                .put("search", phoneNumber)
                .put("company_id", HuoBanConfig.props.getProperty("companyId"));
        JSONObject result = JSONUtil.parseObj(UnicodeUtil.toString(HttpRequest.post(url)
                .header("Content-Type", "application/json")
                .header(Header.HOST, "api.huoban.com")
                .header("Authorization", "Bearer " + HuoBanConfig.getAuthTokenJson().getStr("access_token"))
                .body(paramJson.toString()).execute().body()));
        return result.getStr("total") == "0" ? "" : result.getStr("user_id");
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

    /**
     * 获取上长的ItemId
     *
     * @param element
     * @return
     */
    public String getLeaderItemId(Element element) {
        return getCacheItemsId(JSONUtil.createObj().put("tableId", HbTablesId.staff_info)
                .put("field_id", ((Staff_Info) tableStuckCache.get(staffInfoTbStruc)).getStaff_number().getField_id())
                .put("field_value", element.elementText("Function_Leader")), new TeamNameImpl(), element, true);
    }

    public boolean deleteTable(Class<T> t, Element element) {
        boolean result = false;
        try {
            IHuoBanService iHuoBanService = (IHuoBanService) t.newInstance();
            result = iHuoBanService.deleteTable(element);
        } catch (Exception e) {
            e.printStackTrace();
            XxlJobLogger.log(e.getMessage());
        }
        return result;
    }

    /**
     * 获取各节点的值
     * 当前节点编码为A9999时取上一节点的编码加补码
     *
     * @param element
     * @param orgNodeCode
     * @return
     */
    public String getOrgNodeName(Element element, String orgNodeCode) {
        String res = "";
        switch (orgNodeCode) {
            case "DEPARTMENT":
                res = "A9999".equals(element.elementText("DEPARTMENT")) ? element.elementText("BRANCH") + "b1" : element.elementText("DEPARTMENT");
                break;
            case "DEPARTMENT_DESCRIPTION":
                res = "/".equals(StrUtil.nullToDefault(element.elementText("DEPARTMENT_DESCRIPTION"), "/")) || "".equals(StrUtil.nullToDefault(element.elementText("DEPARTMENT_DESCRIPTION"), "")) ?
                        element.elementText("BRANCH_DESCRIPTION") : element.elementText("DEPARTMENT_DESCRIPTION");
                break;
            case "SECTION":
                res = "A9999".equals(element.elementText("SECTION")) || "".equals(element.elementText("SECTION"))
                        ? ("A9999".equals(element.elementText("DEPARTMENT")) ? element.elementText("BRANCH") + "b1" : element.elementText("DEPARTMENT"))
                        + "b2" : element.elementText("SECTION");
                break;
            case "SECTION_DESCRIPTION":
                res = "/".equals(StrUtil.nullToDefault(element.elementText("SECTION_DESCRIPTION"), "/")) || "".equals(StrUtil.nullToDefault(element.elementText("SECTION_DESCRIPTION"), "")) ?
                        "/".equals(StrUtil.nullToDefault(element.elementText("DEPARTMENT_DESCRIPTION"), "/")) || "".equals(StrUtil.nullToDefault(element.elementText("DEPARTMENT_DESCRIPT"), "")) ?
                                element.elementText("BRANCH_DESCRIPTION") : element.elementText("DEPARTMENT_DESCRIPTION") :
                        element.elementText("SECTION_DESCRIPTION");
                break;
            case "SUB_SECTION":
                res = "A9999".equals(element.elementText("SUB_SECTION")) || "".equals(element.elementText("SUB_SECTION"))
                        ? ("A9999".equals(element.elementText("SECTION")) || "".equals(element.elementText("SECTION"))
                        ? ("A9999".equals(element.elementText("DEPARTMENT")) ?
                        element.elementText("BRANCH") + "b1" : element.elementText("DEPARTMENT"))
                        + "b2" : element.elementText("SECTION")) + "b3" : element.elementText("SUB_SECTION");
                break;
            case "SUB_DESCRIPTION":
                res = "/".equals(StrUtil.nullToDefault(element.elementText("SUB_DESCRIPTION"), "/")) || "".equals(StrUtil.nullToDefault(element.elementText("SUB_DESCRIPTION"), "")) ?
                        "/".equals(StrUtil.nullToDefault(element.elementText("SECTION_DESCRIPTION"), "/")) || "".equals(StrUtil.nullToDefault(element.elementText("SECTION_DESCRIPTION"), ""))
                                ? "/".equals(StrUtil.nullToDefault(element.elementText("DEPARTMENT_DESCRIPTION"), "/")) || "".equals(StrUtil.nullToDefault(element.elementText("DEPARTMENT_DESCRIPT"), ""))
                                ? element.elementText("BRANCH_DESCRIPTION") : element.elementText("DEPARTMENT_DESCRIPTION") :
                                element.elementText("SECTION_DESCRIPTION") : element.elementText("SUB_DESCRIPTION");
                break;
            case "CITY":
                res = "A9999".equals(element.elementText("CITY")) || "".equals(element.elementText("CITY"))
                        ? ("A9999".equals(element.elementText("SUB_SECTION")) || "".equals(element.elementText("SUB_SECTION")) ?
                        ("A9999".equals(element.elementText("SECTION")) || "".equals(element.elementText("SECTION"))
                                ? ("A9999".equals(element.elementText("DEPARTMENT")) ?
                                element.elementText("BRANCH") + "b1" : element.elementText("DEPARTMENT"))
                                + "b2" : element.elementText("SECTION")) + "b3" : element.elementText("SUB_SECTION")) + "b4" : element.elementText("CITY");
                break;
            case "CITY_DESCRIPTION":
                res = "/".equals(StrUtil.nullToDefault(element.elementText("CITY_DESCRIPTION"), "/")) || "".equals(StrUtil.nullToDefault(element.elementText("CITY_DESCRIPTION"), "")) ?
                        "/".equals(StrUtil.nullToDefault(element.elementText("SUB_DESCRIPTION"), "/")) || "".equals(StrUtil.nullToDefault(element.elementText("SUB_DESCRIPTION"), "")) ?
                                "/".equals(StrUtil.nullToDefault(element.elementText("SECTION_DESCRIPTION"), "/")) || "".equals(StrUtil.nullToDefault(element.elementText("SECTION_DESCRIPTION"), ""))
                                        ? "/".equals(StrUtil.nullToDefault(element.elementText("DEPARTMENT_DESCRIPTION"), "/")) || "".equals(StrUtil.nullToDefault(element.elementText("DEPARTMENT_DESCRIPT"), "")) ?
                                        element.elementText("BRANCH_DESCRIPTION") : element.elementText("DEPARTMENT_DESCRIPTION") :
                                        element.elementText("SECTION_DESCRIPTION") : element.elementText("SUB_DESCRIPTION") :
                        element.elementText("CITY_DESCRIPTION");
                break;
            case "RANK":
                res = "A9999".equals(element.elementText("RANK")) || "".equals(element.elementText("RANK"))
                        ? ("A9999".equals(element.elementText("CITY")) || "".equals(element.elementText("CITY"))
                        ? ("A9999".equals(element.elementText("SUB_SECTION")) || "".equals(element.elementText("SUB_SECTION")) ?
                        ("A9999".equals(element.elementText("SECTION")) || "".equals(element.elementText("SECTION"))
                                ? ("A9999".equals(element.elementText("DEPARTMENT")) || "".equals(element.elementText("DEPARTMENT")) ?
                                element.elementText("BRANCH") + "b1" : element.elementText("DEPARTMENT"))
                                + "b2" : element.elementText("SECTION")) + "b3" : element.elementText("SUB_SECTION")) + "b4" : element.elementText("CITY")) + "b5" : element.elementText("RANK");
                break;
            case "RANK_DESCRIPTION":
                res = "/".equals(StrUtil.nullToDefault(element.elementText("RANK_DESCRIPTION"), "/")) || "".equals(StrUtil.nullToDefault(element.elementText("RANK_DESCRIPTION"), "")) ?
                        "/".equals(StrUtil.nullToDefault(element.elementText("CITY_DESCRIPTION"), "/")) || "".equals(StrUtil.nullToDefault(element.elementText("CITY_DESCRIPTION"), "")) ?
                                "/".equals(StrUtil.nullToDefault(element.elementText("SUB_DESCRIPTION"), "/")) || "".equals(StrUtil.nullToDefault(element.elementText("SUB_DESCRIPTION"), "")) ?
                                        "/".equals(StrUtil.nullToDefault(element.elementText("SECTION_DESCRIPTION"), "/")) || "".equals(StrUtil.nullToDefault(element.elementText("SECTION_DESCRIPTION"), "")) ?
                                                "/".equals(StrUtil.nullToDefault(element.elementText("DEPARTMENT_DESCRIPTION"), "/")) || "".equals(StrUtil.nullToDefault(element.elementText("DEPARTMENT_DESCRIPT"), "")) ?
                                                        element.elementText("BRANCH_DESCRIPTION") : element.elementText("DEPARTMENT_DESCRIPTION") :
                                                element.elementText("SECTION_DESCRIPTION") : element.elementText("SUB_DESCRIPTION") :
                                element.elementText("CITY_DESCRIPTION") : element.elementText("RANK_DESCRIPTION");
                break;
            default:
                res = "";
                break;
        }
        //在更新人员信息的时候各组织节点中文描述是空的没有，所以这时统一给他们赋公司的名称
        return res == null ? element.elementText("BRANCH_DESCRIPTION") : res;
    }

}
