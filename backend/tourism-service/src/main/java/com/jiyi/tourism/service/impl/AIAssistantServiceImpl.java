package com.jiyi.tourism.service.impl;

import com.jiyi.tourism.dto.AIQueryRequest;
import com.jiyi.tourism.dto.AIQueryResponse;
import com.jiyi.tourism.service.AIAssistantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * AI智能助手服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIAssistantServiceImpl implements AIAssistantService {
    
    // 知识库 - 实际应用中应该使用向量数据库或AI服务
    private static final Map<String, String> KNOWLEDGE_BASE = new HashMap<>();
    
    static {
        KNOWLEDGE_BASE.put("西柏坡", "西柏坡纪念馆位于河北省石家庄市平山县，是解放战争时期中央工委、中共中央和解放军总部的所在地。必看景点包括：七届二中全会会址、毛泽东旧居、中央军委作战室、西柏坡陈列馆。建议游览时间2-3小时，免费参观需提前预约。");
        KNOWLEDGE_BASE.put("狼牙山", "狼牙山位于河北省保定市易县，因奇峰林立状若狼牙而得名。这里是狼牙山五壮士英雄事迹发生地。主要景点有棋盘陀、五壮士纪念塔、五壮士陈列馆。建议游览时间3-4小时，山路较陡注意安全。");
        KNOWLEDGE_BASE.put("白洋淀", "白洋淀是华北平原最大的淡水湖泊，素有'华北明珠'之称。抗战时期雁翎队在此创造了水上游击战传奇。主要景点有雁翎队纪念馆、荷花大观园、嘎子村。夏季荷花盛开最美，可乘船游览。");
        KNOWLEDGE_BASE.put("塞罕坝", "塞罕坝位于河北省承德市围场县，是世界上面积最大的人工林场。三代塞罕坝人用55年时间将荒原变成百万亩林海。主要景点有塞罕坝展览馆、望海楼、七星湖湿地。夏季凉爽是避暑胜地，秋季色彩斑斓。");
        KNOWLEDGE_BASE.put("美食", "河北红色旅游沿线特色美食：平山缸炉烧饼、驴肉火烧、抿须面、承德烤全羊、白洋淀炖杂鱼、坝上莜面。人均消费30-60元。");
        KNOWLEDGE_BASE.put("一日游", "推荐一日游路线：上午西柏坡纪念馆（2-3小时），中午品尝当地农家菜，下午石家庄解放纪念馆（1-2小时）。预计费用约200元/人。");
        KNOWLEDGE_BASE.put("两日游", "推荐两日游路线：第一天西柏坡+石家庄市区，第二天狼牙山或白洋淀。预计费用约500-800元/人。");
    }
    
    @Override
    public AIQueryResponse chat(AIQueryRequest request, Long userId) {
        String question = request.getQuestion().toLowerCase();
        AIQueryResponse response = new AIQueryResponse();
        response.setSessionId(request.getSessionId() != null ? request.getSessionId() : UUID.randomUUID().toString());
        
        // 简单的关键词匹配 - 实际应用中应该使用NLP或AI模型
        String answer = generateAnswer(question);
        response.setAnswer(answer);
        response.setType(determineResponseType(question));
        response.setSuggestions(generateSuggestions(question));
        
        // 如果是路线相关问题，添加路线推荐
        if (question.contains("路线") || question.contains("游") || question.contains("推荐")) {
            response.setRouteRecommendation(generateRouteRecommendation(question));
        }
        
        return response;
    }
    
    @Override
    public List<String> getSuggestions(String context) {
        List<String> suggestions = new ArrayList<>();
        
        if (context == null || context.isEmpty()) {
            suggestions.add("西柏坡有什么必看景点？");
            suggestions.add("推荐一日游路线");
            suggestions.add("附近有什么美食？");
            suggestions.add("现在去塞罕坝合适吗？");
            suggestions.add("狼牙山怎么去？");
        } else if (context.contains("西柏坡")) {
            suggestions.add("西柏坡门票多少钱？");
            suggestions.add("西柏坡附近住宿推荐");
            suggestions.add("西柏坡到狼牙山怎么走？");
        } else if (context.contains("美食")) {
            suggestions.add("平山有什么特色小吃？");
            suggestions.add("白洋淀有什么好吃的？");
            suggestions.add("承德烤全羊哪家好？");
        }
        
        return suggestions;
    }
    
    @Override
    public AIQueryResponse recommendRoute(AIQueryRequest request, Long userId) {
        AIQueryResponse response = new AIQueryResponse();
        response.setType("route");
        
        int days = request.getDays() != null ? request.getDays() : 1;
        int budget = request.getBudget() != null ? request.getBudget() : 500;
        
        AIQueryResponse.RouteRecommendation recommendation = new AIQueryResponse.RouteRecommendation();
        
        if (days == 1) {
            recommendation.setTitle("西柏坡红色经典一日游");
            recommendation.setDescription("探访新中国从这里走来的革命圣地");
            recommendation.setSpots(Arrays.asList("西柏坡纪念馆", "七届二中全会会址", "毛泽东旧居", "石家庄解放纪念馆"));
            recommendation.setDuration("1天");
            recommendation.setEstimatedCost(200);
            recommendation.setHighlights(Arrays.asList("三大战役指挥中心", "两个务必发源地", "新中国从这里走来"));
        } else if (days == 2) {
            recommendation.setTitle("太行山红色生态两日游");
            recommendation.setDescription("红色文化与自然风光的完美结合");
            recommendation.setSpots(Arrays.asList("西柏坡纪念馆", "狼牙山五壮士纪念地", "白洋淀雁翎队纪念馆"));
            recommendation.setDuration("2天");
            recommendation.setEstimatedCost(600);
            recommendation.setHighlights(Arrays.asList("西柏坡精神", "狼牙山五壮士", "雁翎队传奇"));
        } else {
            recommendation.setTitle("河北红色文化深度游");
            recommendation.setDescription("全面体验河北红色旅游资源");
            recommendation.setSpots(Arrays.asList("西柏坡纪念馆", "狼牙山", "白洋淀", "塞罕坝", "一二九师司令部"));
            recommendation.setDuration(days + "天");
            recommendation.setEstimatedCost(days * 300);
            recommendation.setHighlights(Arrays.asList("革命圣地", "英雄故事", "生态文明", "太行精神"));
        }
        
        response.setRouteRecommendation(recommendation);
        response.setAnswer("根据您的需求，为您推荐以下路线：" + recommendation.getTitle());
        response.setSuggestions(Arrays.asList("查看详细行程", "修改天数", "调整预算"));
        
        return response;
    }
    
    @Override
    public AIQueryResponse spotQA(Long spotId, String question) {
        AIQueryResponse response = new AIQueryResponse();
        
        Map<Long, String> spotNames = new HashMap<>();
        spotNames.put(1L, "西柏坡");
        spotNames.put(2L, "狼牙山");
        spotNames.put(3L, "白洋淀");
        spotNames.put(4L, "塞罕坝");
        
        String spotName = spotNames.getOrDefault(spotId, "该景点");
        String knowledge = KNOWLEDGE_BASE.getOrDefault(spotName, "");
        
        if (question.contains("门票") || question.contains("票价")) {
            response.setAnswer(spotName + "门票信息：西柏坡纪念馆免费（需预约），狼牙山门票80元，白洋淀门票40元+船票，塞罕坝门票130元。");
        } else if (question.contains("交通") || question.contains("怎么去")) {
            response.setAnswer("前往" + spotName + "的交通方式：建议自驾或包车，也可乘坐旅游专线。从石家庄出发约1-2小时车程。");
        } else if (question.contains("住宿") || question.contains("酒店")) {
            response.setAnswer(spotName + "周边住宿推荐：景区附近有多家农家院和酒店，价格100-300元/晚不等。建议提前预订。");
        } else {
            response.setAnswer(knowledge.isEmpty() ? "关于" + spotName + "的更多信息，请咨询景区服务中心。" : knowledge);
        }
        
        response.setType("spot_info");
        response.setSuggestions(Arrays.asList(spotName + "门票多少钱？", spotName + "怎么去？", spotName + "附近住宿"));
        
        return response;
    }
    
    private String generateAnswer(String question) {
        // 关键词匹配
        for (Map.Entry<String, String> entry : KNOWLEDGE_BASE.entrySet()) {
            if (question.contains(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }
        
        // 通用问题处理
        if (question.contains("天气") || question.contains("穿")) {
            return "当前天气：晴，3-12℃。建议穿着保暖外套和舒适的运动鞋，山区早晚温差大，注意保暖。";
        }
        
        if (question.contains("门票") || question.contains("票价")) {
            return "河北红色景点门票信息：西柏坡纪念馆免费（需预约），狼牙山80元，白洋淀40元+船票，塞罕坝130元。建议提前在官方平台预约。";
        }
        
        if (question.contains("交通") || question.contains("怎么去")) {
            return "河北红色旅游交通建议：建议自驾或包车，灵活性高。也可乘坐旅游专线或长途汽车。从石家庄出发到各景点约1-3小时车程。";
        }
        
        return "感谢您的提问！关于河北红色旅游，我可以为您提供路线规划、景点介绍、美食推荐、交通指南等信息。请告诉我您具体想了解什么？";
    }
    
    private String determineResponseType(String question) {
        if (question.contains("路线") || question.contains("游") || question.contains("推荐")) {
            return "route";
        }
        if (question.contains("美食") || question.contains("吃")) {
            return "food";
        }
        if (question.contains("天气")) {
            return "weather";
        }
        return "text";
    }
    
    private List<String> generateSuggestions(String question) {
        List<String> suggestions = new ArrayList<>();
        
        if (question.contains("西柏坡")) {
            suggestions.add("西柏坡门票多少钱？");
            suggestions.add("西柏坡附近有什么美食？");
            suggestions.add("从西柏坡到狼牙山怎么走？");
        } else if (question.contains("美食")) {
            suggestions.add("平山有什么特色小吃？");
            suggestions.add("推荐一家农家院");
        } else {
            suggestions.add("推荐一日游路线");
            suggestions.add("附近有什么美食？");
            suggestions.add("现在天气怎么样？");
        }
        
        return suggestions;
    }
    
    private AIQueryResponse.RouteRecommendation generateRouteRecommendation(String question) {
        AIQueryResponse.RouteRecommendation recommendation = new AIQueryResponse.RouteRecommendation();
        
        if (question.contains("一日") || question.contains("1天")) {
            recommendation.setTitle("西柏坡红色经典一日游");
            recommendation.setSpots(Arrays.asList("西柏坡纪念馆", "七届二中全会会址", "毛泽东旧居"));
            recommendation.setDuration("1天");
            recommendation.setEstimatedCost(200);
        } else {
            recommendation.setTitle("河北红色文化精华游");
            recommendation.setSpots(Arrays.asList("西柏坡纪念馆", "狼牙山", "白洋淀"));
            recommendation.setDuration("2-3天");
            recommendation.setEstimatedCost(600);
        }
        
        recommendation.setDescription("根据您的需求推荐的路线");
        recommendation.setHighlights(Arrays.asList("革命圣地", "英雄故事", "红色文化"));
        
        return recommendation;
    }
}
