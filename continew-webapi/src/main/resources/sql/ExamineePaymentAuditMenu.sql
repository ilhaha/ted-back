SET @parentId = 1985531881845014528;
-- 考生缴费审核管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '考生缴费审核管理', 1000, 2, '/exam/examineePaymentAudit', 'ExamineePaymentAudit', 'exam/examineePaymentAudit/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 考生缴费审核管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1985531881845014529, '列表', @parentId, 3, 'exam:examineePaymentAudit:list', 1, 1, 1, NOW()),
    (1985531881845014530, '详情', @parentId, 3, 'exam:examineePaymentAudit:detail', 2, 1, 1, NOW()),
    (1985531881845014531, '新增', @parentId, 3, 'exam:examineePaymentAudit:add', 3, 1, 1, NOW()),
    (1985531881845014532, '修改', @parentId, 3, 'exam:examineePaymentAudit:update', 4, 1, 1, NOW()),
    (1985531881845014533, '删除', @parentId, 3, 'exam:examineePaymentAudit:delete', 5, 1, 1, NOW()),
    (1985531881845014534, '导出', @parentId, 3, 'exam:examineePaymentAudit:export', 6, 1, 1, NOW());

