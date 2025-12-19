SET @parentId = 2001262985503625216;
-- 考试劳务费配置管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '考试劳务费配置管理', 1000, 2, '/exam/examViolation', 'ExamViolation', 'exam/examViolation/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 考试劳务费配置管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (2001262985503625217, '列表', @parentId, 3, 'exam:examViolation:list', 1, 1, 1, NOW()),
    (2001262985503625218, '详情', @parentId, 3, 'exam:examViolation:detail', 2, 1, 1, NOW()),
    (2001262985503625219, '新增', @parentId, 3, 'exam:examViolation:add', 3, 1, 1, NOW()),
    (2001262985503625220, '修改', @parentId, 3, 'exam:examViolation:update', 4, 1, 1, NOW()),
    (2001262985503625221, '删除', @parentId, 3, 'exam:examViolation:delete', 5, 1, 1, NOW()),
    (2001262985503625222, '导出', @parentId, 3, 'exam:examViolation:export', 6, 1, 1, NOW());

