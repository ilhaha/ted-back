SET @parentId = 1909074467222810624;
-- 八大类，存储题目分类信息管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '八大类，存储题目分类信息管理', 1000, 2, '/exam/category', 'Category', 'exam/category/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 八大类，存储题目分类信息管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1909074467222810625, '列表', @parentId, 3, 'exam:category:list', 1, 1, 1, NOW()),
    (1909074467222810626, '详情', @parentId, 3, 'exam:category:detail', 2, 1, 1, NOW()),
    (1909074467222810627, '新增', @parentId, 3, 'exam:category:add', 3, 1, 1, NOW()),
    (1909074467222810628, '修改', @parentId, 3, 'exam:category:update', 4, 1, 1, NOW()),
    (1909074467222810629, '删除', @parentId, 3, 'exam:category:delete', 5, 1, 1, NOW()),
    (1909074467222810630, '导出', @parentId, 3, 'exam:category:export', 6, 1, 1, NOW());

