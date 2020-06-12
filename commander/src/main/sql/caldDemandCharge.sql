DROP FUNCTION IF EXISTS calcDemandCharge;

DELIMITER $$
CREATE FUNCTION calcDemandCharge(new_timestamp BIGINT, new_mtu_id BIGINT, new_account_id BIGINT, new_energy DECIMAL(25,4), sampleSizeMin INT) RETURNS DECIMAL(25,4)
BEGIN
	
	DECLARE last_energy DECIMAL(25,4);  -- last cumulative value of the energy post
	DECLARE done INT DEFAULT FALSE;

-- CURSOR TO FIND THE LAST RECORD
	DECLARE LAST_CUR CURSOR FOR SELECT energy from energydata where account_id= new_account_id and mtu_id=new_mtu_id and timestamp = new_timestamp-(60 * sampleSizeMin) limit 1;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

	OPEN LAST_CUR;
	fetch LAST_CUR into  last_energy;
	CLOSE LAST_CUR;

	if (done = true) THEN
		-- Not enough data for the calculation
		RETURN 0;
	else 
		-- Calculate the average
		return (new_energy - last_energy) / sampleSizeMin;
	
	END IF; -- done = false

END;$$

