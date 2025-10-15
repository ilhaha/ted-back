SET @parentId = 1922499364900950016;
-- 考场地点和考场关联管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '考场地点和考场关联管理', 1000, 2, '/exam/locationClassroom', 'LocationClassroom', 'exam/locationClassroom/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 考场地点和考场关联管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1922499364900950017, '列表', @parentId, 3, 'exam:locationClassroom:list', 1, 1, 1, NOW()),
    (1922499364900950018, '详情', @parentId, 3, 'exam:locationClassroom:detail', 2, 1, 1, NOW()),
    (1922499364900950019, '新增', @parentId, 3, 'exam:locationClassroom:add', 3, 1, 1, NOW()),
    (1922499364900950020, '修改', @parentId, 3, 'exam:locationClassroom:update', 4, 1, 1, NOW()),
    (1922499364900950021, '删除', @parentId, 3, 'exam:locationClassroom:delete', 5, 1, 1, NOW()),
    (1922499364900950022, '导出', @parentId, 3, 'exam:locationClassroom:export', 6, 1, 1, NOW());

