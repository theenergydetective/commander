CREATE DEFINER=`commander`@`%` PROCEDURE `addEnergyData`(new_timestamp BIGINT, new_mtu_id BIGINT, new_account_id BIGINT, new_energy DECIMAL(25,4), new_pf DECIMAL(6,2), new_voltage DECIMAL(6,2))
BEGIN
	DECLARE last_timestamp BIGINT;  -- last timestamp of the energy post
	DECLARE last_energy DECIMAL(25,4);  -- last cumulative value of the energy post
	DECLARE smooth_timestamp BIGINT;  -- smooth timestamp of the energy post
	DECLARE smooth_energy DECIMAL(25,4);  -- smooth cumulative value of the energy post
	DECLARE time_diff BIGINT; -- time difference between posts
	DECLARE num_min INT; -- number of minutes between posts
	DECLARE energyDiff DECIMAL(25,4);  -- the energy difference between posts
	DECLARE last_energyDiff DECIMAL(25,4);  -- the energy difference between posts
	DECLARE totalEnergyDiff DECIMAL(25,4);  -- the energy difference between posts
	DECLARE runningEnergyDiff DECIMAL(25,4);  -- the energy difference between posts
	DECLARE done INT DEFAULT FALSE;
    DECLARE smoothing TINYINT(1);

-- CURSOR TO FIND THE LAST RECORD
	DECLARE LAST_CUR CURSOR FOR SELECT timestamp, energy, energyDifference from energydata where account_id= new_account_id and mtu_id=new_mtu_id and timestamp <= new_timestamp order by timestamp desc limit 1;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

	OPEN LAST_CUR;
	fetch LAST_CUR into last_timestamp, last_energy, last_energyDiff;
	CLOSE LAST_CUR;

	if (done = true) THEN
		-- Zero out the energy difference as this is the first row.
		INSERT INTO energydata(timestamp, mtu_id, account_id, energy, energyDifference,  pf, voltage)
		values (new_timestamp, new_mtu_id, new_account_id, new_energy, 0, new_pf, new_voltage);

	elseif (last_timestamp != new_timestamp) THEN -- ignore data if its already been posted. Avoid the dupe error.
		
		-- Create the intermediate differences
		set time_diff = new_timestamp - last_timestamp;
		set num_min = time_diff / 60;
		set totalEnergyDiff = (new_energy - last_energy);
		set energyDiff = totalEnergyDiff / num_min;

		set runningEnergyDiff = 0;
	
		SET smooth_energy = last_energy;
		SET smooth_timestamp = last_timestamp;

		smooth_loop: loop
        
			-- Set the flag if we are smoothing or not
			SET smoothing = 0;
			if (num_min > 1) THEN
				SET smoothing = 1;
            END IF;
            
			-- INSERT A NEW ROW FOR EACH MISSING ROW.
			set smooth_energy = smooth_energy + energyDiff;
			set smooth_timestamp = smooth_timestamp + 60;
            

			if (num_min = 1) THEN
					-- Even out the last one to eliminate rounding error. The larger the timespan, the larger 
					-- this amount will be.
					SET energyDiff = totalEnergyDiff - runningEnergyDiff;
                    -- Adjust for validation
			END IF;

			if (energyDiff < -3000000 || energyDiff > 3000000) THEN
							SET energyDiff = last_energyDiff;	
			END	IF;
            
			set runningEnergyDiff = runningEnergyDiff + energyDiff;
            
            
            
            
			-- calculate averages
            
			INSERT INTO energydata(timestamp, mtu_id, account_id, energy, energyDifference, pf, voltage, avg5, avg10, avg15, avg20, avg30,smoothing)
			values (smooth_timestamp, new_mtu_id, new_account_id, smooth_energy, energyDiff, new_pf, new_voltage,
				calcDemandCharge(smooth_timestamp, new_mtu_id, new_account_id, smooth_energy, 5),
				calcDemandCharge(smooth_timestamp, new_mtu_id, new_account_id, smooth_energy, 10),
				calcDemandCharge(smooth_timestamp, new_mtu_id, new_account_id, smooth_energy, 15),
				calcDemandCharge(smooth_timestamp, new_mtu_id, new_account_id, smooth_energy, 20),
				calcDemandCharge(smooth_timestamp, new_mtu_id, new_account_id, smooth_energy, 30),
                smoothing
			);

			set num_min = num_min - 1;
			if (num_min = 0) THEN 
				LEAVE smooth_loop;
			end if;
		END loop;
		
	END IF; -- done = false
	

END