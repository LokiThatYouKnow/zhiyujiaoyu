package com.mengzhidui.a7springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mengzhidui.a7springboot.entity.po.AttendancePO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttendanceMapper extends BaseMapper<AttendancePO> {
}
