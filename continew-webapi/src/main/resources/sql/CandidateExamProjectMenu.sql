SET @parentId = 2016394018121175040;
-- 考生-考试项目考试状态管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '考生-考试项目考试状态管理', 1000, 2, '/exam/candidateExamProject', 'CandidateExamProject', 'exam/candidateExamProject/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 考生-考试项目考试状态管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (2016394018121175041, '列表', @parentId, 3, 'exam:candidateExamProject:list', 1, 1, 1, NOW()),
    (2016394018121175042, '详情', @parentId, 3, 'exam:candidateExamProject:detail', 2, 1, 1, NOW()),
    (2016394018121175043, '新增', @parentId, 3, 'exam:candidateExamProject:add', 3, 1, 1, NOW()),
    (2016394018121175044, '修改', @parentId, 3, 'exam:candidateExamProject:update', 4, 1, 1, NOW()),
    (2016394018121175045, '删除', @parentId, 3, 'exam:candidateExamProject:delete', 5, 1, 1, NOW()),
    (2016394018121175046, '导出', @parentId, 3, 'exam:candidateExamProject:export', 6, 1, 1, NOW());

