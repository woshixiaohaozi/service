package com.example.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/** 系统部门实体，对应表 sys_dept。树形结构，支撑数据权限。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    private String deptName;
    private Long parentId;
    private Long leaderId;
    private Integer sort;
    private Integer status;

    /** 子节点集合，仅用于构建树形返回，不映射数据库字段。 */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private List<SysDept> children;
}
