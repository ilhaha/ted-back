SET @parentId = 1999009056912920576;
-- 考试劳务费配置管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '考试劳务费配置管理', 1000, 2, '/invigilate/laborFee', 'LaborFee', 'invigilate/laborFee/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 考试劳务费配置管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1999009056912920577, '列表', @parentId, 3, 'invigilate:laborFee:list', 1, 1, 1, NOW()),
    (1999009056912920578, '详情', @parentId, 3, 'invigilate:laborFee:detail', 2, 1, 1, NOW()),
    (1999009056912920579, '新增', @parentId, 3, 'invigilate:laborFee:add', 3, 1, 1, NOW()),
    (1999009056912920580, '修改', @parentId, 3, 'invigilate:laborFee:update', 4, 1, 1, NOW()),
    (1999009056912920581, '删除', @parentId, 3, 'invigilate:laborFee:delete', 5, 1, 1, NOW()),
    (1999009056912920582, '导出', @parentId, 3, 'invigilate:laborFee:export', 6, 1, 1, NOW());

