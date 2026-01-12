package com.jiyi.academy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiyi.academy.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    
    @Update("UPDATE course SET enrollment_count = enrollment_count + 1 WHERE id = #{courseId}")
    void incrementEnrollmentCount(Long courseId);
}
