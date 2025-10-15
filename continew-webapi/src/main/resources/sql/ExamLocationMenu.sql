SET @parentId = 1899355670539284480;
-- 考试地点管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '考试地点管理', 1000, 2, '/exam/examLocation', 'ExamLocation', 'exam/examLocation/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 考试地点管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1899355670539284481, '列表', @parentId, 3, 'exam:examLocation:list', 1, 1, 1, NOW()),
    (1899355670539284482, '详情', @parentId, 3, 'exam:examLocation:detail', 2, 1, 1, NOW()),
    (1899355670539284483, '新增', @parentId, 3, 'exam:examLocation:add', 3, 1, 1, NOW()),
    (1899355670539284484, '修改', @parentId, 3, 'exam:examLocation:update', 4, 1, 1, NOW()),
    (1899355670539284485, '删除', @parentId, 3, 'exam:examLocation:delete', 5, 1, 1, NOW()),
    (1899355670539284486, '导出', @parentId, 3, 'exam:examLocation:export', 6, 1, 1, NOW());

