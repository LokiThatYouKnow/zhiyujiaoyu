package com.mengzhidui.a7springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mengzhidui.a7springboot.entity.po.AttendanceRecordPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttendanceRecordMapper extends BaseMapper<AttendanceRecordPO> {
}
