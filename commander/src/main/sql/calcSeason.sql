delimiter $$

CREATE FUNCTION `calcSeason`(tm int, td int, s1m int, s1d int, s2m int, s2d int,s3m int, s3d int,s4m int, s4d int) RETURNS int(3)
BEGIN
	if (tm < s1m) THEN RETURN 0;
	elseif (tm = s1m AND td <= s1d) THEN RETURN 0;
	elseif (tm < s2m) THEN RETURN 1;
	elseif (tm = s2m AND td <= s2d) THEN RETURN 1;
	elseif (tm < s3m) THEN RETURN 2;
	elseif (tm = s3m AND td <= s3d) THEN RETURN 2;
	elseif (tm < s4m) THEN RETURN 3;
	elseif (tm = s4m AND td <= s4d) THEN RETURN 3;
	END IF;

	RETURN 0;

END$$

