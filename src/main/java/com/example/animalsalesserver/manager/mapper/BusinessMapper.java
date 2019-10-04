package com.example.animalsalesserver.manager.mapper;

import com.example.animalsalesserver.manager.domain.Business;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品表(Businesses)表数据库访问层
 *
 * @author wotrd
 * @since 2019-10-04 14:47:10
 */
@Mapper
@Repository
public interface BusinessMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Business queryById(Long id);

    /**
     * 通过 name 作为筛选条件查询
     *
     * @param name 查询起始位置
     * @return 对象列表
     */
    List<Business> queryLikeName(String name);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param businesses 实例对象
     * @return 对象列表
     */
    List<Business> queryAll(Business businesses);

    /**
     * 新增数据
     *
     * @param businesses 实例对象
     * @return 影响行数
     */
    int insert(Business businesses);

    /**
     * 修改数据
     *
     * @param businesses 实例对象
     * @return 影响行数
     */
    int update(Business businesses);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

}