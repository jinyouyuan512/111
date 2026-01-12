package com.jiyi.experience.dto;

import lombok.Data;
import java.util.List;

/**
 * 排行榜视图对象
 */
@Data
public class LeaderboardVO {
    
    /** 排行榜周期 */
    private String period;
    
    /** 排行榜数据 */
    private List<RankItem> rankings;
    
    /** 当前用户排名 */
    private RankItem currentUserRank;
    
    @Data
    public static class RankItem {
        private Integer rank;
        private Long userId;
        private String username;
        private String avatar;
        private Integer totalPoints;
        private Integer level;
        private String levelName;
    }
}
