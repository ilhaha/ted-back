SET @parentId = 1915238533352579072;
-- 考试计划监考人员关联管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '考试计划监考人员关联管理', 1000, 2, '/invigilate/planInvigilate', 'PlanInvigilate', 'invigilate/planInvigilate/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 考试计划监考人员关联管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1915238533352579073, '列表', @parentId, 3, 'invigilate:planInvigilate:list', 1, 1, 1, NOW()),
    (1915238533352579074, '详情', @parentId, 3, 'invigilate:planInvigilate:detail', 2, 1, 1, NOW()),
    (1915238533352579075, '新增', @parentId, 3, 'invigilate:planInvigilate:add', 3, 1, 1, NOW()),
    (1915238533352579076, '修改', @parentId, 3, 'invigilate:planInvigilate:update', 4, 1, 1, NOW()),
    (1915238533352579077, '删除', @parentId, 3, 'invigilate:planInvigilate:delete', 5, 1, 1, NOW()),
    (1915238533352579078, '导出', @parentId, 3, 'invigilate:planInvigilate:export', 6, 1, 1, NOW());

