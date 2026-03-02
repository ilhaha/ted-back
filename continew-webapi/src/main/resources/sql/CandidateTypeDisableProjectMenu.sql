SET @parentId = 2028351947883753472;
-- 考生类型与禁考项目关联管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '考生类型与禁考项目关联管理', 1000, 2, '/training/candidateTypeDisableProject', 'CandidateTypeDisableProject', 'training/candidateTypeDisableProject/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 考生类型与禁考项目关联管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (2028351947883753473, '列表', @parentId, 3, 'training:candidateTypeDisableProject:list', 1, 1, 1, NOW()),
    (2028351947883753474, '详情', @parentId, 3, 'training:candidateTypeDisableProject:detail', 2, 1, 1, NOW()),
    (2028351947883753475, '新增', @parentId, 3, 'training:candidateTypeDisableProject:add', 3, 1, 1, NOW()),
    (2028351947883753476, '修改', @parentId, 3, 'training:candidateTypeDisableProject:update', 4, 1, 1, NOW()),
    (2028351947883753477, '删除', @parentId, 3, 'training:candidateTypeDisableProject:delete', 5, 1, 1, NOW()),
    (2028351947883753478, '导出', @parentId, 3, 'training:candidateTypeDisableProject:export', 6, 1, 1, NOW());

