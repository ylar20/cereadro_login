CREATE TABLE IF NOT EXISTS `cereadro`.`cer.user` (
  `id` mediumint not null auto_increment,
  `username` nvarchar(64) NOT NULL,
  `password` nvarchar(32) NOT NULL,
  `created_dtime`  datetime NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  PRIMARY KEY (`id`))