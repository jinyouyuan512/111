package com.jiyi.guide.dto;

import lombok.Data;

@Data
public class ArSceneVO {
    private Long id;
    private Long poiId;
    private String name;
    private String description;
    private String sceneDataUrl;
    private String previewImage;
}
