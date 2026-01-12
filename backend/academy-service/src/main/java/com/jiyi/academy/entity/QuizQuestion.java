package com.jiyi.academy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("quiz_question")
public class QuizQuestion {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long quizId;
    
    private String question;
    
    private String type;
    
    private String options;
    
    private String correctAnswer;
    
    private Integer score;
    
    private Integer orderNum;
}
