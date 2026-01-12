package com.jiyi.experience.service;

import com.jiyi.experience.dto.SceneVO;
import com.jiyi.experience.entity.InteractionPoint;
import com.jiyi.experience.entity.Scene;
import com.jiyi.experience.mapper.*;
import com.jiyi.experience.service.impl.ExperienceServiceImpl;
import net.jqwik.api.*;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * 体验馆服务属性测试
 * 使用基于属性的测试验证系统的正确性属性
 */
class ExperienceServicePropertyTest {
    
    /**
     * Feature: jiyi-red-route, Property 1: 场景列表完整性
     * 
     * 属性: 对于任意场景列表请求，返回的每个场景对象都应该包含名称、预览图、时代背景和体验时长字段
     * 验证需求: 1.1
     * 
     * 这个属性测试验证了场景列表的完整性。无论数据库中有多少场景，
     * 每个返回的场景对象都必须包含所有必需的字段（name, thumbnail, era, duration）。
     */
    @Property(tries = 100)
    @Label("场景列表完整性 - 所有场景对象必须包含必需字段")
    void sceneListCompleteness(@ForAll("sceneList") List<Scene> scenes) {
        // Arrange: 创建mock并设置行为
        SceneMapper sceneMapper = Mockito.mock(SceneMapper.class);
        InteractionPointMapper interactionPointMapper = Mockito.mock(InteractionPointMapper.class);
        UserProgressMapper userProgressMapper = Mockito.mock(UserProgressMapper.class);
        CertificateMapper certificateMapper = Mockito.mock(CertificateMapper.class);
        
        ExperienceService experienceService = new ExperienceServiceImpl(
                sceneMapper, interactionPointMapper, userProgressMapper, certificateMapper);
        when(sceneMapper.selectList(null)).thenReturn(scenes);
        
        // Act: 调用服务获取场景列表
        List<SceneVO> result = experienceService.getSceneList();
        
        // Assert: 验证每个场景对象都包含所有必需字段
        assertThat(result).isNotNull();
        assertThat(result).hasSize(scenes.size());
        
        // 对于任意场景列表，每个场景对象都应该包含完整的字段
        for (SceneVO sceneVO : result) {
            // 验证名称字段存在且非空
            assertThat(sceneVO.getName())
                    .as("场景名称不能为空")
                    .isNotNull()
                    .isNotBlank();
            
            // 验证预览图字段存在且非空
            assertThat(sceneVO.getThumbnail())
                    .as("场景预览图不能为空")
                    .isNotNull()
                    .isNotBlank();
            
            // 验证时代背景字段存在且非空
            assertThat(sceneVO.getEra())
                    .as("场景时代背景不能为空")
                    .isNotNull()
                    .isNotBlank();
            
            // 验证体验时长字段存在且为正数
            assertThat(sceneVO.getDuration())
                    .as("场景体验时长不能为空且必须为正数")
                    .isNotNull()
                    .isPositive();
        }
    }
    
    /**
     * Feature: jiyi-red-route, Property 2: 场景数据加载一致性
     * 
     * 属性: 对于任意有效的场景ID，加载场景应该返回包含3D模型数据和交互点列表的完整场景对象
     * 验证需求: 1.2
     * 
     * 这个属性测试验证了场景详情加载的一致性。当用户选择某个场景时，
     * 系统必须返回包含3D模型URL和交互点列表的完整场景对象，确保用户能够
     * 正常体验3D虚拟环境和交互式导览。
     */
    @Property(tries = 100)
    @Label("场景数据加载一致性 - 场景详情必须包含3D模型和交互点列表")
    void sceneDataLoadingConsistency(
            @ForAll("validScene") Scene scene,
            @ForAll("interactionPointsForScene") List<InteractionPoint> interactionPoints,
            @ForAll("optionalUserId") Long userId) {
        // Arrange: 创建mock并设置行为
        SceneMapper sceneMapper = Mockito.mock(SceneMapper.class);
        InteractionPointMapper interactionPointMapper = Mockito.mock(InteractionPointMapper.class);
        UserProgressMapper userProgressMapper = Mockito.mock(UserProgressMapper.class);
        CertificateMapper certificateMapper = Mockito.mock(CertificateMapper.class);
        
        ExperienceService experienceService = new ExperienceServiceImpl(
                sceneMapper, interactionPointMapper, userProgressMapper, certificateMapper);
        
        // 设置场景查询返回
        when(sceneMapper.selectById(scene.getId())).thenReturn(scene);
        
        // 设置交互点查询返回
        when(interactionPointMapper.selectList(Mockito.any())).thenReturn(interactionPoints);
        
        // 设置用户进度查询返回（可能为null）
        when(userProgressMapper.selectOne(Mockito.any())).thenReturn(null);
        
        // Act: 调用服务获取场景详情
        com.jiyi.experience.dto.SceneDetailVO result = experienceService.getSceneDetail(scene.getId(), userId);
        
        // Assert: 验证场景详情包含完整的3D模型数据和交互点列表
        assertThat(result).isNotNull();
        
        // 验证场景基本信息
        assertThat(result.getId())
                .as("场景ID必须存在")
                .isEqualTo(scene.getId());
        
        assertThat(result.getName())
                .as("场景名称必须存在且非空")
                .isNotNull()
                .isNotBlank();
        
        assertThat(result.getDescription())
                .as("场景描述必须存在且非空")
                .isNotNull()
                .isNotBlank();
        
        assertThat(result.getEra())
                .as("时代背景必须存在且非空")
                .isNotNull()
                .isNotBlank();
        
        assertThat(result.getDuration())
                .as("体验时长必须存在且为正数")
                .isNotNull()
                .isPositive();
        
        // 验证3D模型数据存在（这是Property 2的核心验证点）
        assertThat(result.getModelUrl())
                .as("3D模型URL必须存在且非空，以支持3D虚拟环境加载")
                .isNotNull()
                .isNotBlank();
        
        // 验证交互点列表存在（这是Property 2的核心验证点）
        assertThat(result.getInteractionPoints())
                .as("交互点列表必须存在，以提供交互式导览")
                .isNotNull()
                .hasSize(interactionPoints.size());
        
        // 验证每个交互点都包含必需的字段
        for (com.jiyi.experience.dto.InteractionPointVO pointVO : result.getInteractionPoints()) {
            assertThat(pointVO.getId())
                    .as("交互点ID必须存在")
                    .isNotNull();
            
            assertThat(pointVO.getTitle())
                    .as("交互点标题必须存在且非空")
                    .isNotNull()
                    .isNotBlank();
            
            assertThat(pointVO.getType())
                    .as("交互点类型必须存在且非空")
                    .isNotNull()
                    .isNotBlank();
            
            assertThat(pointVO.getContent())
                    .as("交互点内容必须存在且非空")
                    .isNotNull()
                    .isNotBlank();
            
            // 验证3D位置坐标存在
            assertThat(pointVO.getPositionX())
                    .as("交互点X坐标必须存在")
                    .isNotNull();
            
            assertThat(pointVO.getPositionY())
                    .as("交互点Y坐标必须存在")
                    .isNotNull();
            
            assertThat(pointVO.getPositionZ())
                    .as("交互点Z坐标必须存在")
                    .isNotNull();
        }
        
        // 验证进度字段存在（即使用户未登录也应该有默认值0）
        assertThat(result.getProgress())
                .as("进度字段必须存在")
                .isNotNull()
                .isBetween(0, 100);
    }
    
    /**
     * 生成随机场景列表的提供者
     * 
     * 这个提供者生成包含0到10个场景的列表，每个场景都有完整的必需字段。
     * 通过随机生成不同的场景数据，我们可以验证系统在各种输入下的行为。
     */
    @Provide
    Arbitrary<List<Scene>> sceneList() {
        return Arbitraries.integers().between(0, 10)
                .flatMap(size -> {
                    // 生成指定数量的场景
                    Arbitrary<Scene> sceneArbitrary = Combinators.combine(
                            Arbitraries.longs().greaterOrEqual(1L),
                            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(50),
                            Arbitraries.strings().alpha().ofMinLength(10).ofMaxLength(200),
                            Arbitraries.of("建党时期", "抗战时期", "解放战争", "新中国建设"),
                            Arbitraries.integers().between(5, 60),
                            Arbitraries.strings().alpha().ofMinLength(10).ofMaxLength(100),
                            Arbitraries.strings().alpha().ofMinLength(10).ofMaxLength(100),
                            Arbitraries.integers().between(1, 20)
                    ).as((id, name, description, era, duration, thumbnail, modelUrl, interactionCount) -> {
                        Scene scene = new Scene();
                        scene.setId(id);
                        scene.setName(name);
                        scene.setDescription(description);
                        scene.setEra(era);
                        scene.setDuration(duration);
                        scene.setThumbnail(thumbnail);
                        scene.setModelUrl(modelUrl);
                        scene.setInteractionCount(interactionCount);
                        return scene;
                    });
                    
                    // 生成场景列表
                    return sceneArbitrary.list().ofSize(size);
                });
    }
    
    /**
     * 生成有效场景的提供者
     * 
     * 为Property 2生成单个有效的场景对象，包含所有必需字段
     */
    @Provide
    Arbitrary<Scene> validScene() {
        return Combinators.combine(
                Arbitraries.longs().greaterOrEqual(1L),
                Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(50),
                Arbitraries.strings().alpha().ofMinLength(10).ofMaxLength(200),
                Arbitraries.of("建党时期", "抗战时期", "解放战争", "新中国建设"),
                Arbitraries.integers().between(5, 60),
                Arbitraries.strings().alpha().ofMinLength(10).ofMaxLength(100),
                Arbitraries.strings().alpha().ofMinLength(10).ofMaxLength(100),
                Arbitraries.integers().between(1, 20)
        ).as((id, name, description, era, duration, thumbnail, modelUrl, interactionCount) -> {
            Scene scene = new Scene();
            scene.setId(id);
            scene.setName(name);
            scene.setDescription(description);
            scene.setEra(era);
            scene.setDuration(duration);
            scene.setThumbnail(thumbnail);
            scene.setModelUrl(modelUrl);
            scene.setInteractionCount(interactionCount);
            return scene;
        });
    }
    
    /**
     * 生成交互点列表的提供者
     * 
     * 为Property 2生成1到10个交互点，每个交互点都有完整的必需字段
     */
    @Provide
    Arbitrary<List<InteractionPoint>> interactionPointsForScene() {
        return Arbitraries.integers().between(1, 10)
                .flatMap(size -> {
                    // 由于Combinators.combine最多支持8个参数，我们需要分步构建
                    Arbitrary<InteractionPoint> pointArbitrary = 
                            Combinators.combine(
                                    Arbitraries.longs().greaterOrEqual(1L),
                                    Arbitraries.longs().greaterOrEqual(1L),
                                    Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(50),
                                    Arbitraries.of("info", "video", "story", "photo", "artifact", "character", "event"),
                                    Arbitraries.integers().between(0, 100),
                                    Arbitraries.integers().between(0, 100),
                                    Arbitraries.integers().between(0, 100),
                                    Arbitraries.strings().alpha().ofMinLength(10).ofMaxLength(200)
                            ).flatAs((id, sceneId, title, type, posX, posY, posZ, content) ->
                                    Combinators.combine(
                                            Arbitraries.strings().alpha().ofMinLength(10).ofMaxLength(100),
                                            Arbitraries.integers().between(1, 100)
                                    ).as((mediaUrl, sortOrder) -> {
                                        InteractionPoint point = new InteractionPoint();
                                        point.setId(id);
                                        point.setSceneId(sceneId);
                                        point.setTitle(title);
                                        point.setType(type);
                                        point.setPositionX(posX);
                                        point.setPositionY(posY);
                                        point.setPositionZ(posZ);
                                        point.setContent(content);
                                        point.setMediaUrl(mediaUrl);
                                        point.setSortOrder(sortOrder);
                                        return point;
                                    })
                            );
                    
                    return pointArbitrary.list().ofSize(size);
                });
    }
    
    /**
     * Feature: jiyi-red-route, Property 3: 交互点触发响应性
     * 
     * 属性: 对于任意场景中的交互点，触发该交互点应该返回对应的内容数据（文物、人物或事件信息）
     * 验证需求: 1.4
     * 
     * 这个属性测试验证了交互点触发的响应性。当用户在虚拟场景中触发某个交互点时，
     * 系统必须返回该交互点对应的完整内容数据，包括标题、类型、内容描述和媒体资源。
     * 这确保了用户能够获得相关的历史文物、人物故事或事件详情。
     */
    @Property(tries = 100)
    @Label("交互点触发响应性 - 触发交互点必须返回对应的内容数据")
    void interactionPointTriggerResponsiveness(
            @ForAll("validScene") Scene scene,
            @ForAll("interactionPointsForScene") List<InteractionPoint> interactionPoints) {
        // Arrange: 创建mock并设置行为
        SceneMapper sceneMapper = Mockito.mock(SceneMapper.class);
        InteractionPointMapper interactionPointMapper = Mockito.mock(InteractionPointMapper.class);
        UserProgressMapper userProgressMapper = Mockito.mock(UserProgressMapper.class);
        CertificateMapper certificateMapper = Mockito.mock(CertificateMapper.class);
        
        ExperienceService experienceService = new ExperienceServiceImpl(
                sceneMapper, interactionPointMapper, userProgressMapper, certificateMapper);
        
        // 设置场景查询返回
        when(sceneMapper.selectById(scene.getId())).thenReturn(scene);
        
        // 设置交互点查询返回
        when(interactionPointMapper.selectList(Mockito.any())).thenReturn(interactionPoints);
        
        // 设置用户进度查询返回
        when(userProgressMapper.selectOne(Mockito.any())).thenReturn(null);
        
        // Act: 调用服务获取场景详情（这会加载所有交互点）
        com.jiyi.experience.dto.SceneDetailVO sceneDetail = experienceService.getSceneDetail(scene.getId(), null);
        
        // Assert: 验证每个交互点都能被"触发"并返回完整的内容数据
        assertThat(sceneDetail.getInteractionPoints())
                .as("场景必须包含交互点列表")
                .isNotNull()
                .hasSize(interactionPoints.size());
        
        // 对于任意场景中的交互点，触发该交互点应该返回对应的内容数据
        for (int i = 0; i < interactionPoints.size(); i++) {
            InteractionPoint originalPoint = interactionPoints.get(i);
            com.jiyi.experience.dto.InteractionPointVO triggeredPoint = sceneDetail.getInteractionPoints().get(i);
            
            // 验证交互点ID匹配（确保是同一个交互点）
            assertThat(triggeredPoint.getId())
                    .as("触发的交互点ID必须与原始交互点ID匹配")
                    .isEqualTo(originalPoint.getId());
            
            // 验证返回了标题信息
            assertThat(triggeredPoint.getTitle())
                    .as("触发交互点必须返回标题信息")
                    .isNotNull()
                    .isNotBlank()
                    .isEqualTo(originalPoint.getTitle());
            
            // 验证返回了类型信息（文物、人物或事件）
            assertThat(triggeredPoint.getType())
                    .as("触发交互点必须返回类型信息（文物、人物或事件）")
                    .isNotNull()
                    .isNotBlank()
                    .isEqualTo(originalPoint.getType());
            
            // 验证返回了内容详情（这是Property 3的核心验证点）
            assertThat(triggeredPoint.getContent())
                    .as("触发交互点必须返回内容详情（历史文物、人物故事或事件详情）")
                    .isNotNull()
                    .isNotBlank()
                    .isEqualTo(originalPoint.getContent());
            
            // 验证返回了媒体资源URL（用于展示图片、视频等）
            assertThat(triggeredPoint.getMediaUrl())
                    .as("触发交互点必须返回媒体资源URL")
                    .isNotNull()
                    .isNotBlank()
                    .isEqualTo(originalPoint.getMediaUrl());
            
            // 验证返回了3D位置坐标（用于在场景中定位交互点）
            assertThat(triggeredPoint.getPositionX())
                    .as("触发交互点必须返回X坐标")
                    .isNotNull()
                    .isEqualTo(originalPoint.getPositionX());
            
            assertThat(triggeredPoint.getPositionY())
                    .as("触发交互点必须返回Y坐标")
                    .isNotNull()
                    .isEqualTo(originalPoint.getPositionY());
            
            assertThat(triggeredPoint.getPositionZ())
                    .as("触发交互点必须返回Z坐标")
                    .isNotNull()
                    .isEqualTo(originalPoint.getPositionZ());
            
            // 验证内容数据的完整性（非空且有意义）
            assertThat(triggeredPoint.getContent().length())
                    .as("交互点内容必须有足够的信息量")
                    .isGreaterThan(5);
        }
        
        // 额外验证：确保所有交互点都能被访问（没有遗漏）
        assertThat(sceneDetail.getInteractionPoints())
                .as("所有交互点都必须能被触发和访问")
                .extracting("id")
                .containsExactlyInAnyOrderElementsOf(
                        interactionPoints.stream()
                                .map(InteractionPoint::getId)
                                .toList()
                );
    }
    
    /**
     * 生成可选用户ID的提供者
     * 
     * 生成可能为null或有效的用户ID，用于测试有无用户登录的场景
     */
    @Provide
    Arbitrary<Long> optionalUserId() {
        return Arbitraries.frequencyOf(
                Tuple.of(1, Arbitraries.just(null)),
                Tuple.of(3, Arbitraries.longs().greaterOrEqual(1L))
        );
    }
}
