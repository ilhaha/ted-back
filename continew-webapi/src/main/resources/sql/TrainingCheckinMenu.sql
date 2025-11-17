SET @parentId = 1990261564922744832;
-- 培训签到记录管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '培训签到记录管理', 1000, 2, '/training/trainingCheckin', 'TrainingCheckin', 'training/trainingCheckin/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 培训签到记录管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1990261564922744833, '列表', @parentId, 3, 'training:trainingCheckin:list', 1, 1, 1, NOW()),
    (1990261564922744834, '详情', @parentId, 3, 'training:trainingCheckin:detail', 2, 1, 1, NOW()),
    (1990261564922744835, '新增', @parentId, 3, 'training:trainingCheckin:add', 3, 1, 1, NOW()),
    (1990261564922744836, '修改', @parentId, 3, 'training:trainingCheckin:update', 4, 1, 1, NOW()),
    (1990261564922744837, '删除', @parentId, 3, 'training:trainingCheckin:delete', 5, 1, 1, NOW()),
    (1990261564922744838, '导出', @parentId, 3, 'training:trainingCheckin:export', 6, 1, 1, NOW());

