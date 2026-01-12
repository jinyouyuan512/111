package com.jiyi.creative.service;

import com.jiyi.creative.dto.*;
import java.math.BigDecimal;
import java.util.List;

public interface CreativeService {
    // 众创空间首页
    CreativeSpaceVO getCreativeSpace();
    
    // 设计大赛
    List<ContestVO> getContests();
    ContestVO getContestById(Long contestId);
    
    // 创意征集
    List<CreativeCallVO> getCreativeCalls();
    CreativeCallVO getCreativeCallById(Long callId);
    
    // 设计作品
    DesignVO submitDesign(Long userId, DesignSubmitRequest request);
    DesignVO getDesignById(Long designId, Long userId);
    List<DesignVO> getDesignsByContest(Long contestId, Long userId);
    List<DesignVO> getDesignsByCall(Long callId, Long userId);
    List<DesignVO> getMyDesigns(Long userId);
    
    // 作品审核
    void reviewDesign(Long designId, DesignReviewRequest request);
    
    // 投票系统
    void voteDesign(Long userId, Long designId);
    void unvoteDesign(Long userId, Long designId);
    List<DesignVO> getTopDesigns(Integer limit);
    
    // 奖励发放
    void distributeReward(Long designId, String type, BigDecimal amount);
    
    // 设计师匹配
    List<DesignerVO> matchDesigners(Long requirementId);
}
