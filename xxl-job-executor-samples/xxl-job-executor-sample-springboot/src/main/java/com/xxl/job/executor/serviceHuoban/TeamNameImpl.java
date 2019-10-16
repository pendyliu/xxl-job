package com.xxl.job.executor.serviceHuoban;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.Models.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.*;

import static com.xxl.job.executor.service.jobhandler.PersonHbJobHandler.tableStuckCache;
import static com.xxl.job.executor.serviceHuoban.StaffInfoImpl.staffInfoTbStruc;

/**
 * @author pendy
 */
public class TeamNameImpl extends BaseHuoBanServ implements IHuoBanService {
    static Team_Name team_name = new Team_Name();
    public static String rankItemsId = "rankItemsId";
    public static String rankStruc = "team_name";

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
    public Map<String, Object> getItemId(JSONObject paramJson, Element element) {
//        try {
//            JSONArray andWhere = JSONArray.class.newInstance().put(JSONUtil.createObj().put("field", paramJson.get("field_id"))
//                    .put("query", JSONUtil.createObj().put("eq", paramJson.get("field_value"))));
//            paramJson.put("where", JSONUtil.createObj().put("and", andWhere)).remove("field_id");
//        } catch (Exception e) {
//            XxlJobLogger.log(e.getMessage());
//        }
//        return getItemsId(paramJson, this, element);
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
    public String getCacheItemId(Element element) {
        String teamItemId = getCacheItemsId(JSONUtil.createObj().put("tableId", HbTablesId.team_name)
                .put("field_id", ((Team_Name) tableStuckCache.get("team_name")).getTeam_Code().getField_id())
                .put("field_value", getOrgNodeName(element, "RANK"))
                .put("fieldCnName", getOrgNodeName(element, "RANK_DESCRIPTION")), this, element);
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
                    String companyItemId = new CompanyImpl().getCacheItemId(element);
                    String firDepartMentItemId = new FirDepartMentImpl().getCacheItemId(element);
                    String sectionItemId = new Sec_DepartImpl().getCacheItemId(element);
                    String sub_sectionItemId = new KeClassImpl().getCacheItemId(element);
                    String cityItemId = new GroupImpl().getCacheItemId(element);
                    String teamItemId = new TeamNameImpl().getCacheItemId(element);
                    String postItemId = new PostNameImpl().getCacheItemId(element);
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
        team_name = (Team_Name) tableStuckCache.get("team_name");
        List<JSONObject> jsonArray = (List<JSONObject>) ((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("fields");
        String cnName = ((JSONObject) ((JSONArray) jsonArray.stream().filter(p -> p.getStr("field_id").
                equals(team_name.getTeam_Name().getField_id())).findFirst().get().get("values")).get(0)).get("value").toString();
        String fieldCnName = getOrgNodeName(element, "RANK_DESCRIPTION");
        resultJson.put("fieldCnName", fieldCnName);
        if (!cnName.equals(fieldCnName)) {
            String itemId = ((JSONObject) ((JSONArray) jsonObject.get("items")).get(0)).get("item_id").toString();
            JSONObject dataJson = JSONUtil.createObj().put(team_name.getTeam_Name().getField_id(), fieldCnName);
            //如果本节点是最后一个组织节点，更新本组织负责人员
            if (isEndOrg(element) && !StrUtil.isBlank(element.elementText("Function_Leader"))) {
                String leaderItemId = getCacheItemsId(JSONUtil.createObj().put("tableId", HbTablesId.staff_info)
                        .put("field_id", ((Staff_Info) tableStuckCache.get(staffInfoTbStruc)).getStaff_number().getField_id())
                        .put("field_value", element.elementText("Function_Leader")), this, element);
                dataJson.put(team_name.getLeaders().getField_id(), leaderItemId);
            }
            JSONObject paramJson = JSONUtil.createObj().put("item_ids", itemId).put("filter", JSONUtil.createObj().put("and",
                    JSONUtil.createArray().put(JSONUtil.createObj().put("field", team_name.getTeam_Code().getField_id())
                            .put("query", JSONUtil.createObj().put("eq", jsonObject.getStr("field_code"))))))
                    .put("data", JSONUtil.createObj().put(team_name.getTeam_Name().getField_id(), fieldCnName));
            //更新数据
            resultJson.put("rspStatus", updateTable(HbTablesId.team_name, paramJson));
        }
        return resultJson;
    }

    private boolean isEndOrg(Element element) {
        boolean result = !("A9999".equals(element.elementText("RANK")) || "".equals(element.elementText("RANK")));
        return result;
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
