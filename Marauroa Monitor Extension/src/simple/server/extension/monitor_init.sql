CREATE  TABLE IF NOT EXISTS monitor (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `account_id` INT NOT NULL ,
  `enabled` INT NOT NULL ,
  PRIMARY KEY (`id`, `account_id`) );