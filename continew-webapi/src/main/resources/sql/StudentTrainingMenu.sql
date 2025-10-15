SET @parentId = 1904743305392074752;
-- 学生培训管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '学生培训管理', 1000, 2, '/training/studentTraining', 'StudentTraining', 'training/studentTraining/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 学生培训管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1904743305392074753, '列表', @parentId, 3, 'training:studentTraining:list', 1, 1, 1, NOW()),
    (1904743305392074754, '详情', @parentId, 3, 'training:studentTraining:detail', 2, 1, 1, NOW()),
    (1904743305392074755, '新增', @parentId, 3, 'training:studentTraining:add', 3, 1, 1, NOW()),
    (1904743305392074756, '修改', @parentId, 3, 'training:studentTraining:update', 4, 1, 1, NOW()),
    (1904743305392074757, '删除', @parentId, 3, 'training:studentTraining:delete', 5, 1, 1, NOW()),
    (1904743305392074758, '导出', @parentId, 3, 'training:studentTraining:export', 6, 1, 1, NOW());

