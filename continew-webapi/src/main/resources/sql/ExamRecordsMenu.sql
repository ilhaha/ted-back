SET @parentId = 1901441628088733696;
-- 考试记录管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '考试记录管理', 1000, 2, '/exam/examRecords', 'ExamRecords', 'exam/examRecords/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 考试记录管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1901441628088733697, '列表', @parentId, 3, 'exam:examRecords:list', 1, 1, 1, NOW()),
    (1901441628088733698, '详情', @parentId, 3, 'exam:examRecords:detail', 2, 1, 1, NOW()),
    (1901441628088733699, '新增', @parentId, 3, 'exam:examRecords:add', 3, 1, 1, NOW()),
    (1901441628088733700, '修改', @parentId, 3, 'exam:examRecords:update', 4, 1, 1, NOW()),
    (1901441628088733701, '删除', @parentId, 3, 'exam:examRecords:delete', 5, 1, 1, NOW()),
    (1901441628088733702, '导出', @parentId, 3, 'exam:examRecords:export', 6, 1, 1, NOW());

