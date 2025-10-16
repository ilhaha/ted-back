package top.continew.admin.training.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.continew.admin.training.model.entity.OrgCategoryRelationDO;
import top.continew.starter.data.mp.base.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * 机构与八大类关联，记录多对多关系 Mapper
 */
public interface OrgCategoryRelationMapper extends BaseMapper<OrgCategoryRelationDO> {

    /**
     * 根据机构ID删除该机构与所有类目的关联关系
     */
    @Delete("DELETE FROM ted_org_category_relation WHERE org_id = #{id}")
    void deleteByOrgId(Long id);

    /**
     * 批量查询机构对应的类目名称
     */
    @Select({
            "<script>",
            "SELECT r.org_id, c.name",
            "FROM ted_org_category_relation r",
            "JOIN ted_category c ON r.category_id = c.id",
            "WHERE r.org_id IN",
            "<foreach collection='orgIds' item='id' open='(' separator=',' close=')'>",
            "   #{id}",
            "</foreach>",
            "AND r.is_deleted = 0",
            "AND c.is_deleted = 0",
            "</script>"
    })
    List<Map<String, Object>> listCategoryInfoByOrgIds(@Param("orgIds") List<Long> orgIds);

    @Select("""
    SELECT c.name
    FROM ted_org_category_relation r
    JOIN ted_category c ON r.category_id = c.id
    WHERE r.org_id = #{orgId}
      AND r.is_deleted = 0
      AND c.is_deleted = 0
    """)
    List<String> listCategoryNamesByOrgId(@Param("orgId") Long orgId);

}

