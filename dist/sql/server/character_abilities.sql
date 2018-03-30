CREATE TABLE IF NOT EXISTS `character_abilities` (
  `charId`     INT UNSIGNED NOT NULL DEFAULT 0,
  `classIndex` INT UNSIGNED NOT NULL DEFAULT 0,
  `skillId`    INT UNSIGNED NOT NULL DEFAULT 0,
  `level`      INT UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`charId`, `classIndex`, `skillId`),
  FOREIGN KEY (`charId`) REFERENCES `characters` (`charId`)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);
