-- 快速初始化社区数据库脚本
USE jiyi_social;

-- 如果表已存在则跳过
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

SOURCE backend/social-service/src/main/resources/db/schema.sql;
SOURCE backend/social-service/src/main/resources/db/data.sql;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
