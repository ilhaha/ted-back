SET @parentId = 1909073987109220352;
-- 题库，存储各类题目及其分类信息管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '题库，存储各类题目及其分类信息管理', 1000, 2, '/examconnect/questionBank', 'QuestionBank', 'examconnect/questionBank/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 题库，存储各类题目及其分类信息管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1909073987109220353, '列表', @parentId, 3, 'examconnect:questionBank:list', 1, 1, 1, NOW()),
    (1909073987109220354, '详情', @parentId, 3, 'examconnect:questionBank:detail', 2, 1, 1, NOW()),
    (1909073987109220355, '新增', @parentId, 3, 'examconnect:questionBank:add', 3, 1, 1, NOW()),
    (1909073987109220356, '修改', @parentId, 3, 'examconnect:questionBank:update', 4, 1, 1, NOW()),
    (1909073987109220357, '删除', @parentId, 3, 'examconnect:questionBank:delete', 5, 1, 1, NOW()),
    (1909073987109220358, '导出', @parentId, 3, 'examconnect:questionBank:export', 6, 1, 1, NOW());

