SET @parentId = 1962428770709053440;
-- 机构与八大类关联，记录多对多关系管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '机构与八大类关联，记录多对多关系管理', 1000, 2, '/admin/orgCategoryRelation', 'OrgCategoryRelation', 'admin/orgCategoryRelation/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 机构与八大类关联，记录多对多关系管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1962428770709053441, '列表', @parentId, 3, 'admin:orgCategoryRelation:list', 1, 1, 1, NOW()),
    (1962428770709053442, '详情', @parentId, 3, 'admin:orgCategoryRelation:detail', 2, 1, 1, NOW()),
    (1962428770709053443, '新增', @parentId, 3, 'admin:orgCategoryRelation:add', 3, 1, 1, NOW()),
    (1962428770709053444, '修改', @parentId, 3, 'admin:orgCategoryRelation:update', 4, 1, 1, NOW()),
    (1962428770709053445, '删除', @parentId, 3, 'admin:orgCategoryRelation:delete', 5, 1, 1, NOW()),
    (1962428770709053446, '导出', @parentId, 3, 'admin:orgCategoryRelation:export', 6, 1, 1, NOW());

