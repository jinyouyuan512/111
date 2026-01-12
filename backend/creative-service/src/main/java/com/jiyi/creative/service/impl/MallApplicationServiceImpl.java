package com.jiyi.creative.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiyi.creative.dto.MallApplicationRequest;
import com.jiyi.creative.dto.MallApplicationVO;
import com.jiyi.creative.entity.Design;
import com.jiyi.creative.entity.MallApplication;
import com.jiyi.creative.mapper.DesignMapper;
import com.jiyi.creative.mapper.MallApplicationMapper;
import com.jiyi.creative.service.MallApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MallApplicationServiceImpl implements MallApplicationService {
    
    @Autowired
    private MallApplicationMapper applicationMapper;
    
    @Autowired
    private DesignMapper designMapper;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    @Transactional
    public MallApplication submitApplication(MallApplicationRequest request, Long userId) {
        // 检查作品是否存在
        Design design = designMapper.selectById(request.getDesignId());
        if (design == null) {
            throw new RuntimeException("作品不存在");
        }
        
        // 检查是否是作品所有者
        if (!design.getDesignerId().equals(userId)) {
            throw new RuntimeException("只能申请上架自己的作品");
        }
        
        // 检查是否已申请
        if (hasApplied(request.getDesignId())) {
            throw new RuntimeException("该作品已提交过上架申请");
        }
        
        // 创建申请
        MallApplication application = new MallApplication();
        application.setDesignId(request.getDesignId());
        application.setUserId(userId);
        application.setProductName(request.getProductName());
        application.setCategory(request.getCategory());
        application.setDescription(request.getDescription());
        application.setSuggestedPrice(request.getSuggestedPrice());
        application.setInitialStock(request.getInitialStock());
        application.setIcon(request.getIcon() != null ? request.getIcon() : "🎁");
        application.setStatus("pending");
        
        applicationMapper.insert(application);
        
        log.info("用户 {} 提交了作品 {} 的上架申请", userId, request.getDesignId());
        
        return application;
    }
    
    @Override
    public Page<MallApplication> getApplicationList(int page, int size, String status) {
        Page<MallApplication> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<MallApplication> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty() && !"all".equals(status)) {
            wrapper.eq(MallApplication::getStatus, status);
        }
        wrapper.orderByDesc(MallApplication::getCreatedAt);
        
        return applicationMapper.selectPage(pageParam, wrapper);
    }
    
    @Override
    public Page<MallApplicationVO> getApplicationListWithDesign(int page, int size, String status) {
        Page<MallApplication> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<MallApplication> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty() && !"all".equals(status)) {
            wrapper.eq(MallApplication::getStatus, status);
        }
        wrapper.orderByDesc(MallApplication::getCreatedAt);
        
        Page<MallApplication> applicationPage = applicationMapper.selectPage(pageParam, wrapper);
        
        // 转换为 VO 并填充作品信息
        Page<MallApplicationVO> voPage = new Page<>(page, size, applicationPage.getTotal());
        List<MallApplicationVO> voList = applicationPage.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    @Override
    public MallApplicationVO getApplicationWithDesign(Long id) {
        MallApplication application = applicationMapper.selectById(id);
        if (application == null) {
            return null;
        }
        return convertToVO(application);
    }
    
    /**
     * 将 MallApplication 转换为 MallApplicationVO（包含作品信息）
     */
    private MallApplicationVO convertToVO(MallApplication application) {
        MallApplicationVO vo = new MallApplicationVO();
        BeanUtils.copyProperties(application, vo);
        
        // 获取关联的作品信息
        if (application.getDesignId() != null) {
            Design design = designMapper.selectById(application.getDesignId());
            if (design != null) {
                vo.setDesignTitle(design.getTitle());
                vo.setDesignDescription(design.getDescription());
                vo.setCoverImage(design.getCoverImage());
                vo.setDesignerName("设计师" + design.getDesignerId());
                
                // 解析文件列表
                if (design.getFiles() != null && !design.getFiles().isEmpty()) {
                    try {
                        List<String> files = objectMapper.readValue(
                            design.getFiles(), 
                            new TypeReference<List<String>>() {}
                        );
                        vo.setFiles(files);
                    } catch (Exception e) {
                        log.warn("解析作品文件列表失败: {}", e.getMessage());
                        vo.setFiles(new ArrayList<>());
                    }
                }
            }
        }
        
        return vo;
    }
    
    @Override
    public Page<MallApplication> getUserApplications(Long userId, int page, int size) {
        Page<MallApplication> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<MallApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MallApplication::getUserId, userId);
        wrapper.orderByDesc(MallApplication::getCreatedAt);
        
        return applicationMapper.selectPage(pageParam, wrapper);
    }
    
    @Override
    public MallApplication getApplicationById(Long id) {
        return applicationMapper.selectById(id);
    }
    
    @Override
    @Transactional
    public MallApplication reviewApplication(Long id, boolean approved, String comment, Long reviewerId) {
        MallApplication application = applicationMapper.selectById(id);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }
        
        if (!"pending".equals(application.getStatus())) {
            throw new RuntimeException("该申请已处理");
        }
        
        application.setStatus(approved ? "approved" : "rejected");
        application.setReviewComment(comment);
        application.setReviewerId(reviewerId);
        application.setReviewedAt(LocalDateTime.now());
        
        if (approved) {
            // 调用商城服务创建商品
            try {
                Long productId = createProductInMall(application);
                application.setProductId(productId);
                log.info("商品创建成功，ID: {}", productId);
            } catch (Exception e) {
                log.error("创建商品失败", e);
                throw new RuntimeException("创建商品失败: " + e.getMessage());
            }
        }
        
        applicationMapper.updateById(application);
        
        log.info("审核申请 {}: {}", id, approved ? "通过" : "拒绝");
        
        return application;
    }
    
    @Override
    public boolean hasApplied(Long designId) {
        LambdaQueryWrapper<MallApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MallApplication::getDesignId, designId);
        wrapper.ne(MallApplication::getStatus, "rejected"); // 被拒绝的可以重新申请
        
        return applicationMapper.selectCount(wrapper) > 0;
    }
    
    /**
     * 调用商城服务创建商品
     */
    private Long createProductInMall(MallApplication application) {
        // 获取设计作品信息
        Design design = designMapper.selectById(application.getDesignId());
        
        String mallServiceUrl = "http://localhost:8085/api/mall/products";
        
        Map<String, Object> productData = new HashMap<>();
        productData.put("name", application.getProductName());
        productData.put("category", application.getCategory());
        productData.put("description", application.getDescription());
        productData.put("price", application.getSuggestedPrice());
        productData.put("stock", application.getInitialStock());
        productData.put("icon", application.getIcon());
        productData.put("designer", "众创设计师");
        productData.put("inStock", true);
        productData.put("color", "linear-gradient(135deg, #c41e3a, #8b1e3f)");
        
        // 如果有封面图片和作品文件，添加到商品
        if (design != null) {
            // 设置文化背景
            productData.put("culturalBackground", "来自众创空间的优秀设计作品：" + design.getTitle());
            
            // 收集所有图片
            List<String> allImages = new ArrayList<>();
            
            // 添加封面图片
            if (design.getCoverImage() != null && !design.getCoverImage().isEmpty()) {
                allImages.add(design.getCoverImage());
            }
            
            // 添加作品文件中的图片
            if (design.getFiles() != null && !design.getFiles().isEmpty()) {
                try {
                    List<String> files = objectMapper.readValue(
                        design.getFiles(), 
                        new TypeReference<List<String>>() {}
                    );
                    for (String file : files) {
                        // 只添加图片文件
                        if (file != null && (file.endsWith(".jpg") || file.endsWith(".jpeg") || 
                            file.endsWith(".png") || file.endsWith(".gif") || file.endsWith(".webp"))) {
                            if (!allImages.contains(file)) {
                                allImages.add(file);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("解析作品文件列表失败: {}", e.getMessage());
                }
            }
            
            // 将图片列表转为JSON字符串
            if (!allImages.isEmpty()) {
                try {
                    productData.put("images", objectMapper.writeValueAsString(allImages));
                } catch (Exception e) {
                    log.warn("序列化图片列表失败: {}", e.getMessage());
                }
            }
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(productData, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(mallServiceUrl, entity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map body = response.getBody();
                if (body.containsKey("data") && body.get("data") instanceof Map) {
                    Map data = (Map) body.get("data");
                    if (data.containsKey("id")) {
                        return Long.valueOf(data.get("id").toString());
                    }
                }
                // 直接返回的情况
                if (body.containsKey("id")) {
                    return Long.valueOf(body.get("id").toString());
                }
            }
            throw new RuntimeException("商城服务返回异常");
        } catch (Exception e) {
            log.error("调用商城服务失败", e);
            throw new RuntimeException("调用商城服务失败: " + e.getMessage());
        }
    }
}
