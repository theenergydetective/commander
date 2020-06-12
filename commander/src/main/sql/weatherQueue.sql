CREATE
    ALGORITHM = UNDEFINED
    DEFINER = `commander`@`%`
    SQL SECURITY DEFINER
VIEW `weatherQueue` AS
    SELECT DISTINCT
        `wk`.`id` AS `id`,
        `wk`.`lat` AS `lat`,
        `wk`.`lon` AS `lon`,
        `wk`.`cityId` AS `cityId`
    FROM
        (`weatherKey` `wk`
            LEFT JOIN `weather_history` `wh` ON ((`wk`.`id` = `wh`.`weatherId`)))
    WHERE
        (ISNULL(`wh`.`timestamp`)
         OR ((UNIX_TIMESTAMP(NOW()) - `wh`.`timestamp`) > 3600))