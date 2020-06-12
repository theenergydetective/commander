-- --------------------------------------------------------------------------------
-- Routine DDL
-- Note: comments before and after the routine body will not be stored by the server
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE FUNCTION `calcTOU`(energyPlan INT, season INT, d DATETIME) RETURNS int(11)
BEGIN

-- declare the return variable
	DECLARE done INT DEFAULT FALSE;
	declare touPeakType int;
	declare h INT;
	declare m INT;

-- cursor variables
	declare cur_peakType INT;
	declare cur_startHour INT;
	declare cur_startMin INT;
	declare cur_endHour INT;
	declare cur_endMinute INT;


-- CREATE THE CURSOR
    declare cur cursor FOR SELECT tou, touStartHour, touStartMinute, touEndHour, touEndMinute
	FROM energyPlanTOU
	where season_id = season and energyPlan_id = energyPlan;

	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;


	set touPeakType = 0;

	set h = HOUR(d);
	set m = MINUTE(d);

-- open the cursor
	open cur;

	start_loop: loop
		fetch cur into cur_peakType, cur_startHour, cur_startMin, cur_endHour, cur_endMinute;
		IF done = 1 THEN
			-- No more records to process
			LEAVE start_loop;
		END IF;

		-- Check the time and change the peak type as needed.
		-- Break the loop on the first match.
		if (h >= cur_startHour AND h <= cur_endHour) THEN
			if (!((h=cur_startHour AND m < cur_startMin) OR (h=cur_endHour AND m >= cur_endMinute))) THEN
				set touPeakType = cur_peakType;
				LEAVE start_loop;
			END IF;
		END IF;

	END loop;





-- close the cursor
	close cur;




-- OFF PEAK DEFAULT
RETURN touPeakType;
END