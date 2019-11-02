package com.xxl.job.executor.serviceHuoban;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.Models.Company;
import com.xxl.job.executor.Models.HbTablesId;
import com.xxl.job.executor.Models.KeyValueModel;
import com.xxl.job.executor.Models.Team_Name;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.*;

import static com.xxl.job.executor.service.jobhandler.PersonHbJobHandler.tableStuckCache;

/**
 * @author pendy
 */
public class TeamNameImpl extends BaseHuoBanServ implements IHuoBanService {
    static Team_Name team_name = new Team_Name();
    public static String rankItemsId = "rankItemsId";
    public static String rankStruc = "team_name";

    String companyItemId;
    String secDepartItemId;
    String firDepartItemId;
    String subSectionItemId;
    String groupItemId;

    @Override
    public void createFieldsIdMap() {
        if (tableStuckCache.get(rankStruc) == null) {
            JSONObject jsonObject = getTables(HbTablesId.team_name);
            setFildsMap(jsonObject, team_name);
            /**
             * 将表结构对象存放到缓存当中
             */
            tableStuckCache.put(rankStruc, team_name);
        }
        if (tableStuckCache.get(rankItemsId) == null) {
            tableStuckCache.put(rankItemsId, new HashMap<>());
        }
    }

    @Override
    public Map<String, Object> getLocalItemId(JSONObject paramJson, Element element) {
        Object itemId = ((Map<String, Object>) tableStuckCache.get(rankItemsId)).get(paramJson.get("field_value"));
        Map<String, Object> itemFieldAndCnName = (Map<String, Object>) itemId;
        return itemFieldAndCnName;
    }

    @Override
    public void saveItemsId(Map itemMap, String field_code) {
        //将以组织编码为Key，Map对象为Value的键值存放到缓存中
        ((Map) tableStuckCache.get(rankItemsId)).put(field_code, itemMap);
    }

    @Override
    public boolean deleteTable(Element element) {
        boolean res = false;
        boolean parentOrgIsNotNull = getOrgItemsId(element, true);
        if (parentOrgIsNotNull) {
            team_name = (Team_Name) tableStuckCache.get("team_name");
            JSONObject paramJson = JSONUtil.createObj();
            JSONArray andWhere = getWhereAndJson(element);
            paramJson.put("where", JSONUtil.createObj().put("and", andWhere))
                    .put("isDelete", true);
            res = deleteJsonObject(paramJson, HbTablesId.team_name);
        }
        System.out.println(String.format("正在删除班组节点：%s %b", element.elementText("RANK"), res));

        return res;
    }

    private JSONArray getWhereAndJson(Element element) {
        return JSONUtil.createArray().put(JSONUtil.createObj().put("field", team_name.getTeam_Code().getField_id())
                .put("query", JSONUtil.createObj().put("eq", JSONUtil.createArray().put(getOrgNodeName(element, "RANK")))))
                .put(JSONUtil.createObj().put("field", team_name.getCompany_Name().getField_id()).put("query", JSONUtil.createObj()
                        .put("eq", JSONUtil.createArray().put(Long.valueOf(companyItemId)))))
                .put(JSONUtil.createObj().put("field", team_name.getFir_Depart().getField_id()).put("query", JSONUtil.createObj()
                        .put("eq", JSONUtil.createArray().put(Long.valueOf(firDepartItemId)))))
                .put(JSONUtil.createObj().put("field", team_name.getSec_Depart().getField_id()).put("query", JSONUtil.createObj()
                        .put("eq", JSONUtil.createArray().put(Long.valueOf(secDepartItemId)))))
                .put(JSONUtil.createObj().put("field", team_name.getT_Class().getField_id()).put("query", JSONUtil.createObj()
                        .put("eq", JSONUtil.createArray().put(Long.valueOf(subSectionItemId)))))
                .put(JSONUtil.createObj().put("field", team_name.getGroup().getField_id()).put("query", JSONUtil.createObj()
                        .put("eq", JSONUtil.createArray().put(Long.valueOf(groupItemId)))));
    }

    @Override
    public String getCacheItemId(Element element, Boolean isDelete) {
        getOrgItemsId(element, isDelete);
        JSONObject paramJson = JSONUtil.createObj().put("tableId", HbTablesId.team_name)
                .put("field_id", ((Team_Name) tableStuckCache.get("team_name")).getTeam_Code().getField_id())
                .put("field_value", getOrgNodeName(element, "RANK"))
                .put("fieldCnName", getOrgNodeName(element, "RANK_DESCRIPTION"))
                .put("isDelete", isDelete);
        Map<String, Object> itemFieldAndCnName = getLocalItemId(paramJson, element);
        JSONArray andWhere = getWhereAndJson(element);
        String teamItemId = getRemoteItem(element, paramJson, itemFieldAndCnName, andWhere, this, false);

//        String teamItemId = getCacheItemsId(JSONUtil.createObj().put("tableId", HbTablesId.team_name)
//                .put("field_id", ((Team_Name) tableStuckCache.get("team_name")).getTeam_Code().getField_id())
//                .put("field_value", getOrgNodeName(element, "RANK"))
//                .put("fieldCnName", getOrgNodeName(element, "RANK_DESCRIPTION")), this, element, false);
        return teamItemId;
    }

    @Override
    public List<Team_Name> readStringXml(String xml) {
        List<Team_Name> team_names = new ArrayList<>();
        try {
            Document doc = null;
            // 将字符串转为XML
            doc = DocumentHelper.parseText(xml.replace(xml.substring(0, xml.indexOf("?>") + 2), ""));
            // 获取根节点
            Element rootElt = doc.getRootElement();
            // 拿到根节点的名称
            System.out.println("根节点：" + rootElt.getName());
            // 获取根节点下的子节点STAFF
            Iterator iter = rootElt.elementIterator("STAFF");
            // 遍历STAFF节点
            while (iter.hasNext()) {
                Element recordEle = (Element) iter.next();
                // 获取子节点STAFF下的子节点ROW
                Iterator iters = recordEle.elementIterator("ROW");
                // 遍历ROW节点下的Response节点
                Company companyStruc = (Company) tableStuckCache.get("company");

                while (iters.hasNext()) {
                    Element element = (Element) iters.next();
                    if ("".equals(StrUtil.nullToDefault(element.elementText("BRANCH"), ""))) {
                        continue;
                    }
                    if (!"".equals(StrUtil.nullToDefault(element.elementText("INVALID_DATE"), ""))) {
                        boolean rankIsEndOrg = deleteTable(TeamNameImpl.class, element);
                        if (Convert.toInt(element.elementText("CITY_END")) == 1) {
                            boolean groupIsEndOrg = deleteTable(GroupImpl.class, element);
                        }
                        if (Convert.toInt(element.elementText("SUB_SECTION_END")) == 1) {
                            boolean keClassIsEndOrg = deleteTable(KeClassImpl.class, element);
                        }
                        if (Convert.toInt(element.elementText("SECTION_END")) == 1) {
                            boolean secDepIsEndOrg = deleteTable(Sec_DepartImpl.class, element);
                        }
                        if (Convert.toInt(element.elementText("SECTION_END")) == 1) {
                            boolean firDepIsEndOrg = deleteTable(FirDepartMentImpl.class, element);
                        }

                    } else {
                        String companyItemId = new CompanyImpl().getCacheItemId(element, false);
                        String firDepartMentItemId = new FirDepartMentImpl().getCacheItemId(element, false);
                        String sectionItemId = new Sec_DepartImpl().getCacheItemId(element, false);
                        String sub_sectionItemId = new KeClassImpl().getCacheItemId(element, false);
                        String cityItemId = new GroupImpl().getCacheItemId(element, false);
                        String teamItemId = new TeamNameImpl().getCacheItemId(element, false);
                        String postItemId = new PostNameImpl().getCacheItemId(element, false);
                        System.out.println(companyItemId + "-" + firDepartMentItemId + "-" + sectionItemId + "-" + sub_sectionItemId + "-" + cityItemId + "-" + teamItemId);
                    }
                }
            }

        } catch (DocumentException e) {
            XxlJobLogger.log(e.getMessage());
        } catch (Exception e) {
            XxlJobLogger.log(e.getMessage());
        } finally {

        }
        return team_names;
    }

    @Override
    public JSONObject insertTable(Element element) {
        String tableId = HbTablesId.team_name;
        String firDepartCode = getOrgNodeName(element, "DEPARTMENT");
        String sec_depart_code = getOrgNodeName(element, "SECTION");
        String subSectionCode = getOrgNodeName(element, "SUB_SECTION");
        String groupCode = getOrgNodeName(element, "CITY");
        team_name = (Team_Name) tableStuckCache.get(rankStruc);
        JSONObject paramJson = JSONUtil.createObj().put("fields", JSONUtil.createObj().
                put(team_name.getCompany_Name().getField_id(),
                        JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(CompanyImpl.companyItemsId))).get(element.elementText("BRANCH"))).get("itemId")))
                .put(team_name.getFir_Depart().getField_id(), JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(FirDepartMentImpl.firDpartItemsId))).get(firDepartCode)).get("itemId")))
                .put(team_name.getSec_Depart().getField_id(), JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(Sec_DepartImpl.secDepartItemsId))).get(sec_depart_code)).get("itemId")))
                .put(team_name.getT_Class().getField_id(), JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(KeClassImpl.keClassItemsId))).get(subSectionCode)).get("itemId")))
                .put(team_name.getGroup().getField_id(), JSONUtil.createArray().put(((Map) ((Map) (tableStuckCache.get(GroupImpl.groupItemsId))).get(groupCode)).get("itemId")))
                .put(team_name.getTeam_Code().getField_id(), getOrgNodeName(element, "RANK"))
                .put(team_name.getTeam_Name().getField_id(), getOrgNodeName(element, "RANK_DESCRIPTION"))
                .put(team_name.getLeaders().getField_id(), element.elementText("")));
        JSONObject reuslt = insertTable(paramJson, tableId);
        return reuslt;
    }

    @Override
    public JSONObject updateTable(JSONObject jsonObject, Element element) {
        JSONObject resultJson = JSONUtil.createObj();
        String fieldCnName = jsonObject.getStr("fieldCnName");
        resultJson.put("fieldCnName", fieldCnName);
        //如果是获取人员信息的时候来查询组织就不需要去更新组织，直接返回即可
        if (!StrUtil.isAllBlank(element.elementText("STAFF_NO"))) {
            return resultJson.put("rspStatus", 0);
        }
        getOrgItemsId(element, false);
        team_name = (Team_Name) tableStuckCache.get("team_name");
//        List<JSONObject> jsonArray = (List<JSONObject>) ((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("fields");
//        String cnName = ((JSONObject) ((JSONArray) jsonArray.stream().filter(p -> p.getStr("field_id").
//                equals(team_name.getTeam_Name().getField_id())).findFirst().get().get("values")).get(0)).get("value").toString();

        String itemId = ((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("item_id").toString();
        JSONObject dataJson = JSONUtil.createObj().put(team_name.getTeam_Name().getField_id(), fieldCnName)
                .put(team_name.getCompany_Name().getField_id(), JSONUtil.createArray().put(companyItemId))
                .put(team_name.getFir_Depart().getField_id(), JSONUtil.createArray().put(firDepartItemId))
                .put(team_name.getSec_Depart().getField_id(), JSONUtil.createArray().put(secDepartItemId))
                .put(team_name.getT_Class().getField_id(), JSONUtil.createArray().put(subSectionItemId))
                .put(team_name.getGroup().getField_id(), JSONUtil.createArray().put(groupItemId));
        //如果本节点是最后一个组织节点，更新本组织负责人员
        if (isEndOrg(element) && !StrUtil.isBlank(element.elementText("Function_Leader"))) {
            String leaderItemId = getLeaderItemId(element);
            dataJson.put(team_name.getLeaders().getField_id(), JSONUtil.createArray().put(leaderItemId));
        }
        JSONObject paramJson = JSONUtil.createObj().put("item_ids", itemId).put("filter", JSONUtil.createObj().put("and",
                JSONUtil.createArray().put(JSONUtil.createObj().put("field", team_name.getTeam_Code().getField_id())
                        .put("query", JSONUtil.createObj().put("eq", jsonObject.getStr("field_code"))))))
                .put("data", dataJson);

        //更新数据
        resultJson.put("rspStatus", updateTable(HbTablesId.team_name, paramJson));
        return resultJson;
    }

    private boolean isEndOrg(Element element) {
        boolean result = !("A9999".equals(element.elementText("RANK")) || "".equals(element.elementText("RANK")));
        return result;
    }

    /**
     * 获取所有父节点的ItemsID
     *
     * @param element
     */
    public Boolean getOrgItemsId(Element element, Boolean isDelete) {
        if (StrUtil.isAllBlank(element.elementText("STAFF_NO"))) {
            companyItemId = new CompanyImpl().getCacheItemId(element, isDelete);
            if ("".equals(companyItemId)) {
                return false;
            }
            firDepartItemId = new FirDepartMentImpl().getCacheItemId(element, isDelete);
            if ("".equals(firDepartItemId)) {
                return false;
            }
            secDepartItemId = new Sec_DepartImpl().getCacheItemId(element, isDelete);
            if ("".equals(secDepartItemId)) {
                return false;
            }
            subSectionItemId = new KeClassImpl().getCacheItemId(element, isDelete);
            if ("".equals(subSectionItemId)) {
                return false;
            }
            groupItemId = new GroupImpl().getCacheItemId(element, isDelete);
            if ("".equals(groupItemId)) {
                return false;
            }
        } else {
            companyItemId = ((Map) ((Map) (tableStuckCache.get(CompanyImpl.companyItemsId))).
                    get(element.elementText("BRANCH"))).get("itemId").toString();
            firDepartItemId = ((Map) ((Map) (tableStuckCache.get(FirDepartMentImpl.firDpartItemsId))).
                    get(getOrgNodeName(element, "DEPARTMENT"))).get("itemId").toString();
            secDepartItemId = ((Map) ((Map) (tableStuckCache.get(Sec_DepartImpl.secDepartItemsId))).
                    get(getOrgNodeName(element, "SECTION"))).get("itemId").toString();
            subSectionItemId = ((Map) ((Map) (tableStuckCache.get(Sec_DepartImpl.secDepartItemsId))).
                    get(getOrgNodeName(element, "SUB_SECTION"))).get("itemId").toString();
            groupItemId = ((Map) ((Map) (tableStuckCache.get(Sec_DepartImpl.secDepartItemsId))).
                    get(getOrgNodeName(element, "CITY"))).get("itemId").toString();
        }

        return true;
    }

    /**
     * 设置字段与字段ID的映射关系
     *
     * @param jsonObject
     * @param team_name
     */
    static void setFildsMap(JSONObject jsonObject, Team_Name team_name) {
        for (Object objects : (JSONArray) jsonObject.get("fields")
                ) {
            JSONObject field_id = ((JSONObject) objects);
            KeyValueModel valueModel = new KeyValueModel();
            valueModel.setField_id(field_id.get("field_id").toString());
            switch (field_id.get("alias").toString()) {
                case "team.company_name":
                    team_name.setCompany_Name(valueModel);
                    break;
                case "team.fir_depart":
                    team_name.setFir_Depart(valueModel);
                    break;
                case "team.sec_depart":
                    team_name.setSec_Depart(valueModel);
                    break;
                case "team.class":
                    team_name.setT_Class(valueModel);
                    break;
                case "team.group":
                    team_name.setGroup(valueModel);
                    break;
                case "team.team_name":
                    team_name.setTeam_Name(valueModel);
                    break;
                case "team.team_code":
                    team_name.setTeam_Code(valueModel);
                    break;
                case "team.leaders":
                    team_name.setLeaders(valueModel);
                    break;
                default:
                    break;
            }
        }
    }


}
