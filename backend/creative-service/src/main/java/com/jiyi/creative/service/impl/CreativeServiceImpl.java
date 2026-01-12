package com.jiyi.creative.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiyi.common.exception.BusinessException;
import com.jiyi.creative.dto.*;
import com.jiyi.creative.entity.*;
import com.jiyi.creative.mapper.*;
import com.jiyi.creative.service.CreativeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreativeServiceImpl implements CreativeService {
    
    private final ContestMapper contestMapper;
    private final CreativeCallMapper creativeCallMapper;
    private final DesignMapper designMapper;
    private final VoteRecordMapper voteRecordMapper;
    private final RewardRecordMapper rewardRecordMapper;
    private final DesignerProfileMapper designerProfileMapper;
    private final DesignRequirementMapper designRequirementMapper;
    private final DesignerMatchMapper designerMatchMapper;
    private final ObjectMapper objectMapper;
    
    @Override
    public CreativeSpaceVO getCreativeSpace() {
        CreativeSpaceVO vo = new CreativeSpaceVO();
        
        // 获取进行中的大赛
        List<Contest> contests = contestMapper.selectList(
            new LambdaQueryWrapper<Contest>()
                .in(Contest::getStatus, "ongoing", "voting")
                .orderByDesc(Contest::getCreatedAt)
                .last("LIMIT 5")
        );
        vo.setContests(contests.stream().map(this::convertToContestVO).collect(Collectors.toList()));
        
        // 获取开放的创意征集
        List<CreativeCall> calls = creativeCallMapper.selectList(
            new LambdaQueryWrapper<CreativeCall>()
                .eq(CreativeCall::getStatus, "open")
                .orderByDesc(CreativeCall::getCreatedAt)
                .last("LIMIT 5")
        );
        vo.setCreativeCalls(calls.stream().map(this::convertToCreativeCallVO).collect(Collectors.toList()));
        
        // 获取精选作品
        List<Design> designs = designMapper.selectList(
            new LambdaQueryWrapper<Design>()
                .eq(Design::getStatus, "published")
                .orderByDesc(Design::getVotes)
                .last("LIMIT 10")
        );
        vo.setFeaturedDesigns(designs.stream().map(d -> convertToDesignVO(d, null)).collect(Collectors.toList()));
        
        return vo;
    }
    
    @Override
    public List<ContestVO> getContests() {
        List<Contest> contests = contestMapper.selectList(
            new LambdaQueryWrapper<Contest>()
                .orderByDesc(Contest::getCreatedAt)
        );
        return contests.stream().map(this::convertToContestVO).collect(Collectors.toList());
    }
    
    @Override
    public ContestVO getContestById(Long contestId) {
        Contest contest = contestMapper.selectById(contestId);
        if (contest == null) {
            throw new BusinessException("大赛不存在");
        }
        return convertToContestVO(contest);
    }
    
    @Override
    public List<CreativeCallVO> getCreativeCalls() {
        List<CreativeCall> calls = creativeCallMapper.selectList(
            new LambdaQueryWrapper<CreativeCall>()
                .orderByDesc(CreativeCall::getCreatedAt)
        );
        return calls.stream().map(this::convertToCreativeCallVO).collect(Collectors.toList());
    }
    
    @Override
    public CreativeCallVO getCreativeCallById(Long callId) {
        CreativeCall call = creativeCallMapper.selectById(callId);
        if (call == null) {
            throw new BusinessException("创意征集不存在");
        }
        return convertToCreativeCallVO(call);
    }
    
    @Override
    @Transactional
    public DesignVO submitDesign(Long userId, DesignSubmitRequest request) {
        log.info("提交设计作品 - 用户ID: {}, 请求数据: {}", userId, request);
        
        // 将空字符串转换为 null
        if (request.getDesignConcept() != null && request.getDesignConcept().trim().isEmpty()) {
            request.setDesignConcept(null);
        }
        if (request.getCoverImage() != null && request.getCoverImage().trim().isEmpty()) {
            request.setCoverImage(null);
        }
        if (request.getCopyrightStatement() != null && request.getCopyrightStatement().trim().isEmpty()) {
            request.setCopyrightStatement(null);
        }
        if (request.getTags() != null && request.getTags().trim().isEmpty()) {
            request.setTags(null);
        }
        
        // 使用 UpdateWrapper 手动构建 INSERT 语句，只包含非 null 字段
        Design design = new Design();
        design.setDesignerId(userId);
        design.setTitle(request.getTitle());
        design.setDescription(request.getDescription());
        design.setStatus("pending");
        design.setVotes(0);
        design.setViews(0);
        
        // 处理文件列表
        try {
            String filesJson = objectMapper.writeValueAsString(request.getFiles());
            design.setFiles(filesJson);
        } catch (Exception e) {
            log.error("文件列表序列化失败", e);
            throw new BusinessException("文件列表格式错误: " + e.getMessage());
        }
        
        // 可选字段 - 只在非 null 时设置
        if (request.getContestId() != null) {
            design.setContestId(request.getContestId());
        }
        if (request.getCallId() != null) {
            design.setCallId(request.getCallId());
        }
        if (request.getCategoryType() != null) {
            design.setCategoryType(request.getCategoryType());
        }
        if (request.getDesignConcept() != null) {
            design.setDesignConcept(request.getDesignConcept());
        }
        if (request.getCoverImage() != null) {
            design.setCoverImage(request.getCoverImage());
        }
        if (request.getCopyrightStatement() != null) {
            design.setCopyrightStatement(request.getCopyrightStatement());
        }
        if (request.getTags() != null) {
            design.setTags(request.getTags());
        }
        
        log.info("准备插入的 Design 对象: designerId={}, title={}, categoryType={}, description={}, designConcept={}, files={}, coverImage={}, copyrightStatement={}, tags={}", 
            design.getDesignerId(), design.getTitle(), design.getCategoryType(), design.getDescription(), 
            design.getDesignConcept(), design.getFiles(), design.getCoverImage(), design.getCopyrightStatement(), design.getTags());
        
        try {
            // 使用自定义的 insertSelective 方法，只插入非 null 字段
            designMapper.insertSelective(design);
            log.info("作品插入成功 - ID: {}", design.getId());
        } catch (Exception e) {
            log.error("作品插入失败", e);
            throw new BusinessException("作品保存失败: " + e.getMessage());
        }
        
        // 更新参赛人数或投稿数量
        if (request.getContestId() != null) {
            Contest contest = contestMapper.selectById(request.getContestId());
            if (contest != null) {
                contest.setParticipantCount(contest.getParticipantCount() + 1);
                contestMapper.updateById(contest);
            }
        }
        
        if (request.getCallId() != null) {
            CreativeCall call = creativeCallMapper.selectById(request.getCallId());
            if (call != null) {
                call.setSubmissionCount(call.getSubmissionCount() + 1);
                creativeCallMapper.updateById(call);
            }
        }
        
        return convertToDesignVO(design, userId);
    }
    
    @Override
    public DesignVO getDesignById(Long designId, Long userId) {
        Design design = designMapper.selectById(designId);
        if (design == null) {
            throw new BusinessException("作品不存在");
        }
        
        // 增加浏览量
        design.setViews(design.getViews() + 1);
        designMapper.updateById(design);
        
        return convertToDesignVO(design, userId);
    }
    
    @Override
    public List<DesignVO> getDesignsByContest(Long contestId, Long userId) {
        List<Design> designs = designMapper.selectList(
            new LambdaQueryWrapper<Design>()
                .eq(Design::getContestId, contestId)
                .in(Design::getStatus, "approved", "published")
                .orderByDesc(Design::getVotes)
        );
        return designs.stream().map(d -> convertToDesignVO(d, userId)).collect(Collectors.toList());
    }
    
    @Override
    public List<DesignVO> getDesignsByCall(Long callId, Long userId) {
        List<Design> designs = designMapper.selectList(
            new LambdaQueryWrapper<Design>()
                .eq(Design::getCallId, callId)
                .in(Design::getStatus, "approved", "published")
                .orderByDesc(Design::getVotes)
        );
        return designs.stream().map(d -> convertToDesignVO(d, userId)).collect(Collectors.toList());
    }
    
    @Override
    public List<DesignVO> getMyDesigns(Long userId) {
        List<Design> designs = designMapper.selectList(
            new LambdaQueryWrapper<Design>()
                .eq(Design::getDesignerId, userId)
                .orderByDesc(Design::getCreatedAt)
        );
        return designs.stream().map(d -> convertToDesignVO(d, userId)).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void reviewDesign(Long designId, DesignReviewRequest request) {
        Design design = designMapper.selectById(designId);
        if (design == null) {
            throw new BusinessException("作品不存在");
        }
        
        if ("approved".equals(request.getStatus())) {
            design.setStatus("published");
            design.setRejectReason(null);
        } else if ("rejected".equals(request.getStatus())) {
            design.setStatus("rejected");
            design.setRejectReason(request.getRejectReason());
        }
        
        designMapper.updateById(design);
    }
    
    @Override
    @Transactional
    public void voteDesign(Long userId, Long designId) {
        // 检查是否已投票
        Long count = voteRecordMapper.selectCount(
            new LambdaQueryWrapper<VoteRecord>()
                .eq(VoteRecord::getUserId, userId)
                .eq(VoteRecord::getDesignId, designId)
        );
        
        if (count > 0) {
            throw new BusinessException("您已经投过票了");
        }
        
        // 创建投票记录
        VoteRecord record = new VoteRecord();
        record.setUserId(userId);
        record.setDesignId(designId);
        voteRecordMapper.insert(record);
        
        // 增加作品票数
        Design design = designMapper.selectById(designId);
        if (design != null) {
            design.setVotes(design.getVotes() + 1);
            designMapper.updateById(design);
        }
    }
    
    @Override
    @Transactional
    public void unvoteDesign(Long userId, Long designId) {
        // 删除投票记录
        voteRecordMapper.delete(
            new LambdaQueryWrapper<VoteRecord>()
                .eq(VoteRecord::getUserId, userId)
                .eq(VoteRecord::getDesignId, designId)
        );
        
        // 减少作品票数
        Design design = designMapper.selectById(designId);
        if (design != null && design.getVotes() > 0) {
            design.setVotes(design.getVotes() - 1);
            designMapper.updateById(design);
        }
    }
    
    @Override
    public List<DesignVO> getTopDesigns(Integer limit) {
        List<Design> designs = designMapper.selectList(
            new LambdaQueryWrapper<Design>()
                .in(Design::getStatus, "pending", "approved", "published")  // 包括 pending 状态
                .orderByDesc(Design::getVotes)
                .orderByDesc(Design::getCreatedAt)  // 按创建时间排序
                .last("LIMIT " + (limit != null ? limit : 10))
        );
        return designs.stream().map(d -> convertToDesignVO(d, null)).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void distributeReward(Long designId, String type, BigDecimal amount) {
        Design design = designMapper.selectById(designId);
        if (design == null) {
            throw new BusinessException("作品不存在");
        }
        
        RewardRecord record = new RewardRecord();
        record.setDesignerId(design.getDesignerId());
        record.setDesignId(designId);
        record.setContestId(design.getContestId());
        record.setCallId(design.getCallId());
        record.setType(type);
        record.setAmount(amount);
        record.setStatus("pending");
        
        rewardRecordMapper.insert(record);
    }
    
    @Override
    public List<DesignerVO> matchDesigners(Long requirementId) {
        DesignRequirement requirement = designRequirementMapper.selectById(requirementId);
        if (requirement == null) {
            throw new BusinessException("需求不存在");
        }
        
        // 简单匹配算法：根据设计师评分和完成项目数
        List<DesignerProfile> profiles = designerProfileMapper.selectList(
            new LambdaQueryWrapper<DesignerProfile>()
                .eq(DesignerProfile::getVerified, 1)
                .orderByDesc(DesignerProfile::getRating)
                .orderByDesc(DesignerProfile::getCompletedProjects)
                .last("LIMIT 10")
        );
        
        return profiles.stream().map(profile -> {
            DesignerVO vo = new DesignerVO();
            BeanUtils.copyProperties(profile, vo);
            
            // 解析技能列表
            try {
                List<String> skills = objectMapper.readValue(
                    profile.getSkills(), 
                    new TypeReference<List<String>>() {}
                );
                vo.setSkills(skills);
            } catch (Exception e) {
                vo.setSkills(new ArrayList<>());
            }
            
            // 计算匹配分数（简化版）
            BigDecimal matchScore = profile.getRating()
                .multiply(BigDecimal.valueOf(0.6))
                .add(BigDecimal.valueOf(profile.getCompletedProjects()).multiply(BigDecimal.valueOf(0.4)));
            vo.setMatchScore(matchScore);
            
            return vo;
        }).collect(Collectors.toList());
    }
    
    private ContestVO convertToContestVO(Contest contest) {
        ContestVO vo = new ContestVO();
        BeanUtils.copyProperties(contest, vo);
        return vo;
    }
    
    private CreativeCallVO convertToCreativeCallVO(CreativeCall call) {
        CreativeCallVO vo = new CreativeCallVO();
        BeanUtils.copyProperties(call, vo);
        return vo;
    }
    
    private DesignVO convertToDesignVO(Design design, Long userId) {
        DesignVO vo = new DesignVO();
        BeanUtils.copyProperties(design, vo);
        
        // 解析文件列表
        try {
            List<String> files = objectMapper.readValue(
                design.getFiles(), 
                new TypeReference<List<String>>() {}
            );
            vo.setFiles(files);
        } catch (Exception e) {
            vo.setFiles(new ArrayList<>());
        }
        
        // 检查用户是否已投票
        if (userId != null) {
            Long count = voteRecordMapper.selectCount(
                new LambdaQueryWrapper<VoteRecord>()
                    .eq(VoteRecord::getUserId, userId)
                    .eq(VoteRecord::getDesignId, design.getId())
            );
            vo.setHasVoted(count > 0);
        }
        
        return vo;
    }
}
